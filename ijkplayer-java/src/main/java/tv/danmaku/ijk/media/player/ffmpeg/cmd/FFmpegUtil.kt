/*
 *
 *  * @author jdpxiaoming
 *  * @github https://github.com/jdpxiaoming
 *  * created  20-5-20 下午4:53: $time
 */
package com.jdpxiaoming.ffmpeg_cmd

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.os.SystemClock
import android.util.Log
import com.jdpxiaoming.ffmpeg_cmd.FFmpegCmd.OnCmdExecListener
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit
import kotlin.concurrent.Volatile

/**
 * ffmepg tools thread attack to main thread util class.
 * @author jdpxiaoming
 * @github https://github.com/jdpxiaoming
 * created  2020/05/20 16:48
 */
class FFmpegUtil private constructor() : Handler.Callback {
    private var mHandler: Handler? = null

    private var mCallbackListener: Callback? = null //回调.

    // TODO: 2020/5/21 缓存请求命令到一个缓存队列，按序执行 .
    private val mAsynTaskQueue = LinkedBlockingQueue<FFmepgTask?>()

    //固定一个线程来实现异步，方便后期扩展.
    private val mThreadPoolService: ExecutorService = Executors.newFixedThreadPool(1)

    private var isRunning = false

    private var mCurrentTask: FFmepgTask? = null

    private var mLock: Any? = null


