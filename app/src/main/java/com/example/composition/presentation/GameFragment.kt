package com.example.composition.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import java.lang.RuntimeException

class GameFragment : Fragment() {
    private val args by navArgs<GameFragmentArgs>()
    private val viewModelFactory by lazy {
        GameViewModelFactory(args.level,requireActivity().application)}
    private val viewModel: GameViewModel by lazy {ViewModelProvider(
        this,viewModelFactory)[GameViewModel :: class.java]
    }
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
    get() = _binding ?: throw RuntimeException("FragmentGameBinding == null")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        observeViewModel()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("SetTextI18n")
    private fun observeViewModel(){
        viewModel.currentResult.observe(viewLifecycleOwner){
            binding.tvAnswersProgress.text = getString(R.string.progress_answers,it.second.toString(),
            viewModel.gameSettings.value!!.minCountOfRightAnswers.toString())
            }
        viewModel.isFinished.observe(viewLifecycleOwner){
            if (it){
                    launchGameFinishFragment(viewModel.getReadyGameResult())
               }
        }
    }
    private fun launchGameFinishFragment(gameResult: GameResult){
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToGameFinishedFragment(gameResult)
        )
    }
}
