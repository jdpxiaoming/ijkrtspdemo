# ijkrtspdemo
ijkplayer open the rtsp &amp; h265 surpport android demo . 

# 编译了ijk0.8.8
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
