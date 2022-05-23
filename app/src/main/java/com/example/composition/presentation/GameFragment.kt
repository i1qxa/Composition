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
    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
        }
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
        observeViewModel()
        }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    @SuppressLint("SetTextI18n")
    private fun observeViewModel(){
        viewModel.question.observe(viewLifecycleOwner){
            setupQuestionViews(it)
        }
        viewModel.currentResult.observe(viewLifecycleOwner){
            binding.tvAnswersProgress.text = getString(R.string.progress_answers,it.second.toString(),
            viewModel.gameSettings.value!!.minCountOfRightAnswers.toString())
            binding.progressBar.setProgress(viewModel.percentOfRightAnswers(it), true)
        }
        viewModel.lastTime.observe(viewLifecycleOwner){
            binding.tvTimer.text = it
        }
        viewModel.isFinished.observe(viewLifecycleOwner){
            if (it){
                    launchGameFinishFragment(viewModel.getReadyGameResult())
               }
        }
    }



    private fun setupQuestionViews(question: Question){
        binding.tvSum.text = question.sum.toString()
        binding.tvLeftNumber.text = question.visibleNumber.toString()
        for (i in 0 until tvOptions.size){
            tvOptions[i].text = question.option[i].toString()
            tvOptions[i].setOnClickListener { optionClickListener(question.option[i]) }
        }

    }

    private fun optionClickListener(answer:Int){
        val visibleCount = binding.tvLeftNumber.text.toString().toInt()
        val sum = binding.tvSum.text.toString().toInt()
        viewModel.run {
            checkAnswer(answer, visibleCount,sum)
        }
        viewModel.generateQuestion()
    }

    private fun launchGameFinishFragment(gameResult: GameResult){
        findNavController().navigate(
            GameFragmentDirections.actionGameFragmentToGameFinishedFragment(gameResult)
        )
    }
}
