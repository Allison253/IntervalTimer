package com.example.intervaltimer


import android.media.AudioAttributes
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.example.intervaltimer.databinding.FragmentSoundOptionsBinding


/**
 * A simple [Fragment] subclass.
 * Use the [SoundOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SoundOptionsFragment : Fragment() {
    private val sharedViewModel:SoundViewModel by activityViewModels()
    private lateinit var binding:FragmentSoundOptionsBinding

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    var soundID1:Int=0
    var soundID2:Int=0
    var soundID3:Int=0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val fragmentBinding=FragmentSoundOptionsBinding.inflate(inflater,container  ,false)
            // inflate(inflater, container, false)
        binding=fragmentBinding
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)

        binding?.apply{
            lifecycleOwner=viewLifecycleOwner
            viewModel=sharedViewModel
            soundOptionsFragment=this@SoundOptionsFragment
        }


        radioButton1= binding?.radioButton1!!
        radioButton2=binding?.radioButton2!!
        radioButton3=binding?.radioButton3!!
        radioGroup=binding?.RadioGroup1!!
        binding?.button!!.setOnClickListener{onBackButton(view)}


        if (sharedViewModel.hasNoSoundSet()==true){

            radioGroup.check(radioButton1.id)
        }
        else{
            if (sharedViewModel.getSound()==R.raw.beep1){
                radioGroup.check(radioButton1.id)
            }
            else if (sharedViewModel.getSound()==R.raw.tingsha_cymbal){
                radioGroup.check(radioButton2.id)
            }else{
                radioGroup.check(radioButton3.id)
            }
        }

        soundID1=sharedViewModel.soundPool.load(context,R.raw.beep1,1)
        soundID2=sharedViewModel.soundPool.load(context,R.raw.tingsha_cymbal,1)
        soundID3=sharedViewModel.soundPool.load(context,R.raw.four_beeps,1)




        radioGroup.setOnCheckedChangeListener { radioGroup, isChecked ->
            var selectedId=radioGroup.checkedRadioButtonId

            if (selectedId==radioButton1.id){
                sharedViewModel.setSound(1)
                sharedViewModel.soundPool.play(soundID1,1F,1F,5,0,1F)

            }else if(selectedId==radioButton2.id){
                sharedViewModel.setSound(2)
                sharedViewModel.soundPool.play(soundID2,1F,1F,1,0,1F)
            }else if(selectedId==radioButton3.id){
                sharedViewModel.setSound(3)
                sharedViewModel.soundPool.play(soundID3,1F,1F,1,0,1F)
            }



        }
    }


    private fun onBackButton(view: View) {
        val action=SoundOptionsFragmentDirections.actionSoundOptionsFragmentToTimerFragment()
        view.findNavController().navigate(action)
    }

}