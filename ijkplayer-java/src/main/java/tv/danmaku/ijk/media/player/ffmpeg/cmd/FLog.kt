/*
 *
 *  * @author jdpxiaoming
 *  * @github https://github.com/jdpxiaoming
 *  * created  20-5-21 上午9:02: $time
 */
package com.jdpxiaoming.ffmpeg_cmd

import android.util.Log

object FLog {
    fun i(tag: String?, args: String) {
//        if(BuildConfig.DEBUG){
        Log.i(tag, args)
        //        }
    }


    fun e(tag: String?, args: String) {
//        if(BuildConfig.DEBUG){
        Log.e(tag, args)
        //        }
    }
}
