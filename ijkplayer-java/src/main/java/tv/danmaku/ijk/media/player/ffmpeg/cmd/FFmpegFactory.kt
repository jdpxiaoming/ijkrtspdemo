package com.jdpxiaoming.ffmpeg_cmd

import android.util.Log

/**
 * some ffmpeg cmds factory.
 */
object FFmpegFactory {
    //device info .
    var screenWidth: Int = 1280
    var screenHeight: Int = 720
    var cameraWidth: Int = 640
    var cameraHeight: Int = 360
    var recodeWidth: Int = 640
    var recodeHeight: Int = 360

    //video setting .
    var titlePicWidth: Int = 200
    var titlePicHeight: Int = 100
    var titleDuration: Int = 3


    /**
     * simple ffmpeg -i input_url out_path.
     * @param inputPath
     * @param outputPath
     * @return
     */
    fun buildSimple(inputPath: String?, outputPath: String?): Array<String?> {
        val commands = arrayOfNulls<String>(5)
        commands[0] = "ffmpeg"
        commands[1] = "-i"
        commands[2] = inputPath
        commands[3] = "-y"
        commands[4] = outputPath
        return commands
    }

    /**
     * flv to mp4 .
     * @param inputPath
     * @param outputPath
     * @return
     */
    fun buildFlv2Mp4(inputPath: String?, outputPath: String?): Array<String?> {
        val commands = arrayOfNulls<String>(8)
        commands[0] = "ffmpeg"
        commands[1] = "-i"
        commands[2] = inputPath
        commands[3] = "-vcodec"
        commands[4] = "copy"
        commands[5] = "-acodec"
        commands[6] = "aac"
        commands[7] = outputPath
        return commands
    }


    /**
     * flv->mp4  .
     * @param inputPath
     * @param outputPath  .
     * @return
     */
    fun buildRtsp2Mp4(inputPath: String?, outputPath: String?): Array<String?> {
        val commands = arrayOfNulls<String>(19)
        commands[0] = "ffmpeg"
        commands[1] = "-rtsp_transport"
        commands[2] = "tcp"
        commands[3] = "-i"
        commands[4] = inputPath
        // 显式映射视频和音频流，解决Android端"No streams to mux were specified"错误
        // Windows端可能自动处理，但Android端需要显式指定
        // -map 必须在 -i 之后，codec 参数之前
        // 使用简化的map语法：0:0表示第一个输入文件的第一个流，0:1表示第二个流
        commands[5] = "-map"
        commands[6] = "0:0" // 映射第一个输入文件的第一个流（视频）
        commands[7] = "-map"
        commands[8] = "0:1" // 映射第一个输入文件的第二个流（音频）
        // 使用 -c:v 和 -c:a 替代 -vcodec 和 -acodec（更标准的语法）
        commands[9] = "-c:v"
        commands[10] = "copy"
        commands[11] = "-c:a"
        commands[12] = "aac"
        // 添加MP4格式优化参数，提高Android端兼容性
        commands[13] = "-movflags"
        commands[14] = "+faststart" // 优化MP4文件，支持流式播放
        commands[15] = "-f"
        commands[16] = "mp4"
        commands[17] = "-y" //覆盖
        commands[18] = outputPath

        return commands
    }


    /**
     * zoom scale video
     * 
     * @param inputPath
     * @param outputPath
     * @param width
     * @param height
     * @return
     */
    fun scaleVideo(
        inputPath: String?,
        outputPath: String?,
        width: Int,
        height: Int
    ): Array<String?> {
        val commands = arrayOfNulls<String>(6)
        commands[0] = "ffmpeg"
        commands[1] = "-i"
        commands[2] = inputPath
        commands[3] = "-vf"
        commands[4] = "scale=" + width + ":" + height
        commands[5] = outputPath
        return commands
    }


    //ffmpeg -ss 00:00:01 -t 3 -i input.mp4 -vf crop=iw:ih*2/3 -s 320x240 -r 7 output.gif
    //ffmpeg -t 3 -ss 00:00:02 -i small.mp4 small-clip.gif
    fun cutVideoGif(
        inputPath: String?,
        outputPath: String?,
        duration: String?,
        fps: String?,
        wh: String?
    ): Array<String?> {
        val commands = arrayOfNulls<String>(12)
        commands[0] = "ffmpeg"
        commands[1] = "-ss"
        commands[2] = "00:00:00"
        commands[3] = "-t"
        commands[4] = duration
        commands[5] = "-i"
        commands[6] = inputPath
        commands[7] = "-s"
        commands[8] = wh
        commands[9] = "-r"
        commands[10] = fps
        commands[11] = outputPath
        return commands
    }

    fun cutVideoGif(inputPath: String?, outputPath: String?, duration: String?): Array<String?> {
        val commands = arrayOfNulls<String>(8)
        commands[0] = "ffmpeg"
        commands[1] = "-ss"
        commands[2] = "00:00:00"
        commands[3] = "-t"
        commands[4] = duration
        commands[5] = "-i"
        commands[6] = inputPath
        commands[7] = outputPath
        return commands
    }

