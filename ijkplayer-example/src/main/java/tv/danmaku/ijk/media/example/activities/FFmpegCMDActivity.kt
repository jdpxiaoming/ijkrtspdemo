package tv.danmaku.ijk.media.example.activities

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.PermissionChecker
import com.jdpxiaoming.ffmpeg_cmd.FFmpegFactory
import com.jdpxiaoming.ffmpeg_cmd.FFmpegUtil
import tv.danmaku.ijk.media.example.databinding.ActivityMainBinding
import java.io.File

/**
 * this is a demo for how to use ffmpegtools
 * https://github.com/jdpxiaoming/FFmpegTools
 *
 * @author jdpxiaoming 2020/12/01
 */
class FFmpegCMDActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        @JvmStatic
        fun intentTo(DemoActivity: DemoActivity, s: String, s2: String) {
            fun intentTo(context: Context, videoPath: String?, videoTitle: String?) {
                context.startActivity(
                    VideoSplit4ExoActivity.newIntent(
                        context,
                        videoPath,
                        videoTitle
                    )
                )
            }
        }

        private const val TAG = "MainActivity"
    }


    private val requestPermissionCode = 10086
    private val requestPermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Example of a call to a native method
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                requestPermissions(requestPermission, requestPermissionCode)
            }
        }

        initViews();
    }



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1001 ->                 // 1001的请求码对应的是申请打电话的权限
                // 判断是否同意授权，PERMISSION_GRANTED 这个值代表的是已经获取了权限
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@FFmpegCMDActivity, "你同意授权了", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@FFmpegCMDActivity, "你不同意授权", Toast.LENGTH_LONG).show()
                }
        }
    }



    /**
     * 初始化urls.
     */
    private fun initViews() {
        val inputPath = "http://ds-ctmu-ningbo-g1-001.ovopark.com:5581/rtsp/3ee68d4b-7859-4343-bfa1-99d354100636.flv"
        val outputPath = Environment.getExternalStorageDirectory().absolutePath + "/Download/1118.mp4"
        val output = File(outputPath)
        if (output.exists()) {
            output.delete()
        }
        //cmds for ffmpeg flv->mp4.
        var commands: Array<String?>? = null // FFmpegFactory.buildRtsp2Mp4(inputPath,outputPath);
        commands = FFmpegFactory.buildRtsp2Mp4(inputPath,outputPath);//FFmpegFactory.buildFlv2Mp4(inputPath, outputPath)

        commands?.let {
            var jpsb:StringBuffer = StringBuffer();
            for(str in commands){
                jpsb.append("$str ")
            }

            binding.cmdEt.setText(jpsb.toString());
        }

        //转化flv的地址.
        binding.flvEt.setText("http://ds-ctmu-ningbo-g1-001.ovopark.com:5581/rtsp/3ee68d4b-7859-4343-bfa1-99d354100636.flv");
        //转化rtsp（hevc)的地址
        binding.rtspEt.setText("http://ds-ctmu-ningbo-g1-001.ovopark.com:5581/rtsp/3ee68d4b-7859-4343-bfa1-99d354100636.flv");
    }


    /**
     * 停止命令.
     * @param view
     */
    fun stopRun(view: View?) {
        FFmpegUtil.instance?.stopTask()
    }


    /**
     * 视频转码 flv->mp4.
     * @param view
     */
    fun videoTransform(view: View?) {
        val inputPath = "http://ds-ctmu-ningbo-g1-001.ovopark.com:5581/rtsp/3ee68d4b-7859-4343-bfa1-99d354100636.flv"
        val outputPath = Environment.getExternalStorageDirectory().absolutePath + "/Download/20251207rtsp01.mp4"
        val output = File(outputPath)
        if (output.exists()) {
            output.delete()
        }
        //cmds for ffmpeg flv->mp4.
        var commands: Array<String?>? = null // FFmpegFactory.buildRtsp2Mp4(inputPath,outputPath);
//        commands = FFmpegFactory.buildRtsp2Mp4(inputPath, outputPath)
        commands = FFmpegFactory.buildFlv2Mp4(inputPath, outputPath)
        Log.e(TAG, "videoTransform cmds:===>> " + commands?.joinToString(" "))
        FFmpegUtil.instance?.enQueueTask(commands, 0, object : FFmpegUtil.Callback {
            override fun onStart() {
                Log.i(TAG, " onStart2 # ")
            }

            override fun onFailure() {
                Log.i(TAG, " onFailure2 # ")
                Toast.makeText(this@FFmpegCMDActivity, "transcode failed2 ,please check your input file2 !", Toast.LENGTH_LONG).show()
            }

            override fun onComplete() {
                Log.i(TAG, " onComplete2 # ")
                Toast.makeText(this@FFmpegCMDActivity, "transcode successful2!", Toast.LENGTH_LONG).show()
            }

            override fun onProgress(progress: Float) {
                Log.i(TAG, " onProgress2 # $progress")
            }
        })
    }



    /**
     * 添加视频水印，并输出.
     */
    fun addWaterMark(view: View?){
        Log.i(TAG,"addWaterMark~start~!")
        val inputVideo = "/storage/emulated/0/OvoparkVideo/video_1636700501191.mp4";
        val inputWaterPic = "/storage/emulated/0/OvoparkVideo/tuya.png"
        val output = File(Environment.getExternalStorageDirectory(), "/Download/output65.mp4").absolutePath

        val commands: Array<String?>? = FFmpegFactory.addWaterMark(inputWaterPic,inputVideo, output)
        var commandStr = ""
        commands?.forEach { commandStr = "$commandStr $it" }
        Log.e(TAG,"FFmpeg cmds:===>> $commandStr")

        FFmpegUtil.instance?.enQueueTask(commands, 0, object : FFmpegUtil.Callback {
            override fun onStart() {
                Log.i(TAG, " onStart2 # ")
            }

            override fun onFailure() {
                Log.i(TAG, " onFailure2 # ")
                Toast.makeText(this@FFmpegCMDActivity, "transcode failed2 ,please check your input file2 !", Toast.LENGTH_LONG).show()
            }

            override fun onComplete() {
                Log.i(TAG, " onComplete2 # ")
                Toast.makeText(this@FFmpegCMDActivity, "transcode successful2!", Toast.LENGTH_LONG).show()
            }

            override fun onProgress(progress: Float) {
                Log.i(TAG, " onProgress2 # $progress")
            }
        })

    }
}