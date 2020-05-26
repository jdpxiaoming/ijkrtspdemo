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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.content.RecentMediaStorage;
import tv.danmaku.ijk.media.example.fragments.TracksFragment;
import tv.danmaku.ijk.media.ijkplayerview.utils.Settings;
import tv.danmaku.ijk.media.ijkplayerview.widget.IjkPrettyVideoView;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.MeasureHelper;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.misc.ITrackInfo;

/**
 * ijkplayer 视频播放类.
 */
public class VideoLayoutActivity extends AppCompatActivity{
    private static final String TAG = "VideoLayoutActivity";

    private String mVideoPath ;

    private AndroidMediaController mMediaController;
    private IjkPrettyVideoView mVideoView;
    private ImageView mScreenShotIv;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoLayoutActivity.class);
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
        setContentView(R.layout.activity_video_layout);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        // handle arguments
        //h265测试.rtsp://47.104.185.91:5555/rtsp/968e6862-96a2-4a5d-afb3-0594784d5af4
        mVideoPath = "https://1ovopark-record.oss-cn-shanghai.aliyuncs.com/3dcde46f-9265-40be-8185-915e7653409c.mp4";

        if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }
        // init UI
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        // init player
        mVideoView = (IjkPrettyVideoView) findViewById(R.id.video_view);
        mScreenShotIv = (ImageView) findViewById(R.id.image_view);
        mVideoView.setMediaController(mMediaController);

        // prefer mVideoPath
        if (mVideoPath != null){
            mVideoView.setVideoPath(mVideoPath, IjkVideoView.IJK_TYPE_LIVING_LOW_DELAY);
            mVideoView.start();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = mVideoView.getBitmap();
                if(null != bmp){
                    Log.i(TAG," screenshot success !");
                    mScreenShotIv.setImageBitmap(bmp);
                }else{
                    Log.i(TAG," screenshot fail is NULL !");
                }
            }
        },10*1000);
    }
}