    fun cutVideoTime(path: String?, startTime: Int, endTime: Int, output: String?): Array<String?> {
        //ffmpeg -ss 00:00:15 -t 00:00:05 -i input.mp4 -vcodec copy -acodec copy output.mp4
        //ffmpeg -ss **START_TIME** -i input.mp4  -t **STOP_TIME** -acodec copy -vcodec copy output.mp4
        val startM = startTime / 1000
        val endM = (endTime - startTime) / 1000

        val startStr: String?
        val endStr: String?

        if (startM < 10) {
            startStr = "00:00:0" + startM
        } else {
            startStr = "00:00:" + startM
        }

        if (endM < 10) {
            endStr = "00:00:0" + endM
        } else {
            endStr = "00:00:" + endM
        }

        val commands = arrayOfNulls<String>(12)
        commands[0] = "ffmpeg"
        commands[1] = "-i"
        commands[2] = path
        commands[3] = "-vcodec"
        commands[4] = "copy"
        commands[5] = "-acodec"
        commands[6] = "copy"
        commands[7] = "-ss"
        commands[8] = startStr
        commands[9] = "-t"
        commands[10] = endStr
        commands[11] = output
        return commands
    }

    fun addWaterMark(imageUrl: String?, videoUrl: String?, outputUrl: String?): Array<String?> {
//        # libx264 最快速度35秒
        val commands = arrayOfNulls<String>(24)
        commands[0] = "ffmpeg"
        //input
        commands[1] = "-i"
        commands[2] = videoUrl
        //water cover.
        commands[3] = "-i"
        commands[4] = imageUrl
        commands[5] = "-filter_complex"
        commands[6] =
            "[1:v] fade=out:st=30:d=1:alpha=1 [ov]; [0:v][ov] overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2  [v]"
        commands[7] = "-map"
        commands[8] = "[v]"
        commands[9] = "-map"
        commands[10] = "0:a"
        commands[11] = "-c:v"
        commands[12] = "libx264"
        commands[13] = "-c:a"
        commands[14] = "copy"
        commands[15] = "-r"
        commands[16] = "15"
        commands[17] = "-crf"
        commands[18] = "28"
        commands[19] = "-shortest"
        commands[20] = "-preset"
        commands[21] = "ultrafast"
        //override -vcodec h264 -f mp4
        commands[22] = "-y"
        //output .
        commands[23] = outputUrl


        /*String[] commands = new String[16];
        commands[0] = "ffmpeg";
        //input
        commands[1] = "-i";
        commands[2] = videoUrl;
        //water cover.
        commands[3] = "-i";
        commands[4] = imageUrl;
        commands[5] = "-filter_complex";
        commands[6] = "overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2";
        commands[7] = "-r";
        commands[8] = "15";
        //override -vcodec h264 -f mp4
        commands[9] = "-preset";
        commands[10] = "ultrafast";
        commands[11] = "-crf";
        commands[12] = "28";
        commands[13] = "-shortest";
        commands[14] = "-y";
        //output .
        commands[15] = outputUrl;*/
        return commands
    }

    /**
     * picture water hint .
     */
    fun addImageMark(videoUrl: String?, imageUrl: String?, outputUrl: String?): Array<String?> {
        val commands = arrayOfNulls<String>(9)
        commands[0] = "ffmpeg"
        //input
        commands[1] = "-i"
        commands[2] = videoUrl
        //water hint .
        commands[3] = "-i"
        commands[4] = imageUrl
        commands[5] = "-filter_complex"
        commands[6] = "[1:v]scale=" + screenWidth + ":" + screenHeight + "[s];[0:v][s]overlay=0:0"
        commands[7] = "-y"
        commands[8] = outputUrl
        return commands
    }

    fun addImageMark(
        videoUrl: String?,
        imageUrl: String?,
        outputUrl: String?,
        width: Int,
        height: Int
    ): Array<String?> {
        val commands = arrayOfNulls<String>(9)
        commands[0] = "ffmpeg"
        commands[1] = "-i"
        commands[2] = videoUrl
        commands[3] = "-i"
        commands[4] = imageUrl
        commands[5] = "-filter_complex"
        commands[6] = "[1:v]scale=" + width + ":" + height + "[s];[0:v][s]overlay=0:0"
        commands[7] = "-y"
        commands[8] = outputUrl
        return commands
    }

    /**
     * bgm .
     */
    fun addMusic(videoUrl: String?, musicUrl: String?, outputUrl: String?): Array<String?> {
        val commands = arrayOfNulls<String>(7)
        commands[0] = "ffmpeg"
        commands[1] = "-i"
        commands[2] = videoUrl
        commands[3] = "-i"
        commands[4] = musicUrl
        commands[5] = "-y"
        commands[6] = outputUrl
        return commands
    }

