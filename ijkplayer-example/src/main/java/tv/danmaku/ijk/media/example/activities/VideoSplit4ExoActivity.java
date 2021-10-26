/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
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
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.SurfaceView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.util.Log;
import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.content.RecentMediaStorage;
import tv.danmaku.ijk.media.ijkplayerview.utils.Settings;
import tv.danmaku.ijk.media.ijkplayerview.widget.IjkPrettyVideoView;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 *
 * 四分屏测试类.(Exo)
 * ijkplayer 视频播放类.
 * @author caixingming 2021/09/08
 */
public class VideoSplit4ExoActivity extends AppCompatActivity {
    private static final String TAG = "VideoSplit4ExoActivity";

    private String mVideoPath,mVideoPath2,mVideoPath3,mVideoPath4,mVideoPath5,mVideoPath6 ;

    private AndroidMediaController mMediaController;
    private SurfaceView mVideoView,mVideoView2,mVideoView3,mVideoView4,mVideoView5,mVideoView6;
    private SimpleExoPlayer player,player2,player3,player4,player5,player6;
    private RtspMediaSource mediaSources;//配置rtsp用.

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoSplit4ExoActivity.class);
        intent.putExtra("videoPath", videoPath);
        intent.putExtra("videoTitle", videoTitle);
        return intent;
    }

    public static void intentTo(Context context, String videoPath, String videoTitle) {
        context.startActivity(newIntent(context, videoPath, videoTitle));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exo_split_layout);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");


        // handle arguments
        mVideoPath =  "http://113.31.119.60:5581/rtsp/f5b7164f-af85-4731-b54c-6c4aad8a2a4a.flv";
        mVideoPath2 = "rtmp://113.31.119.60:2935/rtsp/f5b7164f-af85-4731-b54c-6c4aad8a2a4a";//rtsp -aac h264
//        mVideoPath3 = "rtsp://113.31.119.60:5555/rtsp/35d1ebac-da5c-49df-856e-e7ef6eae7024";
//        mVideoPath4 = "http://113.31.119.60:5581/rtsp/b838b687-bd65-4e16-8996-114f6bc31d4d.flv";
//        mVideoPath5 = "http://106.75.210.197:5581/rtsp/e7069f2d-e181-413e-b71c-a326d2e54267.flv";
//        mVideoPath6 = "http://106.75.210.197:5581/rtsp/03de5042-87e7-4853-b659-98dbf012dc1e.flv";

        // init player
        mVideoView =  findViewById(R.id.video_view);
        mVideoView2 = findViewById(R.id.video_view2);
//        mVideoView3 = findViewById(R.id.video_view3);
//        mVideoView4 = findViewById(R.id.video_view4);
//        mVideoView5 = findViewById(R.id.video_view5);
//        mVideoView6 = findViewById(R.id.video_view6);


        boolean preferExtensionDecoders = true;
//          intent.getBooleanExtra(PREFER_EXTENSION_DECODERS_EXTRA, false);
        RenderersFactory renderersFactory =new DefaultRenderersFactory(/* context= */ this)
                .setExtensionRendererMode(DefaultRenderersFactory.EXTENSION_RENDERER_MODE_ON);

        player = new SimpleExoPlayer.Builder(this,renderersFactory).build();
        player2 = new SimpleExoPlayer.Builder(this,renderersFactory).build();
//        player3 = new SimpleExoPlayer.Builder(this).build();
//        player4 = new SimpleExoPlayer.Builder(this).build();
//        player5 = new SimpleExoPlayer.Builder(this).build();
//        player6 = new SimpleExoPlayer.Builder(this).build();

        //binding views .
        player.setVideoSurfaceView(mVideoView);
        player2.setVideoSurfaceView(mVideoView2);
//        player3.setVideoSurfaceView(mVideoView3);
//        player4.setVideoSurfaceView(mVideoView4);
//        player5.setVideoSurfaceView(mVideoView5);
//        player6.setVideoSurfaceView(mVideoView6);



//        mVideoPath2 = mVideoPath;
//        mVideoPath3 = mVideoPath;
//        mVideoPath4 = mVideoPath;
        if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }
        // init UI
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        MediaItem mediaItem = MediaItem.fromUri(mVideoPath);
        MediaItem mediaItem2 = MediaItem.fromUri(mVideoPath2);
//        MediaItem mediaItem3 = MediaItem.fromUri(mVideoPath3);
//        MediaItem mediaItem4 = MediaItem.fromUri(mVideoPath4);
//        MediaItem mediaItem5 = MediaItem.fromUri(mVideoPath5);
//        MediaItem mediaItem6 = MediaItem.fromUri(mVideoPath6);

        player.setMediaItem(mediaItem);
        player2.setMediaItem(mediaItem2);
//        mediaSources = createTopLevelMediaSources();
        // TODO: 2021/10/16 使用rtsp支持的meidasource来播放数据.
//        player2.setMediaSource(mediaSources);
//        player3.setMediaItem(mediaItem3);
//        player4.setMediaItem(mediaItem4);
//        player5.setMediaItem(mediaItem5);
//        player6.setMediaItem(mediaItem6);

        player.prepare();
        player2.prepare();
//        player3.prepare();
//        player4.prepare();
//        player5.prepare();
//        player6.prepare();


        player.play();
        player2.play();
//        player3.play();
//        player4.play();
//        player5.play();
//        player6.play();
    }


    private RtspMediaSource createTopLevelMediaSources() {
        Log.i(TAG,"createTopLevelMediaSources");
        MediaItem mediaItem2 = MediaItem.fromUri(mVideoPath2);
        return new RtspMediaSource.Factory()
                .setForceUseRtpTcp(true)
                .createMediaSource(mediaItem2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }
}
