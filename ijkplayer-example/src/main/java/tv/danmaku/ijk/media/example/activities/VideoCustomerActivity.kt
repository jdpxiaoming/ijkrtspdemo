package tv.danmaku.ijk.media.example.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import tv.danmaku.ijk.media.example.R
import tv.danmaku.ijk.media.ijkplayerview.widget.media.AndroidMediaController
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView.RENDER_TEXTURE_VIEW
import tv.danmaku.ijk.media.player.IjkMediaPlayer

/**
 * Author:zhangmengjie
 * Time:2021/4/14  16:25
 * Description:自定义参数播放器
 */
class VideoCustomerActivity : AppCompatActivity() {

    private var mMediaController: AndroidMediaController? = null
    lateinit var mVideoView: IjkVideoView
    private var mScreenShotIv: ImageView? = null

    private var mLastStartTime: Long = 0
    private var btnPlay:Button? = null
    private var btnPause:Button? = null
    private var mVideoPath: String? = null
    private val mVideoUri: Uri? = null
    private var mBackPressed = false

    private val formatMap = mutableMapOf<String, Long>()
    private val codecMap = mutableMapOf<String, Long>()
    private val swsMap = mutableMapOf<String, Long>()
    private val playerMap = mutableMapOf<String, Long>()
    private val formatStringMap = mutableMapOf<String, String>()

    /**
     * 暂停时间点.
     */
    private var mSeekPos = -1;


    companion object {
        const val TAG = "VideoCustomerActivity"

        private fun newIntent(context: Context?, videoPath: String?, videoTitle: String?): Intent {
            val intent = Intent(context, VideoCustomerActivity::class.java)
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
        setContentView(R.layout.activity_video_customer)
        // init UI
        // init player
        mVideoView = findViewById(R.id.video_view)
        mScreenShotIv = findViewById(R.id.image_view)
        btnPlay = findViewById(R.id.play)
        btnPause = findViewById(R.id.pause)

        init()
    }

    private fun init() {
        mVideoPath = "/storage/emulated/0/Ovopark_tv/Media/圆真真广告片无水印-1600834510472-1606204922412.mp4"

        // init player
        IjkMediaPlayer.loadLibrariesOnce(null)
        IjkMediaPlayer.native_profileBegin("libijkplayer.so")

        val actionBar = supportActionBar
        mMediaController = AndroidMediaController(this, false)
        mMediaController?.setSupportActionBar(actionBar)

        mVideoView.setOnInfoListener { mp, what, extra ->
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
            mVideoView.start()
            // 如果有seek操作，seek到指定位置.其实ijkplayer提供的pause在下层已经记录了seek再次start就会调用不需要额外的操作.
            if(mSeekPos > 0 ){
                mVideoView.postDelayed({
                    mVideoView.seekTo(mSeekPos)
                    //恢复未seek状态.
                    mSeekPos = -1;
                }, 200)
            }
        }

        btnPause?.setOnClickListener{
            // DO: 2021/2/3 pause the video and record current progress.
            mVideoView.let{
                mSeekPos = it.currentPosition;
                Log.i(TAG, "current pause pos is : $mSeekPos")
                it.pause()
            }
        }

        //设置自定义参数
        setMapValue()
        // prefer mVideoPath
        mVideoView.setRender(RENDER_TEXTURE_VIEW)
        mVideoView.setVideoPath(mVideoPath, IjkVideoView.IJK_TYPE_CUSTOMER_PLAY)
//        mVideoView.setVideoPath(mVideoPath, IjkVideoView.IJK_TYPE_LIVING_WATCH)

    }

    private fun setMapValue(){
//        formatMap["http-detect-range-support"] = 0
//        //增加rtmp打开速度. 没有缓存会黑屏1s.
//        formatMap["buffer_size"] = 1024
//        // 每处理一个packet之后刷新io上下文
//        formatMap["flush_packets"] = 1
//        formatMap["max-fps"] = 15
//        formatMap["fflags"] = 0
//        // 无限读
//        formatMap["infbuf"] = 1
//        // 设置播放前的最大探测时间 （100未测试是否是最佳值）
//        formatMap["analyzemaxduration"] = 90
//        // 播放前的探测Size,单位byte，默认是1M（1024x1024）, 改小一点会出画面更快
        formatMap["probesize"] = 1024 * 10
//
//        formatMap["infbuf"] = 1
//        //清空dns，因为多种协议播放会缓存协议导致播放h264后无法播放h265.
//        formatMap["dns_cache_clear"] = 1
//
//        // 设置是否开启环路过滤: 0开启，画面质量高，解码开销大，48关闭，画面质量差点，解码开销小
//        codecMap["skip_loop_filter"] = 48
//        //跳帧
//        codecMap["skip_frame"] = 0
//        //准备好了就播放.提高首开速度
//        playerMap["start-on-prepared"] = 1
//        // 是否开启预缓冲,直接禁用否则会有14s的卡顿缓冲时间.
//        playerMap["packet-buffering"] = 0
//        // 不额外优化（使能非规范兼容优化，默认值0 ）
//        playerMap["fast"] = 1
//        // 自动旋屏
//        playerMap["mediacodec-auto-rotate"] = 0
//        // 处理分辨率变化
//        playerMap["mediacodec-handle-resolution-change"] = 0
//        // 最大缓冲大小,单位kb，IJKPlayer拖动播放进度会导致重新请求数据，未使用已经缓冲好的数据，所以应该尽量控制缓冲区大小，减少不必要的数据损失
//        playerMap["max-buffer-size"] = 1024
//        // 视频的话，设置缓冲多少帧即开始播放
//        playerMap["min-frames"] = 5
//        // 最大缓存时长
//        playerMap["max_cached_duration"] = 300
//        //丢帧
//        playerMap["framedrop"] = 2
        //视频硬解
        playerMap["mediacodec"] = 1
        //音频硬解，开启后会导致视频播放时间延长2s，一开始声音会延时2s播放（后续会和视频同步）
//        playerMap["opensles"] = 1

        mVideoView.setCustomerValue(formatMap,codecMap,swsMap,playerMap,formatStringMap)

    }

    override fun onBackPressed() {
        mBackPressed = true
        super.onBackPressed()
    }


    override fun onDestroy() {
        super.onDestroy()
        mVideoView.stopPlayback()
        mVideoView.stopBackgroundPlay()
        mVideoView.release(true)
        IjkMediaPlayer.native_profileEnd()
    }
}