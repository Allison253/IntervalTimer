package com.example.intervaltimer


import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock.elapsedRealtime
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.intervaltimer.databinding.FragmentTimerBinding
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.internal.FallbackServiceBroker
import kotlinx.coroutines.*
import java.security.KeyStore

private const val ost="offScreenTimer"
private const val dC="debugChrono"
private const val f="myfragment"
private const val n="myNoti"
private const val comp="chronoCompare"


class TimerFragment : Fragment() {

    private val sharedViewModel:SoundViewModel by activityViewModels()
    private lateinit var binding: FragmentTimerBinding
    var intervalworked: Boolean = false
    var interval: Int = 0
    var chronoText: String=""
    var soundID:Int=0
    var streamID:Int=0
    val CHANNEL_ID="channel_id_example01"
    lateinit var pendingIntent: PendingIntent

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)


    override fun onCreateView(
        inflater:LayoutInflater, container:ViewGroup?,
        savedInstanceState:Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        //setRetainInstance(true)
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        val fragmentBinding= FragmentTimerBinding.inflate(inflater,container  ,false)
        // inflate(inflater, container, false)
        binding=fragmentBinding
        Log.d(f, "create view")
        return binding!!.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding?.apply{
            lifecycleOwner=viewLifecycleOwner
            viewModel=sharedViewModel
            soundOptionsFragment=this@TimerFragment
        }


        //if clock is supposed to already be running, run it!
        Log.d(f, "onViewCreated Called")


        KeepScreenOn()
        //set up button clicks
        binding.startButton.setOnClickListener{ onStartButton()}
        binding.pauseButton.setOnClickListener{ onPauseButton()}
        binding.resetButton.setOnClickListener{ onResetButton()}
        binding.changeButton.setOnClickListener{ onChangeButton(view)}
        binding.intervalText.setOnClickListener{onChangeInterval(view)}
        binding.chronometer.setOnClickListener{onChangeInterval(view)}
        val context=requireContext().applicationContext

        val resultIntent = Intent(activity, MainActivity::class.java)


        resultIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)

        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        // The stack builder object will contain an artificial back stack for the started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        val stackBuilder: TaskStackBuilder = TaskStackBuilder.create(activity)

        // Adds the back stack for the Intent (but not the Intent itself)

        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity::class.java)

        // Adds the Intent that starts the Activity to the top of the stack

        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent)


        val requestID = System.currentTimeMillis().toInt()
        pendingIntent =PendingIntent.getActivity(activity, requestID, resultIntent , PendingIntent.FLAG_IMMUTABLE)
            /*NavDeepLinkBuilder(context)
                .setGraph(R.navigation.nav_graph)
                .setDestination(R.id.timerFragment)
                .setComponentName(MainActivity::class.java)
                .createPendingIntent()*/
                //MY NEW IDEA TRIED BELOW, just crashes the app, yay



        if (sharedViewModel.hasNoSoundSet()){
            sharedViewModel.setSound(1)
        }
        soundID = sharedViewModel.soundPool.load(context,sharedViewModel.getSound(),1)

    }

    //override fun onNewIntent(intent: Intent?) {
       // super.onNewIntent(intent)
       //navController.handleDeepLink(intent)
   // }



    //------------------Open up interface to change interval---------------------------
    private fun onChangeInterval(view:View){
        val toChange=TimerFragmentDirections.actionTimerFragmentToSetIntervalFragment()
        view.findNavController().navigate(toChange)

    }
    //-----------------Open up settings-------------------------------------------------
    private fun onChangeButton(view: View) {
        val action=TimerFragmentDirections.actionTimerFragmentToSoundOptionsFragment()
        view.findNavController().navigate(action)

    }
    override fun onStart(){
        Log.d(f,"on start called")

        super.onStart()
    }
    //--------------Logic if app is stopped (leave screen or go to settings)----------
    override fun onStop() {
        super.onStop()
        Log.d(ost,"fragment stopped")
        TurnScreenOff()
        //start hard timer, stop chronometer, and set pause offset
        if (sharedViewModel.getClockRunning()){
            //set pause offet and start timer
            sharedViewModel.setClockOffset(elapsedRealtime() -binding.chronometer.base)
            sharedViewModel.setChronoBase(binding.chronometer.base)//same as pauseOffset
            timer()


            binding.chronometer.stop()

        }
    }


    //--------------------logic for when fragment is resumed-------------
    override fun onResume(){
        super.onResume()
        KeepScreenOn()
        Log.d(f, "on resume called")
        if (sharedViewModel.getClockRunning()){
            Log.d(f, "clock is running")
            sharedViewModel.setClockOffset(elapsedRealtime() - sharedViewModel.getChronoBase())
            interval=sharedViewModel.getInterval()
            runClock()
            if (sharedViewModel.beeps != null){
                sharedViewModel.beeps!!.cancel()
            }
            //cancel any notifications
            Notifier.cancelNotification(requireContext().applicationContext)

        }
    }


    //-------------Should not happen but logic for if app is destroyed
    override fun onDestroy(){
        Log.d(f,"fragment destroyed")
        sharedViewModel.beeps!!.cancel()
        TurnScreenOff()
        super.onDestroy()

    }
    companion object{
        const val isRunning_KEY="isRunning_KEY"
        const val lastChronoBase="lastChronoBase"
        const val GETINTERVAL="GETINTERVAL"
    }

    override fun onSaveInstanceState(outState:Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(isRunning_KEY,sharedViewModel.getClockRunning())
        outState.putLong(lastChronoBase, sharedViewModel.getChronoBase())
        outState.putInt(GETINTERVAL,sharedViewModel.getInterval())

        Log.d(f,"saveInstanceStateCalled")
    }





    //------------------start, stop and pause---------------
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




    private fun onResetButton(){
        binding.chronometer.base = elapsedRealtime()//resets back to 0
        sharedViewModel.setClockOffset(0)//resets the pause offset to 0

        if (sharedViewModel.getClockRunning()) {//if the user forgot to stop running first
            stopClock()
        }

    }

    private fun onPauseButton(){
        if (sharedViewModel.getClockRunning()) {
            Log.d(dC,"Pause Button called and chronometer base="+binding.chronometer.base.toString())
            Log.d(dC,"System elapsed real time="+ elapsedRealtime().toString())
            sharedViewModel.setClockOffset(elapsedRealtime() - binding.chronometer.base)
            stopClock()
        }
    }


    //----------------logic for chronometer to be running

    private fun runClock(){

        binding.chronometer.base= elapsedRealtime() - sharedViewModel.getClockOffset()
        Log.d(dC, "runClock called and base set to:"+binding.chronometer.base.toString())
        binding.chronometer.start()
        sharedViewModel.setClockRunning(true)
        if (sharedViewModel.getShift()==true){
            listenChronometer(interval-3)
        }else{
            listenChronometer(0)
        }
    }

    //logic for when clock is stopped
    //occurs after pause or reset button is hit

    private fun stopClock(){

        binding.chronometer.stop()
        sharedViewModel.setClockRunning(false)
        sharedViewModel.soundPool.stop(streamID)

    }

    //------------LISTEN CHRONOMETER-------------------
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

    //----------------------------------PLAY SOUND
    fun playSound() {
        streamID = sharedViewModel.soundPool.play(soundID, 1F, 1F, 1, 0, 1F)
    }




    //---------------------OFF SCREEN TIMER (when user navigates away)-----------
    private fun timer(){
        //assume is timer running is already set to true, as that is required to run this
        val context=requireContext().applicationContext
        val timerOffset: Long = elapsedRealtime() - binding.chronometer.base //recalculating pauseoffset

        Log.d(ost, "timeroffset"+timerOffset.toString())
        var intervalForTimer:Long=0
        if (sharedViewModel.getShift()==false) {
            intervalForTimer = interval.toLong() * 1000 - (timerOffset) % (interval * 1000)


        }else if (sharedViewModel.getShift()==true){
            intervalForTimer = interval.toLong() * 1000 - timerOffset % (interval * 1000)-3000

        }

        Log.d(ost,"Timer Called and interval for timer="+intervalForTimer.toString())
        Log.d(ost, "Notification Timer will be:"+(elapsedRealtime() - binding.chronometer.base).toString())

        sharedViewModel.beeps =startCoroutineTimer (intervalForTimer,interval.toLong()*1000,
            { playSound() })

        sharedViewModel.beeps.start()


        //createNotification channel and start timer if user is navigating offscreen

        Notifier.postNotification(sharedViewModel.notificationID,context,pendingIntent,binding.chronometer.base)






    }

    private fun startCoroutineTimer(delayMillis: Long = 0, repeatMillis: Long = 0, action: () -> Unit) = scope.launch(
        Dispatchers.IO) {
        delay(delayMillis)
        if (repeatMillis > 0) {
            while (true) {
                //Log.d(ost,"beep!")
                action()
                delay(repeatMillis)
            }
        } else {
            action()
        }
    }
    /*

    */
    //----NOTIFICATIONS DURING TIMER
    //Stop this for now
    /*
    private fun createNotificationChannel(){
        Log.d(n,"created")
        Log.d(n,"Build version="+Build.VERSION.SDK_INT.toString())
        Log.d(n,"Build version codes=" + Build.VERSION_CODES.O.toString())

        if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            Log.d(n,"made it inside")
            val name="Notification Title"
            val descriptionText="Notification Description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel= NotificationChannel(CHANNEL_ID,name,importance).apply{
                description=descriptionText

            }
            sharedViewModel.notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            sharedViewModel.notificationManager.createNotificationChannel(channel)
        }

    }*/

    //check out this link for opening form noti https://medium.com/androiddevelopers/navigating-with-deep-links-910a4a6588c






    //-----------functions to turn screens off and on
    private fun TurnScreenOff() {
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun KeepScreenOn(){
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    /*
    GRAVEYARD

     private fun createBuilder(timerOffset: Long): NotificationCompat.Builder{
        Log.d(n, "currentTimeMillis="+System.currentTimeMillis().toString())
        Log.d(n,"system clock elapsed real time="+elapsedRealtime().toString())
        Log.d(n,"offset="+timerOffset.toString())

        Log.d(comp, "timerOffset in timer:"+timerOffset.toString())
        Log.d(comp,"recalculating timer offset="+(elapsedRealtime() - binding.chronometer.base).toString())
        val context=requireContext().applicationContext

        Log.d(comp, "about to call pending intent")

        val builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setChannelId(CHANNEL_ID)
            .setSmallIcon(R.drawable.timer_icon)
            .setTicker("Ticker Text")
            .setWhen(System.currentTimeMillis()-(elapsedRealtime() - binding.chronometer.base))  // the time stamp, you will probably use System.currentTimeMillis() for most scenarios
            .setUsesChronometer(true)
            //.setContentIntent(pendingIntent)
            .setContentText("Timer Running")
        return builder
    }

     private fun startCoRoutineNotification(firstdelay:Long,timeRemaining: Long)=scope.launch(
    Dispatchers.IO) {
        myBuilder.setContentText("Time Remaining: "+timeRemaining.toString())

        Log.d(ost,"firstdelay="+firstdelay.toString())
        Log.d(ost,"newTime="+timeRemaining.toString())
        delay(firstdelay) //wont be a full second
        var newTime: Long=timeRemaining
        while(true){
            if (newTime==1L){
                newTime=interval.toLong()
                Log.d(ost, "interval="+interval.toString())
                Log.d(ost,"new time="+newTime.toString())
            }else{
                newTime-=1
            }

            myBuilder.setContentText("Time Remaining: "+newTime.toString())
            with(NotificationManagerCompat.from(requireContext())){
                notify(notificationID,myBuilder.build())
            }
            delay(1000)
        }

    }

     */



}

