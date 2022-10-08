package com.example.intervaltimer

import android.media.SoundPool
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*

private const val ViewMOD="viewMod"

class SoundViewModel : ViewModel() {
    private val _sound= MutableLiveData<Int>()
    val sound: LiveData<Int> = _sound

    val soundPool:SoundPool=SoundManager().mySoundPool

    fun setSound(soundOption:Int){
        if (soundOption==1){
            Log.d(ViewMOD,"first if")
            _sound.value=R.raw.beep1
        }else if(soundOption==2){
            Log.d(ViewMOD,"second if")
            _sound.value=R.raw.tingsha_cymbal
        }else{
            Log.d(ViewMOD,"third if")
           _sound.value=R.raw.beep1
        }
    }
    fun getSound(): Int {
        return sound.value!!
    }
    fun hasNoSoundSet():Boolean{
        return _sound.value==null
    }

}