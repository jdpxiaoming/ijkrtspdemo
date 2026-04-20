package com.jdpxiaoming.ffmpeg_cmd

/**
 * ffmpeg tools.
 * @author jdpxiaoming 2020/05/16
 */
object FFmpegCmd {
    private const val TAG = "FFmpegCmd"

    init {
        System.loadLibrary("ijkffmpegcmd")
    }

    private var sOnCmdExecListener: OnCmdExecListener? = null
    private var sDuration: Long = 0

    // JNI still binds to the legacy static Java API shape.
    @JvmStatic
    external fun exec(argc: Int, argv: Array<String?>?): Int

    @JvmStatic
    external fun exit()

    /**
     * 手动c代码调用api，非cmd模式.
     * @param input
     * @param output
     * @return
     */
    @JvmStatic
    external fun dump_stream(input: String?, output: String?): Int

    @JvmStatic
    external fun dump_Rtsp_h265(input: String?, output: String?): Int

    /**
     * this method invoked child thread , please use [FFmpegUtil.exec]
     * @param cmds
     * @param duration
     * @param listener
     */
    @JvmStatic
    fun exec(cmds: Array<String?>, duration: Long, listener: OnCmdExecListener?) {
        sOnCmdExecListener = listener
        sDuration = duration

        exec(cmds.size, cmds)
    }

    /**
     * this method invoked child thread , please use [FFmpegUtil.exec]
     * @param cmds
     * @param listener
     */
    @JvmStatic
    fun exec(cmds: Array<String?>, listener: OnCmdExecListener?) {
        sOnCmdExecListener = listener
        exec(cmds.size, cmds)
    }


    @JvmStatic
    fun onExecuted(ret: Int) {
        FLog.i(TAG, " onExecuted # " + ret)
        if (sOnCmdExecListener != null) {
            if (ret == 0) {
                sOnCmdExecListener!!.onProgress(sDuration.toFloat())
                sOnCmdExecListener!!.onSuccess()
            } else {
                sOnCmdExecListener!!.onFailure()
            }
        }
    }

    /**
     * transcode on progress .
     * jni invoked this method .
     * @param progress
     */
    @JvmStatic
    fun onProgress(progress: Float) {
        FLog.i(TAG, " onProgress # " + progress)
        if (sOnCmdExecListener != null) {
            if (sDuration != 0L) {
                sOnCmdExecListener!!.onProgress(progress / (sDuration / 1000) * 0.95f)
            } else {
                sOnCmdExecListener!!.onProgress(progress)
            }
        }
    }

    /**
     * task finish invoked.
     * jni invoked this method when task is finished.
     */
    @JvmStatic
    fun onComplete() {
        FLog.i(TAG, " onComplete ()# action done!")
        if (sOnCmdExecListener != null) {
            sOnCmdExecListener!!.onComplete()
        }
    }


    /**
     * task finish invoked.
     * jni invoked this method when task is finished.
     */
    @JvmStatic
    fun onFailure() {
        FLog.i(TAG, " onFailure ()# action done!")
        if (sOnCmdExecListener != null) {
            sOnCmdExecListener!!.onFailure()
        }
    }

    /**
     * stop下载任务回调.
     */
    @JvmStatic
    fun onCancelFinish() {
        FLog.i(TAG, " onCancelFinish ()# action done!")
        if (sOnCmdExecListener != null) {
            sOnCmdExecListener!!.onCancelFinish()
        }
    }

    interface OnCmdExecListener {
        fun onSuccess()

        fun onFailure()

        fun onComplete()

        fun onProgress(progress: Float)

        /**
         * 取消完成.
         */
        fun onCancelFinish()
    }
}
