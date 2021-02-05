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
package tv.danmaku.ijk.media.example.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import tv.danmaku.ijk.media.example.R
import tv.danmaku.ijk.media.example.application.PApplication
import tv.danmaku.ijk.media.ijkplayerview.widget.IjkPrettyVideoView
import tv.danmaku.ijk.media.ijkplayerview.widget.media.AndroidMediaController
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * 采用本地代理方法实现mp4文件边下边播功能.
 * 使用开源框架 {https://github.com/danikula/AndroidVideoCache}.
 * ijkplayer 视频播放类.
 * author : poe.Cai https://github.com/jdpxiaoming
 * date   : 2020/5/28 10:31
 */
class VideoCacheActivity : AppCompatActivity() {

    private var mMediaController: AndroidMediaController? = null
    private var mVideoView: IjkPrettyVideoView? = null
    private var mScreenShotIv: ImageView? = null

    private var mLastStartTime: Long = 0
    private var btnPlay:Button? = null;
    private var btnPause:Button? = null;
    private var mVideoPath: String? = null
    private val mVideoUri: Uri? = null
    private var mBackPressed = false;

    /**
     * 暂停时间点.
     */
    private var mSeekPos = -1;


    companion object {
        const val TAG = "VideoCacheActivity"

        private fun newIntent(context: Context?, videoPath: String?, videoTitle: String?): Intent {
            val intent = Intent(context, VideoCacheActivity::class.java)
            intent.putExtra("videoPath", videoPath)
            intent.putExtra("videoTitle", videoTitle)
            return intent
        }

        @JvmStatic
        fun intentTo(context: Context, videoPath: String?, videoTitle: String?) {
            context.startActivity(newIntent(context, videoPath, videoTitle))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_cache)
        // init UI
        // init player
        mVideoView = findViewById<View>(R.id.video_view) as IjkPrettyVideoView
        mScreenShotIv = findViewById<View>(R.id.image_view) as ImageView
        btnPlay = findViewById(R.id.play) as Button
        btnPause = findViewById(R.id.pause) as Button

        init()
    }

    private fun init() {
        mVideoPath = "http://ovopark.oss-cn-hangzhou.aliyuncs.com/video_video_1612540142579.mp4"
        val proxy = PApplication.getProxy(this)
        val proxyUrl = proxy.getProxyUrl(mVideoPath)
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")

        val actionBar = supportActionBar
        mMediaController = AndroidMediaController(this, false)
        mMediaController?.setSupportActionBar(actionBar)

//        mVideoView?.setMediaController(mMediaController)
        mVideoView?.setOnInfoListener { mp, what, extra ->
            Log.e(TAG, "onInfo#position: " + mp.currentPosition + " what: " + what + " extra: " + extra)
            if (IjkMediaPlayer.MEDIA_ERROR_NO_STREAM == what) {
                val takeTime = SystemClock.currentThreadTimeMillis() - mLastStartTime
                Log.i(TAG, "加载视频prepare耗时#=====================> $takeTime ms")
            }
            false
        }
        mLastStartTime = SystemClock.currentThreadTimeMillis()
        Log.i(TAG, "start play ~~ #  $mLastStartTime")


        btnPlay?.setOnClickListener {
            mVideoView?.start()
            // 如果有seek操作，seek到指定位置.其实ijkplayer提供的pause在下层已经记录了seek再次start就会调用不需要额外的操作.
            if(mSeekPos > 0 ){
                mVideoView?.postDelayed({
                    mVideoView?.seekTo(mSeekPos)
                    //恢复未seek状态.
                    mSeekPos = -1;
                }, 200)
            }
        }

        btnPause?.setOnClickListener{
            // DO: 2021/2/3 pause the video and record current progress.
            mVideoView?.let{
                mSeekPos = it.currentPosition;
                Log.i(TAG, "current pause pos is : $mSeekPos")
                it.pause()
            }
        }

        //设置视频旋转角度.
        mVideoView?.isIgnoreRotation = false;
        mVideoView?.setVideoRotationDegree(0)
        // prefer mVideoPath
        mVideoView?.setVideoPath(proxyUrl, IjkVideoView.IJK_TYPE_HTTP_PLAY)
    }

    override fun onBackPressed() {
        mBackPressed = true
        super.onBackPressed()
    }


    override fun onDestroy() {
        super.onDestroy()
        IjkMediaPlayer.native_profileEnd()
    }
}