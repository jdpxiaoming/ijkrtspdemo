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
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.content.RecentMediaStorage;
import tv.danmaku.ijk.media.ijkplayerview.utils.Settings;
import tv.danmaku.ijk.media.ijkplayerview.widget.IjkPrettyVideoView;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.AndroidMediaController;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 *
 * 四分屏测试类.
 * ijkplayer 视频播放类.
 * @author caixingming 2021/09/08
 */
public class VideoSplit4Activity extends AppCompatActivity {
    private static final String TAG = "VideoSplit4Activity";

    private String mVideoPath,mVideoPath2,mVideoPath3,mVideoPath4 ;

    private AndroidMediaController mMediaController;
    private IjkPrettyVideoView mVideoView,mVideoView2,mVideoView3,mVideoView4;
    private SharedPreferences mSharedPreferences;

    public static Intent newIntent(Context context, String videoPath, String videoTitle) {
        Intent intent = new Intent(context, VideoSplit4Activity.class);
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
        setContentView(R.layout.activity_video_split_layout);

        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");

        //设置启用exoPlayer.
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String key = this.getString(tv.danmaku.ijk.media.ijkplayerview.R.string.pref_key_player);
        mSharedPreferences.edit().putString(key,String.valueOf(Settings.PV_PLAYER__IjkExoMediaPlayer)).apply();

        // init player
        mVideoView = (IjkPrettyVideoView) findViewById(R.id.video_view);
        mVideoView2 = (IjkPrettyVideoView) findViewById(R.id.video_view2);
        mVideoView3 = (IjkPrettyVideoView) findViewById(R.id.video_view3);
        mVideoView4 = (IjkPrettyVideoView) findViewById(R.id.video_view4);

        mVideoView.setMediaController(mMediaController);
        mVideoView.setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        mVideoView.setTimeout(2*1000*1000);

        mVideoView2.setMediaController(mMediaController);
        mVideoView2.setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        mVideoView2.setTimeout(2*1000*1000);

        mVideoView3.setMediaController(mMediaController);
        mVideoView3.setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        mVideoView3.setTimeout(2*1000*1000);

        mVideoView4.setMediaController(mMediaController);
        mVideoView4.setLogLevel(IjkMediaPlayer.IJK_LOG_DEBUG);
        mVideoView4.setTimeout(2*1000*1000);


        // handle arguments
        mVideoPath =  "http://202.umbrellary.com:1000/4K_2.mp4";
        mVideoPath2 = "http://202.umbrellary.com:1000/4K_2.mp4";
        mVideoPath3 = "http://106.75.210.197:5581/rtsp/aca6be36-a0d6-4694-855a-be600be6b198.flv";
        mVideoPath4 = "http://106.75.210.197:5581/rtsp/eff21ca3-79c1-4379-b434-f9c3884c7d44.flv";

        if (!TextUtils.isEmpty(mVideoPath)) {
            new RecentMediaStorage(this).saveUrlAsync(mVideoPath);
        }
        // init UI
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);

        //auto loading video .
//        mVideoView.setAutoLoading(true);
//        mVideoView2.setAutoLoading(true);
//        mVideoView3.setAutoLoading(true);
//        mVideoView4.setAutoLoading(true);

        //使用Exo-播放器.
//        mVideoView.set
        // prefer mVideo
        // +
        //
        // Path
        if (mVideoPath != null){
            mVideoView.setVideoPath(mVideoPath, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView.start();

            mVideoView2.setVideoPath(mVideoPath2, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView2.start();

           /* mVideoView3.setVideoPath(mVideoPath3, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView3.start();

            mVideoView4.setVideoPath(mVideoPath4, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView4.start();*/
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }
}
