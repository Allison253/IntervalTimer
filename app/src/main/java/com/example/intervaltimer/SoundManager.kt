package com.example.intervaltimer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.util.Log


public class SoundManager {
    var mySoundPool:SoundPool

    init{
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.LOLLIPOP){
            val audioAttributes= AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                .build()
            mySoundPool = SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build()
            Log.d("SoundManagerLogz","sound Pool intitialized in soundManager")
        }else{
            Log.d("SoundManagerLogz","else statement called")
            mySoundPool= SoundPool(10, AudioManager.STREAM_DTMF, 1)
            Log.d("SoundManagerLogz","initialized")

        }
    }
    fun load (context:Context, rawID:Int): Int{
        return mySoundPool.load(context,rawID,1)
    }
    public fun playSound(soundID:Int){

        mySoundPool.play(soundID,1F,1F,1,0,1F)
    }


}

