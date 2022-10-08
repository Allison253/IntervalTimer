package com.example.intervaltimer

import android.app.Activity
import android.app.Application
import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


public class SoundManager: Activity(){
    var mySoundPool:SoundPool
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("SoundManagerLogz", "On create called")
    }
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
            Log.d("SoundManagerLogz","sound Pool intitialized")
        }else{
            Log.d("SoundManagerLogz","else statement called")
            mySoundPool= SoundPool(10, AudioManager.STREAM_DTMF, 1)
            Log.d("SoundManagerLogz","initialized")

        }
    }
}

