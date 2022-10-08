package com.example.intervaltimer


import android.media.SoundPool
import android.os.Bundle
import android.os.CountDownTimer
import android.os.SystemClock
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.intervaltimer.databinding.FragmentTimerBinding
import androidx.appcompat.app.AppCompatActivity






class TimerFragment : Fragment() {
    private val sharedViewModel:SoundViewModel by activityViewModels()
    private lateinit var binding: FragmentTimerBinding
    var intervalworked: Boolean = false
    var interval: Int = 0
    var chronoText: String=""
    var running:Boolean = false
    var pauseOffset:Long=0 //used to calculate the chronometer value when restarting after a pause
    lateinit var beeps: CountDownTimer
    var isTimerRunning:Boolean=false
    var soundID:Int=0


    override fun onCreateView(
        inflater:LayoutInflater, container:ViewGroup?,
        savedInstanceState:Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()

        binding= FragmentTimerBinding.inflate(inflater,container,false)
        Log.d("fragment", "GameFragment created/re-created!")
        return binding.root}


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        super.onViewCreated(view, savedInstanceState)
        Log.d("fragment","View created")
        //set up click listener
        listenChronometer()
        KeepScreenOn()
        //set up button clicks
        binding.startButton.setOnClickListener{ onStartButton()}
        binding.pauseButton.setOnClickListener{ onPauseButton()}
        binding.resetButton.setOnClickListener{ onResetButton()}
        binding.changeButton.setOnClickListener{ onChangeButton(view)}


        binding.switch1.setOnCheckedChangeListener { buttonView, isChecked->
            if (isChecked){
                KeepScreenOn()
            }else{
                TurnScreenOff()
            }
        }
        if (sharedViewModel.hasNoSoundSet()){
            sharedViewModel.setSound(1)
        }
        soundID = sharedViewModel.soundPool.load(context,sharedViewModel.getSound(),1)
        Log.d("fragment","soundID="+soundID)

    }

    private fun onChangeButton(view: View) {
        val action=TimerFragmentDirections.actionTimerFragmentToSoundOptionsFragment(soundStart=R.raw.beep1)
        view.findNavController().navigate(action)
    }


    override fun onStop() {
        super.onStop()
        Log.d("fragment","fragment stopped")
        if (running){
            timer(true)
        }
    }

    override fun onResume(){
        super.onResume()
        Log.d("fragment", "fragment resumed")
        if (isTimerRunning){
            beeps.cancel()
        }

    }
    override fun onDestroy(){
        Log.d("fragment","fragment destroyed")
        super.onDestroy()
        sharedViewModel.soundPool.release()
    }

    private fun TurnScreenOff() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    //on startbutton click
    private fun onStartButton(){
        try {
            var test1: Int=0
            var test2:Int=0
            if (binding.intervalNum1.text.toString()!=""){
                test1=Integer.parseInt(binding.intervalNum1.text.toString())
                intervalworked = true
            }
            if (binding.intervalNum2.text.toString()!=""){
                test2=Integer.parseInt(binding.intervalNum2.text.toString())
                intervalworked = true
            }
            interval=test1*60+test2
            Log.d("fragment","interval1=" +interval)

        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Error with interval!", Toast.LENGTH_LONG).show()//catch error would occur if text like 'aa' appeared
            intervalworked = false
        }
        if (!running and intervalworked) {//if already running, do nothing
            binding.chronometer.base=SystemClock.elapsedRealtime() - pauseOffset
            binding.chronometer.start()
            running = true
        }else{
            Toast.makeText(context, "You did not set an interval!", Toast.LENGTH_LONG).show()
        }


    }

    private fun onResetButton(){
        binding.chronometer.base = SystemClock.elapsedRealtime()//resets back to 0
        pauseOffset = 0//resets the pause offset to 0

        if (running) {//if the user forgot to stop running first
            binding.chronometer.stop()
            running = false

        }
    }

    private fun onPauseButton(){
        if (running) {
            binding.chronometer.stop()
            pauseOffset = SystemClock.elapsedRealtime() - binding.chronometer.base
            running = false
        }
    }

    private fun listenChronometer() {
        binding.chronometer.setOnChronometerTickListener {
            chronoText = binding.chronometer.getText().toString()
            var units = chronoText.split(":")
            var minutes = Integer.parseInt(units[0])
            var seconds = Integer.parseInt(units[1])
            var duration = 60 * minutes + seconds
            var remain = duration % interval

            if (duration != 0) {
                if (remain == 0) {
                    playSound()
                }
            }

        }
    }




    fun playSound(){
        sharedViewModel.soundPool.play(soundID,1F,1F,1,0,1F)
        Log.d("playSound","="+soundID)
    }


    private fun KeepScreenOn(){
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun timer(firstTime:Boolean){
        isTimerRunning=true //assume if function is called, timer will run
        val timerOffset: Long =SystemClock.elapsedRealtime()- binding.chronometer.base //recalculating pauseoffset

        var intervalForTimer:Long=0
        if (firstTime){
            intervalForTimer=interval.toLong()*1000-timerOffset

        }else{
            intervalForTimer=interval.toLong()*1000
        }

            beeps=object:CountDownTimer(intervalForTimer,1000){
                override fun onTick(p0: Long) {
                }
                override fun onFinish() {
                    playSound()
                    intervalForTimer=interval.toLong()*1000
                    if (!firstTime){
                        this.start()//should restart timer
                    }else{
                        timer(false)//run this again
                    }
                }
            }
        beeps.start()//start beeping cycle
    }

}

