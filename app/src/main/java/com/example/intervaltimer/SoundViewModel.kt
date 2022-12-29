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

    private val _interval=MutableLiveData<Int>(0)
    val interval : LiveData<Int> =_interval

    private val _minutes=MutableLiveData<Int>(0)
    val minutes:LiveData<Int> = _minutes

    private val _seconds = MutableLiveData<Int>(0)
    val seconds:LiveData<Int> = _seconds

    private val _intervalAsString=MutableLiveData<String>("00:00")
    val intervalAsString=_intervalAsString

    private val _shift=MutableLiveData<Boolean>(false)
    val shift=_shift

    val soundPool=SoundManager().mySoundPool


    fun setSound(soundOption:Int){
        if (soundOption==1){
            Log.d(ViewMOD,"first if")
            _sound.value=R.raw.beep1
            _shift.value=false
        }else if(soundOption==2){
            Log.d(ViewMOD,"second if")
            _sound.value=R.raw.tingsha_cymbal
            _shift.value=false
        }else if (soundOption==3){
            Log.d(ViewMOD,"third if")
           _sound.value=R.raw.four_beeps
           _shift.value=true
        }else{
            _sound.value=R.raw.beep1
            _shift.value=false
        }
    }
    fun getSound(): Int {
        return sound.value!!
    }
    fun hasNoSoundSet():Boolean{
        return _sound.value==null
    }
    fun getShift():Boolean{
        return shift.value!!
    }

    fun setInterval(intervalMin:Int,intervalSecs:Int){
        _minutes.value=intervalMin
        _seconds.value=intervalSecs
        _interval.value=intervalMin*60+intervalSecs


        if (intervalSecs<10){
            _intervalAsString.value= "$intervalMin:0$intervalSecs"
        }else{
            _intervalAsString.value= "$intervalMin:$intervalSecs"
        }
        Log.d(ViewMOD,"interval as string="+intervalAsString)

    }

    fun getInterval():Int{
        return interval.value!!
    }

    fun hasNoIntervalSet():Boolean{
        return _interval.value==null
    }

    fun getMinutes():Int{
        return minutes.value!!
    }

    fun getSeconds():Int{
        return seconds.value!!
    }

    fun getIntString():String{
        return _minutes.value.toString()+":"+_seconds.value.toString()
    }
}