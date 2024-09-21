package com.example.intervaltimer

import android.app.Application
import android.app.NotificationManager
import android.content.Context
import android.media.SoundPool
import android.os.CountDownTimer
import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import java.util.*

private const val ViewMOD="viewMod"

class SoundViewModel: ViewModel() {



    private val _sound= MutableLiveData<Int>()
    val sound: LiveData<Int> = _sound


    //initialize

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

    lateinit var beeps: Job

    val notificationID: Int=101

    private val _clockRunning=MutableLiveData<Boolean>(false) //both offscreen clock and chronometer
    val clockRunning=_clockRunning

    private val _offScreenTimer=MutableLiveData<Boolean>(false) //both offscreen clock and chronometer
    val offScreenTimer=_clockRunning

    private val _clockOffset=MutableLiveData<Long>(0)
    val clockOffset=_clockOffset

    private val _chronoBase=MutableLiveData<Long>(0)
    val chronoBase=_chronoBase


    //sound Options and setting timer offset
    fun setSound(soundOption:Int){
        if (soundOption==1){
            _sound.value=R.raw.beep1
            _shift.value=false
        }else if(soundOption==2){
            _sound.value=R.raw.tingsha_cymbal
            _shift.value=false
        }else if (soundOption==3){
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

    //interval setup
    fun setInterval(intervalMin:Int,intervalSecs:Int){
        _minutes.value=intervalMin
        _seconds.value=intervalSecs
        _interval.value=intervalMin*60+intervalSecs


        if (intervalSecs<10){
            _intervalAsString.value= "$intervalMin:0$intervalSecs"
        }else{
            _intervalAsString.value= "$intervalMin:$intervalSecs"
        }

    }

    fun getInterval():Int{
        return interval.value!!
    }

    //clock running variables for swapping screens and turning off screens

    fun setClockRunning(input:Boolean){


        _clockRunning.value=input

    }

    fun getClockRunning():Boolean{
        return clockRunning.value!!

    }

    fun setoffScreenTimer(input:Boolean){


        _offScreenTimer.value=input

    }

    fun getoffScreenTimer():Boolean{
        return offScreenTimer.value!!

    }


    fun setClockOffset(paused:Long){
        _clockOffset.value=paused
    }

    fun getClockOffset():Long{
        return clockOffset.value!!
    }

    fun setChronoBase(base:Long){
        _chronoBase.value=base

    }

    fun getChronoBase():Long{
        return chronoBase.value!!
    }

}