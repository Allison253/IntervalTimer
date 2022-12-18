package com.example.intervaltimer

import android.app.PendingIntent.getActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.intervaltimer.databinding.FragmentSetIntervalBinding
import com.example.intervaltimer.databinding.FragmentTimerBinding

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class SetIntervalFragment : DialogFragment() {
    private val sharedViewModel:SoundViewModel by activityViewModels()
    private lateinit var binding :FragmentSetIntervalBinding



    override fun onCreateView(
        inflater:LayoutInflater, container:ViewGroup?,
        savedInstanceState:Bundle?
    ): View? {
        (activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        binding= FragmentSetIntervalBinding.inflate(inflater,container,false)

        return binding.root}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        binding.popupWindowButton.setOnClickListener{
            setInterval()

        }
    }

    fun setInterval(){
        var minutes=0
        var seconds=0

        if (binding.seconds.text.toString()=="" && binding.minutes.text.toString()==""){
            Toast.makeText(context,"You did not set an interval!",Toast.LENGTH_LONG).show()
        }
        else{

            if (binding.minutes.text.toString()!=""){
                minutes=Integer.parseInt(binding.minutes.text.toString())
            }
            if (binding.seconds.text.toString()!=""){
                seconds=Integer.parseInt(binding.seconds.text.toString())
            }
            sharedViewModel.setInterval(minutes,seconds)
            activity?.onBackPressed()
        }

    }

}

