/*
 * 任务bean.
 *  * @author jdpxiaoming
 *  * @github https://github.com/jdpxiaoming
 *  * created  20-5-21 下午2:07: $time
 */
package com.jdpxiaoming.ffmpeg_cmd

class FFmepgTask(
    var taskId: Long,
    var duration: Long,
    var cmds: Array<String?>?,
    var callBacklistener: FFmpegUtil.Callback?
)
