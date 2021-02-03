package tv.danmaku.ijk.media.ijkplayerview.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Type;

import tv.danmaku.ijk.media.ijkplayerview.R;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IMediaController;
import tv.danmaku.ijk.media.ijkplayerview.widget.media.IjkVideoView;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * comment:
 * author : poe.Cai
 * date   : 2020/5/25 17:17
 */
public class IjkPrettyVideoView extends FrameLayout
        implements IjkMediaPlayer.OnInfoListener, IjkMediaPlayer.OnErrorListener,
                IjkMediaPlayer.OnCompletionListener{

    private static final String TAG = " IjkPrettyVideoView";
    private IMediaPlayer.OnCompletionListener mOnCompletionListener;
    private IMediaPlayer.OnErrorListener mOnErrorListener;
    private IMediaPlayer.OnInfoListener mOnInfoListener;
    private int mLoadingColor = Color.rgb(255,255,255);
    private int mRetryColor = Color.rgb(255,255,0);
    private TextView mHintTv;
    private IjkVideoView mIjkVideoView;
    /**
     * 固定的宽高比 16:9
     */
    private static float ASPECT = 0.5625f;
    private boolean mAspectEnable = false;
    private ITextClickListener mTextClickListener;
    private ILoadingListener mLoadingListener;

    public IjkPrettyVideoView(Context context) {
        this(context,null);
    }

    public IjkPrettyVideoView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public IjkPrettyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs,defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public IjkPrettyVideoView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs,defStyleAttr);
    }

    /**
     * init attributes .
     * @param attrs
     */
    private void init(AttributeSet attrs,int defStyle) {
        if (!this.isInEditMode()) {
            LayoutInflater.from(getContext()).inflate(R.layout.layout_ijk_pretty_video_view,this,true);

            TypedArray typedArray = getContext().obtainStyledAttributes(attrs,R.styleable.IjkPrettyVideoView, defStyle, 0);
            mLoadingColor = typedArray.getColor(R.styleable.IjkPrettyVideoView_loading_color,mLoadingColor);
            mRetryColor = typedArray.getColor(R.styleable.IjkPrettyVideoView_retry_color,mRetryColor);
            typedArray.recycle();

        }
    }

    /**
     *  return the inner {@link IjkVideoView}
     * @return
     */
    public IjkVideoView getVideoView() {
        return mIjkVideoView;
    }

    public void setTextClickListener(ITextClickListener textClickListener) {
        this.mTextClickListener = textClickListener;
    }

    public void setLoadingListener(ILoadingListener mLoadingListener) {
        this.mLoadingListener = mLoadingListener;
    }

    /**
     * get the video current screenshot.
     * @return
     */
    public Bitmap getBitmap(){
        if(null != mIjkVideoView) return mIjkVideoView.getBitmap();

        return null;
    }

    /**
     * {@link IjkVideoView#setLogLevel(int)}
     * @param logLevel
     */
    public void setLogLevel(int logLevel){
        if(null != mIjkVideoView) mIjkVideoView.setLogLevel(logLevel);
    }
    /**
     * {@link IjkVideoView#setTimeOut(long)}
     * @param timeout
     */
    public void setTimeout(long timeout){
        if(null != mIjkVideoView)
        mIjkVideoView.setTimeOut(timeout);
    }



    public void setOnInfoListener(IMediaPlayer.OnInfoListener l){
        mOnInfoListener = l;
    }

    public void setOnErrorListener(IMediaPlayer.OnErrorListener l) {
        mOnErrorListener = l;
    }

    public void setOnCompletionListener(IMediaPlayer.OnCompletionListener l) {
        mOnCompletionListener = l;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        if (!this.isInEditMode()) {
            mHintTv = (TextView) findViewById(R.id.loading_tv);
            mIjkVideoView = (IjkVideoView) findViewById(R.id.ijk_video_view);
            mIjkVideoView.setOnErrorListener(this);
            mIjkVideoView.setOnInfoListener(this);
            mIjkVideoView.setOnCompletionListener(this);
            //set timeout 10s.

            mHintTv.setTextColor(mLoadingColor);
            mHintTv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //living type retry connect ．　
                    if (null != mIjkVideoView && mIjkVideoView.getUrl() != null) {//&& BnVideoView2.BN_URL_TYPE_RTMP_LIVE == mBnVideoView.getURLType()
                        if (!getResources().getString(R.string.VideoView_loading).equals(mHintTv.getText().toString())) {
                            post(new Runnable() {
                                @Override
                                public void run() {
                                    mHintTv.setTextColor(mLoadingColor);
                                    mHintTv.setText(R.string.VideoView_loading);
                                    mHintTv.setVisibility(VISIBLE);
                                }
                            });
                            setVideoPath(mIjkVideoView.getUrl(),mIjkVideoView.getPlayType());
                            start();

                            if(null!= mIjkVideoView.getUrl()){
                                //dms 不合法
                                if(null != mLoadingListener) mLoadingListener.onClick(v);
                            }
                        }
                    }
                    if (null != mTextClickListener) mTextClickListener.onClick(v);
                }
            });
        }
    }

    /**
     * set  video uri .
     * @param path
     * @param urlType
     */
    public void setVideoPath(String path , int urlType) {


        if(null != mIjkVideoView){
            //if current is playing a video ,stop first before begin new play uri .
            if(mIjkVideoView.isPlaying()) release();

            mIjkVideoView.setVideoPath(path, urlType);
            post(new Runnable() {
                @Override
                public void run() {
                    mHintTv.setTextColor(mLoadingColor);
                    mHintTv.setText(R.string.VideoView_loading);
                    mHintTv.setVisibility(VISIBLE);
                }
            });
        }
    }

    /**
     * start loading video & play .
     */
    public void start() {
        if(null != mIjkVideoView){
            mIjkVideoView.start();
        }
    }

    /**
     * pause 。
     */
    public void pause() {
        if(null != mIjkVideoView){
            mIjkVideoView.pause();
        }
    }

    /**
     * 停止播放 & 销毁资源 .
     */
    public void release(){
        if(null != mIjkVideoView){
            mIjkVideoView.stopPlayback();
            mIjkVideoView.release(true);
            mIjkVideoView.stopBackgroundPlay();
        }
    }

    /**
     * add media player .
     * @param controller
     */
    public void setMediaController(IMediaController controller) {
        if (mIjkVideoView != null && null != controller) {
            mIjkVideoView.setMediaController(controller);
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        Log.i(TAG," onError # " +what+" msg: "+extra);
        // DO: 2020/5/26 视频解码失败，处理.
        if(IjkMediaPlayer.MEDIA_ERROR_URL_INVALIDATE == what||
                IjkMediaPlayer.MEDIA_ERROR_NO_STREAM == what){
            //没有数据流，提示用过播放失败，点击重试.
            post(new Runnable() {
                @Override
                public void run() {
                    mHintTv.setTextColor(mRetryColor);
                    mHintTv.setText(R.string.txt_video_meeting_no_input_stream_retry);
                    mHintTv.setVisibility(VISIBLE);
                }
            });
        }

        if(null != mOnErrorListener) mOnErrorListener.onError(mp , what , extra);
        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        Log.i(TAG," onInfo # " +what+" msg: "+extra);
        // DO: 2020/3/31 真正的准备完成了，准备播放。
        if (IjkMediaPlayer.MP_STATE_PREPARED == what) {
            hideTips();
        }
        if(null != mOnInfoListener) mOnInfoListener.onInfo(mp , what , extra) ;
        return false;
    }

    public void hideTips() {
        post(new Runnable() {
            @Override
            public void run() {
                mHintTv.setVisibility(INVISIBLE);
//                mIjkVideoView.requestFocus();
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        release();
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        Log.i(TAG," onCompletion ()~ ");
        if(null != mOnCompletionListener) mOnCompletionListener.onCompletion(mp);
    }

    /**
     * seek to dst pos.
     * @param seekPos
     */
    public void seekTo(int seekPos) {
        if(null != mIjkVideoView && seekPos > 0 ){
            mIjkVideoView.seekTo(seekPos);
        }
    }

    /**
     * 获取当前播放进度.
     * @return
     */
    public int getCurrentPosition(){
        if(null != mIjkVideoView) return mIjkVideoView.getCurrentPosition();
        return 0;
    }

    /**
     * 点击重新加载...
     */
    public interface ITextClickListener {
        void onClick(View v);
    }
    //点击加载更多...回调
    public interface ILoadingListener {
        void onClick(View v);
    }
}
