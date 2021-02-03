
package tv.danmaku.ijk.media.example.activities;


import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import tv.danmaku.ijk.media.example.R;

/**
 * demo 入口.
 */
public class DemoActivity extends AppCompatActivity {
    private static final String TAG = "DemoActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_demo_list);
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
}
