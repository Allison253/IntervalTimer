package com.example.intervaltimer


import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.activityViewModels
import com.example.intervaltimer.databinding.FragmentSoundOptionsBinding


/**
 * A simple [Fragment] subclass.
 * Use the [SoundOptionsFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SoundOptionsFragment : Fragment() {
    private val sharedViewModel:SoundViewModel by activityViewModels()
    private var _binding:FragmentSoundOptionsBinding?=null
    private val binding get() = _binding!!
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton1: RadioButton
    private lateinit var radioButton2: RadioButton
    private lateinit var radioButton3: RadioButton
    //set up sound options
    var soundID:Int=0



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding=FragmentSoundOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){

        radioButton1=binding.radioButton1
        radioButton2=binding.radioButton2
        radioButton3=binding.radioButton3
        radioGroup=binding.RadioGroup1

        radioGroup.setOnCheckedChangeListener { radioGroup, isChecked ->
            var selectedId=radioGroup.checkedRadioButtonId
            Log.d("soundOptionsFragment","selectedId="+selectedId)

            if (selectedId==radioButton1.id){
                sharedViewModel.setSound(1)
                soundID=sharedViewModel.soundPool.load(context,sharedViewModel.getSound(),1)
                sharedViewModel.soundPool.play(soundID,1F,1F,1,0,1F)
                Log.d("playSound","playSound= "+soundID)
            }else if(selectedId==radioButton2.id){
                sharedViewModel.setSound(2)
                soundID=sharedViewModel.soundPool.load(context,sharedViewModel.getSound(),1)
                sharedViewModel.soundPool.play(soundID,1F,1F,1,0,1F)
                Log.d("playSound","playSound= "+soundID)
            }



        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}