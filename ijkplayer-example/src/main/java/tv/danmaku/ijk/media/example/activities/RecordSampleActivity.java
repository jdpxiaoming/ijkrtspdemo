/*
 * Copyright (C) 2024 Bilibili
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.example.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IRenderView;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class RecordSampleActivity extends AppCompatActivity {
    private IjkVideoView mVideoView;
//    private TextView mStatusTextView;
//    private Button mRecordButton;
//    private boolean isRecording = false;
//    private String mRecordFilePath;
    private AndroidMediaController mMediaController;
    private TableLayout mHudView;

    private Button mStartRecordButton;
    private Button mStopRecordButton;
    private TextView mRecordStatusText;
    private TextView mRecordPathText;
    private RadioGroup mRecordModeGroup;
    private RadioButton mDirectRecordRadio;
    private RadioButton mTranscodeRecordRadio;

    private boolean isRecording = false;
    private String mRecordPath;
    private boolean mUseTranscode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sample);

        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);
        mHudView = (TableLayout) findViewById(R.id.hud_view);

        // 设置默认URL，这里使用的是一个直播流URL
        String url = "http://45.120.102.25:5591/rtsp/e6d12da7-d2d2-48b5-9dd9-a82630f1a750.flv";
//        url = "rtsp://45.120.102.25:5555/rtsp/e6d12da7-d2d2-48b5-9dd9-a82630f1a750";
//        url = "rtsp://221.181.75.22:5555/rtsp/8528596c-aaac-4452-a6d1-91feba53845d";//rtsp://hevc+pcma
        url = "https://ds-ctmu-ningbo-g1-006.ovopark.com:5582/rtsp/d5fe17b8-e5ad-4698-888d-d0489ff4f793.flv";//rtsp://hevc+pcma
        // 初始化播放器设置
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        mVideoView = findViewById(R.id.video_view);
        mStartRecordButton = findViewById(R.id.btn_start_record);
        mStopRecordButton = findViewById(R.id.btn_stop_record);
        mRecordStatusText = findViewById(R.id.text_record_status);
        mRecordPathText = findViewById(R.id.text_record_path);
        mRecordModeGroup = findViewById(R.id.radio_group_record_mode);
        mDirectRecordRadio = findViewById(R.id.radio_direct_record);
        mTranscodeRecordRadio = findViewById(R.id.radio_transcode_record);
//        mStatusTextView = findViewById(R.id.status_text);
//        mRecordButton = findViewById(R.id.record_button);

        //start play living。
        mVideoView.setMediaController(mMediaController);
        mVideoView.setHudView(mHudView);
        mVideoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        //打开opense,h264下有效.
        mVideoView.setAudioHardWare(true);
        mVideoView.setH265(true);
        mVideoView.setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        //set the headers properties in user-agent.
        mVideoView.setUserAgentStr("Android_Station_V1.1.1");

//        mVideoView.setVideoPath(url);
//        if (url != null)
        mVideoView.setVideoPath(url, IjkVideoView.IJK_TYPE_LIVING_WATCH);

        mVideoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
        // 开始播放
        mVideoView.start();

        // 设置录制按钮的点击事件
//        mRecordButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                toggleRecording();
//            }
//        });

        // 设置录制模式选择
        mRecordModeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mUseTranscode = (checkedId == R.id.radio_transcode_record);
            }
        });

        // 开始录制按钮
        mStartRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });

        // 停止录制按钮
        mStopRecordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });

        updateUI();
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, RecordSampleActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);

        context.startActivity(intent);
    }
    private void startRecord() {
        if (isRecording) {
            Toast.makeText(this, "已经在录制中", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建录制文件路径
        File recordDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_MOVIES), "IjkRecords");
        if (!recordDir.exists()) {
            recordDir.mkdirs();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String fileName = "record_" + sdf.format(new Date()) + ".mp4";
        mRecordPath = new File(recordDir, fileName).getAbsolutePath();

        // 获取IjkMediaPlayer实例
        IjkMediaPlayer player = mVideoView.getMediaPlayer();

        if (player != null && player instanceof IjkMediaPlayer) {
            int result;
            if (mUseTranscode) {
                // 使用转码录制
                result = player.startRecordTranscode(mRecordPath);
            } else {
                // 直接录制
                result = player.startRecord(mRecordPath);
            }

            if (result == 0) {
                isRecording = true;
                Toast.makeText(this, "开始录制", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "录制失败: " + result, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "播放器未就绪", Toast.LENGTH_SHORT).show();
        }

        updateUI();
    }

    private void stopRecord() {
        if (!isRecording) {
            Toast.makeText(this, "没有正在进行的录制", Toast.LENGTH_SHORT).show();
            return;
        }

        IjkMediaPlayer player = mVideoView.getMediaPlayer();
        if (player != null) {
            int result = player.stopRecord();
            if (result == 0) {
                isRecording = false;
                Toast.makeText(this, "录制已停止，文件保存在: " + mRecordPath, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "停止录制失败: " + result, Toast.LENGTH_SHORT).show();
            }
        }

        updateUI();
    }

    private void updateUI() {
        mStartRecordButton.setEnabled(!isRecording);
        mStopRecordButton.setEnabled(isRecording);
        mRecordStatusText.setText(isRecording ? "录制中..." : "未录制");
        mRecordPathText.setText(isRecording ? "保存路径: " + mRecordPath : "");
        mRecordModeGroup.setEnabled(!isRecording);
    }

    @Override
    protected void onDestroy() {
        if (isRecording) {
            stopRecord();
        }
        if (mVideoView != null) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
            IjkMediaPlayer.native_profileEnd();
        }
        super.onDestroy();
    }
}