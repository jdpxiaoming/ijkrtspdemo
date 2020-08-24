package tv.danmaku.ijk.media.example.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jdpxiaoming.ffmpeg_cmd.FFmpegCmd;
import com.jdpxiaoming.ffmpeg_cmd.FFmpegFactory;
import com.jdpxiaoming.ffmpeg_cmd.FFmpegUtil;
import java.io.File;

import tv.danmaku.ijk.media.example.R;

public class VideoDownloadActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private int requestPermissionCode = 10086;
    private String[] requestPermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static void intentTo(Context context) {
        Intent intent = new Intent(context, VideoDownloadActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        // Example of a call to a native method
        // Example of a call to a native method
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            if(PermissionChecker.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED){
                requestPermissions(requestPermission,requestPermissionCode);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1001:
                // 1001的请求码对应的是申请打电话的权限
                // 判断是否同意授权，PERMISSION_GRANTED 这个值代表的是已经获取了权限
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(VideoDownloadActivity.this, "你同意授权了", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(VideoDownloadActivity.this, "你不同意授权", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    /**
     * 视频转码 flv->mp4.
     * @param view
     */
    public void videoTransform(View view) {
        String inputPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/58.flv";
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/807.mp4";
        File input =new File(inputPath);
        if(!input.exists()){
            Toast.makeText(VideoDownloadActivity.this, "/Download/58.flv not found!", Toast.LENGTH_LONG).show();
            return;
        }
        File output =new File(outputPath);
        if(output.exists()){
            output.delete();
        }
        //cmds for ffmpeg flv->mp4.
//        inputPath ="http://106.14.218.234:5581/rtsp/0d427a62-3f7b-44e6-b81f-e891ba79f994/live.flv";
        inputPath = "http://47.105.240.204:5580/55000000000000000011100008200000-2.flv";

//        String[] commands = FFmpegFactory.buildRtsp2Mp4(inputPath,outputPath);
        String[] commands = FFmpegFactory.buildFlv2Mp4(inputPath,outputPath);

        FFmpegUtil.getInstance().enQueueTask(commands, 0,new FFmpegUtil.onCallBack() {
            @Override
            public void onStart() {

                Log.i(TAG," onStart # ");
            }

            @Override
            public void onFailure() {
                Log.i(TAG," onFailure # ");
                Toast.makeText(VideoDownloadActivity.this, "transcode failed ,please check your input file !", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                Log.i(TAG," onComplete # ");
                Toast.makeText(VideoDownloadActivity.this, "transcode successful!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(float progress) {
                Log.i(TAG," onProgress # "+progress);
            }
        });
    }

    /**
     * 停止命令.
     * @param view
     */
    public void stopRun(View view) {
        FFmpegUtil.getInstance().stopTask();
    }


    /**
     * 测试 cmd 下载flv.
     * @param view
     */
    public void dumpFlvCmd(View view){
        String inputPath = "http://47.105.240.204:5580/55000000000000000011100024200000-0.flv";
        String outputPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Download/819.mp4";
        File output =new File(outputPath);
        if(output.exists()){
            output.delete();
        }

        //cmds for ffmpeg flv->mp4.
//        String[] commands = FFmpegFactory.buildRtsp2Mp4(inputPath,outputPath);
        String[] commands = FFmpegFactory.buildFlv2Mp4(inputPath,outputPath);

        FFmpegUtil.getInstance().enQueueTask(commands, 0,new FFmpegUtil.onCallBack() {
            @Override
            public void onStart() {

                Log.i(TAG," onStart # ");
            }

            @Override
            public void onFailure() {
                Log.i(TAG," onFailure # ");
                Toast.makeText(VideoDownloadActivity.this, "transcode failed ,please check your input file !", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onComplete() {
                Log.i(TAG," onComplete # ");
                Toast.makeText(VideoDownloadActivity.this, "transcode successful!", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onProgress(float progress) {
                Log.i(TAG," onProgress # "+progress);
            }
        });

    }

    public void dumpFlv(View view) {
        Log.i(TAG," dump Flv and save mp4！");
        //flv测试ok.
        String input = "http://47.105.240.204:5580/55000000000000000011100019400000-3.flv";//flv测试流.
        //1. h265+pcma 测试failed 猜测是音频格式pcma无法封装成mp4格式.
//        input = "rtsp://47.108.81.159:5555/rtsp/4d598b02-e3f7-4499-a55a-8edbe13074cb";//rtsp 测试流
        //2. h264+aac 测试ok.
//        input = "http://118.31.174.18:5581/rtsp/662e1f9e-a06e-4e0a-9b73-a6d07620b3a4/live.flv";
        String output = new File(Environment.getExternalStorageDirectory(),"/poe/output86.mp4").getAbsolutePath();
        FFmpegCmd.dump_stream(input , output);
    }
}
