
package tv.danmaku.ijk.media.example.activities;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import tv.danmaku.ijk.media.example.R;

/**
 * demo 入口.
 */
public class DemoActivity extends AppCompatActivity  {
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
}
