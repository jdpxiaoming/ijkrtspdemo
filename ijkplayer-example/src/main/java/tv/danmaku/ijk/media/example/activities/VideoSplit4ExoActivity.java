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
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.SurfaceView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;

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


        player = new SimpleExoPlayer.Builder(this).build();
        player2 = new SimpleExoPlayer.Builder(this).build();
        player3 = new SimpleExoPlayer.Builder(this).build();
        player4 = new SimpleExoPlayer.Builder(this).build();
        player5 = new SimpleExoPlayer.Builder(this).build();
        player6 = new SimpleExoPlayer.Builder(this).build();

        // init player
        mVideoView =  findViewById(R.id.video_view);
        mVideoView2 = findViewById(R.id.video_view2);
        mVideoView3 = findViewById(R.id.video_view3);
        mVideoView4 = findViewById(R.id.video_view4);
        mVideoView5 = findViewById(R.id.video_view5);
        mVideoView6 = findViewById(R.id.video_view6);

        //binding views .
        player.setVideoSurfaceView(mVideoView);
        player2.setVideoSurfaceView(mVideoView2);
        player3.setVideoSurfaceView(mVideoView3);
        player4.setVideoSurfaceView(mVideoView4);
        player5.setVideoSurfaceView(mVideoView5);
        player6.setVideoSurfaceView(mVideoView6);


        // handle arguments
        mVideoPath =  "http://106.75.210.197:5581/rtsp/8d7143c1-64f7-44be-9bf4-8479e189dd70.flv";
        mVideoPath2 = "http://106.75.210.197:5581/rtsp/8d7143c1-64f7-44be-9bf4-8479e189dd70.flv";
        mVideoPath3 = "http://106.75.254.198:5581/rtsp/fdd98321-00f3-472d-bbfa-4d8841b557f8.flv";
        mVideoPath4 = "http://106.75.210.197:5581/rtsp/8d7143c1-64f7-44be-9bf4-8479e189dd70.flv";
        mVideoPath5 = "http://106.75.210.197:5581/rtsp/8d7143c1-64f7-44be-9bf4-8479e189dd70.flv";
        mVideoPath6 = "http://106.75.254.198:5581/rtsp/fdd98321-00f3-472d-bbfa-4d8841b557f8.flv";
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
//        if (mVideoPath != null){
           /* mVideoView.setVideoPath(mVideoPath, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView.start();

            mVideoView2.setVideoPath(mVideoPath2, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView2.start();

            mVideoView3.setVideoPath(mVideoPath3, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView3.start();

            mVideoView4.setVideoPath(mVideoPath4, IjkVideoView.IJK_TYPE_LIVING_WATCH);
            mVideoView4.start();*/
//        }

        MediaItem mediaItem = MediaItem.fromUri(mVideoPath);
        MediaItem mediaItem2 = MediaItem.fromUri(mVideoPath2);
        MediaItem mediaItem3 = MediaItem.fromUri(mVideoPath3);
        MediaItem mediaItem4 = MediaItem.fromUri(mVideoPath4);
        MediaItem mediaItem5 = MediaItem.fromUri(mVideoPath5);
        MediaItem mediaItem6 = MediaItem.fromUri(mVideoPath6);

        player.setMediaItem(mediaItem);
        player2.setMediaItem(mediaItem2);
        player3.setMediaItem(mediaItem3);
        player4.setMediaItem(mediaItem4);
        player5.setMediaItem(mediaItem5);
        player6.setMediaItem(mediaItem6);

        player.prepare();
        player2.prepare();
        player3.prepare();
        player4.prepare();
        player5.prepare();
        player6.prepare();


        player.play();
        player2.play();
        player3.play();
        player4.play();
        player5.play();
        player6.play();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        IjkMediaPlayer.native_profileEnd();
    }
}