    /**
     * word hint .
     */
    fun addTextMark(videoUrl: String?, imageUrl: String?, outputUrl: String?): Array<String?> {
        val _commands = ArrayList<String?>()
        _commands.add("ffmpeg")
        _commands.add("-i")
        _commands.add(videoUrl)
        _commands.add("-i")
        _commands.add(imageUrl)
        _commands.add("-filter_complex")
        _commands.add("overlay=(main_w-overlay_w)/2:(main_h-overlay_h)/2")
        _commands.add("-y")
        _commands.add(outputUrl)
        val commands = arrayOfNulls<String>(_commands.size)
        for (i in _commands.indices) {
            commands[i] = _commands.get(i)
        }
        return commands
    }

    /**
     * concat video with another video .
     */
    fun concatVideo(
        _filePath: String?,
        _outPath: String?
    ): Array<String?> { //-f concat -i list.txt -c copy concat.mp4
        val _commands = ArrayList<String?>()
        _commands.add("ffmpeg")
        _commands.add("-f")
        _commands.add("concat")
        _commands.add("-i")
        _commands.add(_filePath)
        _commands.add("-c")
        _commands.add("copy")
        _commands.add(_outPath)
        val commands = arrayOfNulls<String>(_commands.size)
        var _pr: String? = ""
        for (i in _commands.indices) {
            commands[i] = _commands.get(i)
            _pr += commands[i]
        }
        Log.d("LOGCAT", "ffmpeg command:" + _pr + "-" + commands.size)
        return commands
    }

    /**
     * make video with many pictures.
     */
    fun image2mov(imageUrl: String, _t: String?, outputUrl: String?): Array<String?> {
        val _commands = ArrayList<String?>()
        _commands.add("ffmpeg")
        val _type = imageUrl.substring(imageUrl.length - 3)
        if (_type == "gif") {
            _commands.add("-ignore_loop")
            _commands.add("0")
        } else {
            _commands.add("-loop")
            _commands.add("1")
        }
        _commands.add("-i")
        _commands.add(imageUrl)
        //_commands.add("-vcodec");
        //_commands.add("libx264");
        _commands.add("-r")
        _commands.add("25")
        _commands.add("-b")
        _commands.add("200k")
        _commands.add("-s")
        _commands.add("640x360")
        _commands.add("-t")
        _commands.add(_t)
        _commands.add("-y")
        _commands.add(outputUrl)
        val commands = arrayOfNulls<String>(_commands.size)
        var _pr: String? = ""
        for (i in _commands.indices) {
            commands[i] = _commands.get(i)
            _pr += commands[i]
        }
        Log.d("LOGCAT", "ffmpeg command:" + _pr + "-" + commands.size)
        return commands
    }

    /**
     * make video .
     */
    fun makeVideo(
        textImageUrl: String,
        imageUrl: String,
        musicUrl: String,
        videoUrl: String?,
        outputUrl: String?,
        _duration: Int
    ): Array<String?> {
        val _commands = ArrayList<String?>()
        _commands.add("ffmpeg")
        _commands.add("-i")
        _commands.add(videoUrl)
        if (textImageUrl != "" || imageUrl != "") {
            //picture hint .
            if (imageUrl != "") {
                _commands.add("-ignore_loop")
                _commands.add("0")
                _commands.add("-i")
                _commands.add(imageUrl)
            }
            if (textImageUrl != "") {
                _commands.add("-i")
                _commands.add(textImageUrl)
            }
            _commands.add("-filter_complex")
            if (textImageUrl == "") {
                _commands.add("[1:v]scale=" + screenWidth + ":" + screenHeight + "[s];[0:v][s]overlay=0:0")
            } else if (imageUrl == "") {
                _commands.add("overlay=x='if(lte(t," + titleDuration + "),(main_w-overlay_w)/2,NAN )':(main_h-overlay_h)/2")
            } else {
                _commands.add(
                    ("[1:v]scale=" + screenWidth + ":" + screenHeight
                            + "[img1];[2:v]scale=" + titlePicWidth + ":" + titlePicHeight
                            + "[img2];[0:v][img1]overlay=0:0[bkg];[bkg][img2]overlay=x='if(lte(t,"
                            + titleDuration + "),(main_w-overlay_w)/2,NAN )':(main_h-overlay_h)/2")
                )
            }
        }
        //music.
        if (musicUrl != "") {
            //-ss&-t control music length .
            //_commands.add("-ss");
            //_commands.add("00:00:00");
            //_commands.add("-t");
            //_commands.add(""+_duration);
            _commands.add("-i")
            _commands.add(musicUrl)
        }
        _commands.add("-r")
        _commands.add("25")
        _commands.add("-b")
        _commands.add("1000k")
        _commands.add("-s")
        _commands.add("640x360")
        //_commands.add("-y");
        _commands.add("-ss")
        _commands.add("00:00:00")
        _commands.add("-t")
        _commands.add("" + _duration)
        _commands.add(outputUrl)
        val commands = arrayOfNulls<String>(_commands.size)
        var _pr: String? = ""
        for (i in _commands.indices) {
            commands[i] = _commands.get(i)
            _pr += commands[i]
        }
        Log.d("LOGCAT", "ffmpeg command:" + _pr + commands.size)
        return commands
    }
}
