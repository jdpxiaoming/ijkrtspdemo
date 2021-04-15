# ijkrtspdemo
ijkplayer open the rtsp &amp; h265 surpport android demo . 

# 0.0.22 打开iJkview 设置旋转角度
```java 
  //设置视频旋转角度. 
  mVideoView?.isIgnoreRotation = false;
  mVideoView?.setVideoRotationDegree(0)
```

# 0.0.21 关闭无效的vp_duration的log信息.
- 2020/12/28 publish. 

# 0.0.20 打开0延迟的时候默认增加同步方式为`AV_SYNC_VIDEO_MASTER`，默认为音频同步为主.
- you should invoke this method befor xx.setVideoPath!
```c
/**
* 0延时开关. 
*@delayOpen  0:关闭 1：开启 默认关闭. 
*/
static void
IjkMediaPlayer_setZeroDelay(JNIEnv *env, jobject thiz, jint delayOpen)
{
    ALOGD("IjkMediaPlayer_setZeroDelay delay_forbidden =  %d",delayOpen);
    MPTRACE("%s\n", __func__);
    IjkMediaPlayer *mp = jni_get_media_player(env, thiz);
	  //给mediaplayer赋值. 
    mp->ffplayer->delay_forbidden = delayOpen;
    //如果打开0延迟则设置视频为主同步
    if(delayOpen){
      mp->ffplayer->av_sync_type  = AV_SYNC_VIDEO_MASTER; 
    } else {
      mp->ffplayer->av_sync_type  = AV_SYNC_AUDIO_MASTER; 
    }
    ijkmp_dec_ref_p(&mp);
}

```
#0.0.19 增加0延迟开关（0延迟会有2s的音频不同步出现，慎用）
```java
 //打开视频0延迟.
 mVideoView.openZeroVideoDelay(true);
```
# 0.0.18 优化直播延时.
- 升级ffmpeg3.4->ffmpeg4.0
- 优化直播0延时观看flv 

# 0.0.17 修改ffmpeg的打包名字，解决和其他ffmpeg库的冲突. 
- 修改libijkffmpeg.so->libijkwdzffmpeg 

# 更换git库测试. 内网服务绑成大赛被重用

## 引入私有库地址.
```groovy
allprojects {
    repositories {
        maven {
            url "https://dl.bintray.com/media/maven"
        }
        google()
        jcenter()
    }
}
```

## 在主项目中build.gradle引入以下库

```groovy
    implementation 'com.github.jdpxiaoming:ijkplayerview:0.0.22'
    implementation 'com.github.jdpxiaoming:ijkplayer-java:0.0.22'
    implementation 'com.github.jdpxiaoming:ijkplayer-armv7a:0.0.22'
    //看情况如果需要64位so则引入.
    implementation 'com.github.jdpxiaoming:ijkplayer-arm64:0.0.22'
```


### 初始化

```java
 // init player
IjkMediaPlayer.loadLibrariesOnce(null);
IjkMediaPlayer.native_profileBegin(IjkMediaPlayer.IJK_LIB_NAME_FFMPEG);
```

### 设置View的填充模式
```java
mVideoView.setAspectRatio(IRenderView.AR_16_9_FIT_PARENT);
```

### Rtsp超时.
```java
//超时单位微妙.2s = 2*1000*1000 
mVideoView.setTimeout(2*1000*1000);
```

### 设置渲染View为`surrfaceView`默认：`TexutureView`
```java
 mVideoView.setRender(IjkVideoView.RENDER_SURFACE_VIEW);
```

### 视图拉伸模式
```java
//拉伸满屏.
 mIjkVideoView.setAspectRatio(IRenderView.AR_MATCH_PARENT);


public interface IRenderView {
    int AR_ASPECT_FIT_PARENT = 0;
    int AR_ASPECT_FILL_PARENT = 1;
    int AR_ASPECT_WRAP_CONTENT = 2;
    int AR_MATCH_PARENT = 3;
    int AR_16_9_FIT_PARENT = 4;
    int AR_4_3_FIT_PARENT = 5;
    ...
    }
``` 

### 根据播放地址类型设置不同的类型 .
```java
    public static final int IJK_TYPE_LIVING_WATCH = 1; //实时监控，要求首开速度,延迟略高一点
    public static final int IJK_TYPE_LIVING_LOW_DELAY = 2; //实时直播要求低延迟，不要求首开熟读 .
    public static final int IJK_TYPE_HTTP_PLAY = 3;//录播 mp4 /hls/flv...
    public static final int IJK_TYPE_FILE_PLAY = 10;//本地文件播放 .
    public static final int IJK_TYPE_CUSTOMER_PLAY = 20;//用户自定义参数模式，需要先调用方法设置参数（setCustomerValue），否则使用ijk默认参数
    public static final int IJK_TYPE_PLAY_DEFAULT = IJK_TYPE_LIVING_WATCH;//默认播放类型.
```

