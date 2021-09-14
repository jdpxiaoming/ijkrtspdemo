
package tv.danmaku.ijk.media.example.activities;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import tv.danmaku.ijk.media.example.R;

/**
 * demo 入口.
 */
public class DemoActivity extends AppCompatActivity {
    private static final String TAG = "DemoActivity";

    private int REQUEST_PERMISSION_CODE = 1100;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_demo_list);

        // TO: 2021/2/4 申请文件权限.
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, PERMISSIONS_STORAGE, REQUEST_PERMISSION_CODE);
            }
        }
    }

    public void goVideoPlay(View view) {
        VideoActivity.intentTo(this,"","video play");
    }

    public void goVideoLayout(View view) {
        VideoLayoutActivity.intentTo(this,"","video Layout loading .");
    }

    public void goVideoCache(View view) {
        VideoCacheActivity.intentTo(this,"","video cache play with local proxy .");
    }

    public void goVideoCache2(View view) {
        VideoCache2Activity.intentTo(this,"","video cache play with local proxy .");
    }

    public void goCustomerVideo(View view) {
        VideoCustomerActivity.intentTo(this,"","video cache play with local proxy .");
    }

    public void goVideoSplit4(View view) {
        VideoSplit4Activity.intentTo(this,"","video splite4screen play .");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION_CODE) {
            for (int i = 0; i < permissions.length; i++) {
                Log.i("MainActivity", "申请的权限为：" + permissions[i] + ",申请结果：" + grantResults[i]);
            }
        }
    }

    public void goVideoSplit4Exo(View view) {
        VideoSplit4ExoActivity.intentTo(this,"","video splite4 exo screen play .");
    }
}