    private val mReadThread: Runnable = object : Runnable {
        override fun run() {
            while (true) {
                if (!isRunning) {
                    FLog.e(TAG, "mReadThread stop isRunning is false !")
                    break
                }

                try {
                    synchronized(mLock!!) {
                        //没有让任务等待，拿到任务继续执行
                        mCurrentTask = mAsynTaskQueue.take()
                        if (null != mCurrentTask) {
                            //枷锁，等待当前任务执行完毕.MSG_ON_COMPLETE被执行.
                            exec(
                                mCurrentTask!!.cmds,
                                mCurrentTask!!.duration,
                                mCurrentTask!!.callBacklistener
                            )
                        }
                        (mLock as Object).wait()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }
    }


    init {
        if (null == mHandler) {
            mHandler = Handler(Looper.getMainLooper(), this)
        }
        mLock = Any()
        // TODO: 2020/5/21 开启读取线程.
        isRunning = true
        mThreadPoolService.execute(mReadThread)
    }


    /**
     * 结束当前任务.清空等待队列. 结束后台任务.
     */
    fun stopTask() {
        isRunning = false
        mAsynTaskQueue.clear()
        synchronized(mLock!!) {
            (mLock as Object).notifyAll()
        }
        FFmpegCmd.exit()
    }

    /**
     * untest please do not use this .
     */
    fun onDestroy() {
        isRunning = false
        mThreadPoolService.shutdown()
        try {
            if (!mThreadPoolService.awaitTermination(100, TimeUnit.MILLISECONDS)) {
                mThreadPoolService.shutdownNow()
            }
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

    /**
     * 同步执行任务。 先进先出.
     * @param cmds
     * @param duration
     * @param listener
     */
    fun enQueueTask(cmds: Array<String?>?, duration: Long, listener: Callback?) {
        Log.e(TAG, "enQueueTask()")

        val id = SystemClock.currentThreadTimeMillis()
        val task = FFmepgTask(id, duration, cmds, listener)
        mAsynTaskQueue.add(task)
        if (!isRunning) {
            isRunning = true
            mThreadPoolService.execute(mReadThread)
        }
    }


    /**
     * 执行底层命令 .
     * @param cmds string[] commands collection . [FFmpegFactory.buildFlv2Mp4]
     * @param duration total time long in millisecond .
     * @param listener listen callback .
     */
    private fun exec(cmds: Array<String?>?, duration: Long, listener: Callback?) {
        FLog.e(TAG, "exec() ~FFmpegCmd.exec.....$cmds !")
        if(cmds == null || cmds.size == 0) return

        mCallbackListener = listener

        FFmpegCmd.exec(cmds, duration, object : OnCmdExecListener {
            override fun onSuccess() {
                FLog.i(TAG, " onSuccess # ")
                if (null != mHandler) mHandler!!.sendEmptyMessage(MSG_ON_START)
            }

            override fun onFailure() {
                FLog.e(TAG, " onFailure # ")
                if (null != mHandler) mHandler!!.sendEmptyMessage(MSG_ON_FAILURE)
            }

            override fun onComplete() {
                FLog.e(TAG, " onComplete #mHandler~ ")
                if (null != mHandler) mHandler!!.sendEmptyMessage(MSG_ON_COMPLETE)
            }

            override fun onProgress(progress: Float) {
                FLog.i(TAG, " onProgress # ")
                if (null != mHandler) mHandler!!.sendMessage(
                    mHandler!!.obtainMessage(
                        MSG_ON_PROGRESS, progress
                    )
                )
            }

            override fun onCancelFinish() {
                FLog.e(TAG, " onCancelFinish #mHandler~ ")
                if (null != mHandler) mHandler!!.sendEmptyMessage(MSG_ON_CANCEL_FINISH)
            }
        })
    }

    private fun exec(cmds: Array<String?>?, listener: Callback?) {
        exec(cmds, 0, listener)
    }


    override fun handleMessage(msg: Message): Boolean {
        when (msg.what) {
            MSG_ON_START -> {
                FLog.i(TAG, " MSG_ON_START # ")
                if (null != mCallbackListener) mCallbackListener!!.onStart()
            }

            MSG_ON_FAILURE -> {
                FLog.i(TAG, " MSG_ON_FAILURE # ")
                synchronized(mLock!!) {
                    if (null != mCallbackListener) mCallbackListener!!.onFailure()
                    (mLock as Object).notifyAll()
                }
            }

            MSG_ON_PROGRESS -> {
                //视频时长单位s(秒).
                val currentTime = msg.obj as Float
                FLog.i(TAG, " onProgress # " + currentTime)
                if (null != mCallbackListener) mCallbackListener!!.onProgress(currentTime)
            }

            MSG_ON_COMPLETE -> {
                FLog.i(TAG, " MSG_ON_COMPLETE # ")
                // DO: 2020/5/21 通知任务继续执行.
                synchronized(mLock!!) {
                    if (null != mCallbackListener) mCallbackListener!!.onComplete()
                    (mLock as Object).notifyAll()
                }
            }

            MSG_ON_CANCEL_FINISH -> {
                FLog.i(TAG, " MSG_ON_COMPLETE # ")
                synchronized(mLock!!) {
                    (mLock as Object).notifyAll()
                }
            }

            else -> {
                FLog.i(TAG, " MSG_ON_START # ")
                if (null != mCallbackListener) mCallbackListener!!.onStart()
            }
        }
        return true
    }

    interface Callback {
        /**
         * 成功执行代码.
         */
        fun onStart()

        /**
         * 中断错误.
         */
        fun onFailure()

        /**
         * 执行任务完成.
         */
        fun onComplete()

        /**
         * 进度回掉.
         * @param progress
         */
        fun onProgress(progress: Float)
    }

    companion object {
        private const val TAG = "FFmpegUtil"

        @Volatile
        private var mInstance: FFmpegUtil? = null

        private const val MSG_ON_START = 0x001
        private const val MSG_ON_FAILURE = 0x002
        private const val MSG_ON_PROGRESS = 0x003
        private const val MSG_ON_COMPLETE = 0x004
        private const val MSG_ON_CANCEL_FINISH = 0x005
        private const val MSG_ON_ERROR = 0x101

        val instance: FFmpegUtil?
            get() {
                if (null == mInstance) {
                    synchronized(FFmpegUtil::class.java) {
                        if (null == mInstance) {
                            mInstance = FFmpegUtil()
                        }
                    }
                }
                return mInstance
            }
    }
}
