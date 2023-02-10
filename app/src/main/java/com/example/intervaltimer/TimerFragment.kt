package com.example.intervaltimer


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

private const val ost="offScreenTimer"
private const val dC="debugChrono"
private const val f="fragment"

class TimerFragment : Fragment() {
    private val sharedViewModel:SoundViewModel by activityViewModels()

    private lateinit var binding: FragmentTimerBinding
    var intervalworked: Boolean = false
    var interval: Int = 0
    var chronoText: String=""
    var soundID:Int=0
    var streamID:Int=0

    override fun onCreateView(
        inflater:LayoutInflater, container:ViewGroup?,
        savedInstanceState:Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val fragmentBinding= FragmentTimerBinding.inflate(inflater,container  ,false)
        // inflate(inflater, container, false)
        binding=fragmentBinding
        return binding!!.root
        Log.d(f, "create view")


    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding?.apply{
            lifecycleOwner=viewLifecycleOwner
            viewModel=sharedViewModel
            soundOptionsFragment=this@TimerFragment
        }
        //if clock is supposed to already be running, run it!


        KeepScreenOn()
        //set up button clicks
        binding.startButton.setOnClickListener{ onStartButton()}
        binding.pauseButton.setOnClickListener{ onPauseButton()}
        binding.resetButton.setOnClickListener{ onResetButton()}
        binding.changeButton.setOnClickListener{ onChangeButton(view)}
        binding.intervalText.setOnClickListener{onChangeInterval(view)}
        binding.chronometer.setOnClickListener{onChangeInterval(view)}


        if (sharedViewModel.hasNoSoundSet()){
            sharedViewModel.setSound(1)
        }
        soundID = sharedViewModel.soundPool.load(context,sharedViewModel.getSound(),1)

    }

    private fun onChangeInterval(view:View){
        val toChange=TimerFragmentDirections.actionTimerFragmentToSetIntervalFragment()
        view.findNavController().navigate(toChange)

    }

    private fun onChangeButton(view: View) {
        val action=TimerFragmentDirections.actionTimerFragmentToSoundOptionsFragment()
        view.findNavController().navigate(action)

    }


    override fun onStop() {
        super.onStop()
        Log.d(f,"fragment stopped")
        TurnScreenOff()
        //start hard timer, stop chronometer, and set pause offset
        if (sharedViewModel.getClockRunning()){
            //set pause offet and start timer
            sharedViewModel.setClockOffset(SystemClock.elapsedRealtime()-binding.chronometer.base) //same as pauseOffset
            timer(true)
            sharedViewModel.setChronoBase(binding.chronometer.base)

            binding.chronometer.stop()

        }
    }

    override fun onStart(){
        super.onStart()

        Log.d(f,"start called")
    }

    override fun onResume(){
        super.onResume()
        KeepScreenOn()
        if (sharedViewModel.getClockRunning()){
            Log.d(f,"fragment resumed with clock running")
            sharedViewModel.setClockOffset(SystemClock.elapsedRealtime() - sharedViewModel.getChronoBase())
            interval=sharedViewModel.getInterval()
            runClock()
            sharedViewModel.beeps!!.cancel()

        }
    }
    override fun onDestroy(){
        Log.d(f,"fragment destroyed")

        TurnScreenOff()
        super.onDestroy()

    }


    private fun TurnScreenOff() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }


    //on startbutton click
    private fun onStartButton(){

        try {
            interval=sharedViewModel.getInterval()
            intervalworked=true

        } catch (e: NumberFormatException) {
            Toast.makeText(context, "Error with interval!", Toast.LENGTH_LONG).show()//catch error would occur if text like 'aa' appeared
            intervalworked = false
        }
        if (interval==0){
            Toast.makeText(context, "Interval cannot be set to zero!", Toast.LENGTH_LONG).show()
        }
        if (!sharedViewModel.getClockRunning() && intervalworked && interval!==0) {//if already running, do nothing

            runClock()
        }
        else if (interval==0){
            Toast.makeText(context,"Inerval cannot be set to 0!",Toast.LENGTH_LONG).show()
        }


    }

    private fun runClock(){

        binding.chronometer.base=SystemClock.elapsedRealtime() - sharedViewModel.getClockOffset()
        Log.d(dC, "runClock called and base set to:"+binding.chronometer.base.toString())
        binding.chronometer.start()
        sharedViewModel.setClockRunning(true)
        if (sharedViewModel.getShift()==true){
            listenChronometer(interval-3)
        }else{
            listenChronometer(0)
        }
    }


    private fun onResetButton(){
        binding.chronometer.base = SystemClock.elapsedRealtime()//resets back to 0
        sharedViewModel.setClockOffset(0)//resets the pause offset to 0

        if (sharedViewModel.getClockRunning()) {//if the user forgot to stop running first
            binding.chronometer.stop()
            sharedViewModel.setClockRunning(false)

        }
        sharedViewModel.soundPool.stop(streamID) //if sound is playing, stop it
    }

    private fun onPauseButton(){
        if (sharedViewModel.getClockRunning()) {
            Log.d(dC,"Pause Button called and chronometer base="+binding.chronometer.base.toString())
            Log.d(dC,"System elapsed real time="+SystemClock.elapsedRealtime().toString())
            binding.chronometer.stop()
            sharedViewModel.setClockOffset(SystemClock.elapsedRealtime() - binding.chronometer.base)
            sharedViewModel.setClockRunning(false)
            sharedViewModel.soundPool.stop(streamID) //if sound is playing, stop it
        }
    }

    private fun listenChronometer(target:Int) {
        binding.chronometer.setOnChronometerTickListener {
            chronoText = binding.chronometer.getText().toString()
            var units = chronoText.split(":")
            var minutes = Integer.parseInt(units[0])
            var seconds = Integer.parseInt(units[1])
            var duration = 60 * minutes + seconds
            var remain = duration % interval


            if (duration != 0) {
                if (remain == target) {
                    playSound()
                }
            }

        }
    }

    fun playSound(){
        streamID=sharedViewModel.soundPool.play(soundID,1F,1F,1,0,1F)
    }


    private fun KeepScreenOn(){
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun timer(firstTime:Boolean){
        //assume is timer running is already set to true, as that is required to run this
        val timerOffset: Long =SystemClock.elapsedRealtime()- binding.chronometer.base //recalculating pauseoffset
        Log.d(ost,"Timer Called")

        var intervalForTimer:Long=0
        if (firstTime){
            intervalForTimer=interval.toLong()*1000-timerOffset%(interval*1000)

        }else{
            intervalForTimer=interval.toLong()*1000
        }
            sharedViewModel.beeps =object:CountDownTimer(intervalForTimer,1000){
                override fun onTick(p0: Long) {
                }
                override fun onFinish() {
                    Log.d(ost, "onFinish for beeps called")
                    playSound()
                    intervalForTimer=interval.toLong()*1000
                    if (!firstTime){
                        this.start()//should restart timer
                    }else{
                        timer(false)//run this again
                    }
                }
            }
        sharedViewModel.beeps!!.start()//start beeping cycle
    }

    override fun onSaveInstanceState(outState: Bundle) {

        Log.d(f,"saving instance state")
        super.onSaveInstanceState(outState)

    }
}