- example 点播
```java
 mVideoView.setVideoPath(mVideoPath, IjkVideoView.IJK_TYPE_HTTP_PLAY);
```

### 实现Mp4文件边下边播放
- 引入下载代理库 : `implementation 'com.danikula:videocache:2.7.1'`
- 定义Application. 
```java 
/**
 * comment:
 * author : poe.Cai
 * date   : 2020/5/28 10:31
 */
public class PApplication extends Application {
    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        PApplication app = (PApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
//        return new HttpProxyCacheServer(this);
        return new HttpProxyCacheServer.Builder(this)
                .maxCacheSize(10 * 1024 * 1024)       // 1 Gb for cache，oss的视频3分钟一般在10M以下.
                .build();
    }
}
```
- 处理播放url
```java
mVideoPath = "https://ovopark-record.oss-cn-shanghai.aliyuncs.com/039570f6-e4c3-4a1b-9886-5ad7e6d7181f.mp4";
HttpProxyCacheServer proxy = PApplication.getProxy(this);
String proxyUrl = proxy.getProxyUrl(mVideoPath);
mVideoView.setVideoPath(proxyUrl, IjkVideoView.IJK_TYPE_HTTP_PLAY);
```


# 编译了[ijk0.8.8](https://github.com/bilibili/ijkplayer)
打开了rtsp的开关

```
#rtsp支持. 
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=rtsp"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=mjpeg"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=sdp"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-demuxer=rtp"

export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-decoder=mjpeg"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-protocol=rtp"
export COMMON_FF_CFG_FLAGS="$COMMON_FF_CFG_FLAGS --enable-protocol=tcp"
```

# 运行demo
> 直接运行ijkplayer-example即可。

# 我编译时选择了target-25 
> 所以你的targetAPI <= 25 最好，如果有高版本需求请自行编译. 

# rtsp首开速度优化 
>配置1. 速度首开50
```配置1. 速度首开50
IJKMEDIA: ===== options =====
IJKMEDIA: player-opts : opensles                     = 1
IJKMEDIA: player-opts : overlay-format               = 842225234
IJKMEDIA: player-opts : mediacodec                   = 1
IJKMEDIA: player-opts : mediacodec-hevc              = 1
IJKMEDIA: player-opts : packet-buffering             = 0
IJKMEDIA: player-opts : fast                         = 1
IJKMEDIA: player-opts : mediacodec-handle-resolution-change = 0
IJKMEDIA: player-opts : framedrop                    = 1
IJKMEDIA: player-opts : start-on-prepared            = 1
IJKMEDIA: player-opts : reconnect                    = 5
IJKMEDIA: format-opts : ijkapplication               = -1284518032
IJKMEDIA: format-opts : ijkiomanager                 = -1414032000
IJKMEDIA: format-opts : rtsp_transport               = tcp
IJKMEDIA: format-opts : rtsp_flags                   = prefer_tcp
IJKMEDIA: format-opts : http-detect-range-support    = 1
IJKMEDIA: format-opts : buffer_size                  = 1316
IJKMEDIA: format-opts : max-buffer-size              = 0
IJKMEDIA: format-opts : infbuf                       = 1
IJKMEDIA: format-opts : analyzemaxduration           = 100
IJKMEDIA: format-opts : probesize                    = 1024
IJKMEDIA: format-opts : flush_packets                = 1
IJKMEDIA: codec-opts  : skip_loop_filter             = 48
IJKMEDIA: ===================
```
>配置2. 首开速度20左右
```
===== options =====
SDL_RunThread: [30113] ff_msg_loop
player-opts : opensles                     = 1
player-opts : overlay-format               = 842225234
message_loop
player-opts : mediacodec                   = 1
player-opts : mediacodec-hevc              = 1
player-opts : packet-buffering             = 0
player-opts : fast                         = 1
player-opts : mediacodec-handle-resolution-change = 0
player-opts : framedrop                    = 1
player-opts : start-on-prepared            = 1
player-opts : reconnect                    = 5
format-opts : ijkapplication               = -1351159456
format-opts : ijkiomanager                 = -1078774656
format-opts : rtsp_transport               = tcp
format-opts : rtsp_flags                   = prefer_tcp
format-opts : http-detect-range-support    = 1
format-opts : buffer_size                  = 1024
format-opts : max-buffer-size              = 0
format-opts : max-fps                      = 20
format-opts : infbuf                       = 1
format-opts : analyzemaxduration           = 80
format-opts : probesize                    = 800
format-opts : flush_packets                = 1
codec-opts  : skip_loop_filter             = 48
===================
```
