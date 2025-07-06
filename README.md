# ijkrtsp
ijkplayer open the rtsp &amp; h265 surpport  . 

# Function
- PCMA/PCMU audio surpport.
- H265 video surpport .
- Video Sync model .(video zero delay!)
- rtsp over tcp surpport. 
- flv living model. 
- loading view .
- ffmpeg4.0 .

## 在主项目中build.gradle引入以下库

```groovy
    implementation 'io.github.jdpxiaoming:ijkplayer-view:0.0.26'
    implementation 'io.github.jdpxiaoming:ijkplayer-java:0.0.26'
    implementation 'io.github.jdpxiaoming:ijkplayer-armv7a:0.0.26'
    //看情况如果需要64位so则引入.
    implementation 'io.github.jdpxiaoming:ijkplayer-arm64:0.0.26'
```



# IJKPlayer直播录制功能

本项目为IJKPlayer添加了直播录制功能，使用户能够将正在观看的直播内容保存为本地MP4文件。

## 功能概述

- 支持将正在播放的直播流录制为MP4文件
- 提供简单的Java API接口：`startRecord`和`stopRecord`
- 包含示例Activity展示如何使用录制功能
- 在播放直播的同时进行录制，不影响视频播放体验

## 实现细节

### C层实现

1. 在`ff_ffplay.h`和`ff_ffplay.c`中添加了录制相关函数：
    - `ffp_start_record`: 开始录制
    - `ffp_stop_record`: 停止录制
    - `ffp_record_file`: 将视频数据包写入MP4文件

2. 在`read_thread`函数中添加了录制处理逻辑，当`ffp->is_record`为true时，将接收到的数据包写入MP4文件。

### JNI层实现

1. 在`ijkplayer_jni.c`中实现了JNI接口：
    - `IjkMediaPlayer_startRecord`: 调用`ijkmp_start_record`开始录制
    - `IjkMediaPlayer_stopRecord`: 调用`ijkmp_stop_record`停止录制

2. 在JNI注册表中注册了以上接口，使Java层可以调用它们。

### Java层实现

1. 在`IjkMediaPlayer.java`中添加了录制接口：
    - `startRecord(String filePath)`: 开始录制到指定路径
    - `stopRecord()`: 停止录制

2. 创建了示例Activity `RecordSampleActivity`，演示如何使用录制功能：
    - 提供按钮控制录制的开始和停止
    - 显示录制状态和保存路径
    - 处理生命周期事件，确保正确释放资源

## 使用方法

### 基本用法

```java
// 假设已经初始化了IjkMediaPlayer并开始播放
IjkMediaPlayer player = new IjkMediaPlayer();
player.setDataSource(url);
player.prepare();
player.start();

// 开始录制
String recordPath = "/storage/emulated/0/Movies/record.mp4";
int result = player.startRecord(recordPath);
if (result == 0) {
    // 录制开始成功
} else {
    // 录制开始失败
}

// ... 播放过程中 ...

// 停止录制
player.stopRecord();
```

### 注意事项

1. 确保应用有写入外部存储的权限
2. 录制会增加CPU和内存占用，可能影响播放性能
3. 录制的文件可能会很大，请确保有足够的存储空间
4. 直播录制时应考虑网络波动带来的影响

## 示例代码

详见`RecordSampleActivity.java`，它提供了完整的录制功能演示。

## 未来改进

1. 添加录制质量和格式控制
2. 实现暂停/继续录制功能
3. 添加录制时间限制
4. 优化录制过程中的性能
5. 增加录制事件回调



### 初始化

```java
 // init player
IjkMediaPlayer.loadLibrariesOnce(null);
IjkMediaPlayer.native_profileBegin(IjkMediaPlayer.IJK_LIB_NAME_FFMPEG);
```

# 音频去掉opense硬解码，改为可配置
```java
 //打开opense,h264下有效. 
 mVideoView.setAudioHardWare(true);
```

# 增加自定义参数选项.
```java
mVideoView.setVideoPath(mVideoPath, IjkVideoView.IJK_TYPE_CUSTOMER_PLAY)
```

# 打开iJkview 设置旋转角度
```java 
  //设置视频旋转角度. 
  mVideoView?.isIgnoreRotation = false;
  mVideoView?.setVideoRotationDegree(0)
```

# 打开0延迟的时候默认增加同步方式为`AV_SYNC_VIDEO_MASTER`，默认为音频同步为主.
- you should invoke this method on prepare callback (需要再prepare回调方法中处理同步方式)
```java
 //准备就绪，做一些配置操作，比如音视频同步方式. 
        mVideoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer mp) {
                Log.e(TAG, "onPrepared#done! ");
                mVideoView.openZeroVideoDelay(true);
            }
        });
```

# 增加0延迟开关（0延迟会有2s的音频不同步出现，慎用）
```java
 //打开视频0延迟.
 mVideoView.openZeroVideoDelay(true);
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
### 设置流请求时候`header`-`user-agent`
```java
//set the headers properties in user-agent. 
        mVideoView.setUserAgentStr("Android_Station_V1.1.1");
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
