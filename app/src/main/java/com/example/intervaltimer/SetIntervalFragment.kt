package com.example.intervaltimer


import android.os.Bundle
import android.view.LayoutInflater

import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.example.intervaltimer.databinding.FragmentSetIntervalBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class SetIntervalFragment : DialogFragment() {
    private val sharedViewModel:SoundViewModel by activityViewModels()
    private lateinit var binding :FragmentSetIntervalBinding



    override fun onCreateView(
        inflater:LayoutInflater, container:ViewGroup?,
        savedInstanceState:Bundle?
    ): View {

        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding= FragmentSetIntervalBinding.inflate(inflater,container,false)

        return binding.root}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        binding.popupWindowButton.setOnClickListener{
            setInterval()
            navBack()

        }
    }

    private fun setInterval(){
        var worked =true
        var minutes=0
        var seconds=0


        if (binding.minutes.text.toString()=="" && binding.seconds.text.toString()==""){
            Toast.makeText(context,"You did not set an interval!",Toast.LENGTH_LONG).show()
        }
        else{

            if (binding.minutes.text.toString()!=""){
                try{
                    minutes=Integer.parseInt(binding.minutes.text.toString())
                }catch(e: NumberFormatException){
                    worked=false
                }

            }
            if (binding.seconds.text.toString()!=""){
                try {
                    seconds=Integer.parseInt(binding.seconds.text.toString())
                }catch (e:NumberFormatException){
                    worked=false
                }

            }

            if (worked){
                sharedViewModel.setInterval(minutes,seconds)
                //activity?.onBackPressed()// THIS IS HAVING ISSUE WHERE IT KILLS THE APP!!! idk why. Seems it is grabbing main activity and pressing back button
                //it seems to think the "activity" I want to press back on is the main timer fragment to navigate back and destroy the fragment (exit out)
                //need to make sure it is actually like pressing the back button on the dialog fragment

            }else{
                Toast.makeText(context,"Error with Interval",Toast.LENGTH_LONG).show()
            }

        }

    }

    private fun navBack(){
        val action=SetIntervalFragmentDirections.actionSetIntervalFragmentToTimerFragment()
        //SoundOptionsFragmentDirections.actionSoundOptionsFragmentToTimerFragment()
        NavHostFragment.findNavController(this).navigate(action)
    }

}

