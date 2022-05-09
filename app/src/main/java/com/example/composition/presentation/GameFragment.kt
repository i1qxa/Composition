package com.example.composition.presentation

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import java.lang.RuntimeException

class GameFragment : Fragment() {
    private lateinit var viewModel: GameFragmentViewModel
    private lateinit var level: Level
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[GameFragmentViewModel::class.java]
        viewModel.gameSettings.value = viewModel.getGameSettingsUseCase(level)
        generateNewQuestion()
        observeViewModel()
        viewModel.startTimer((viewModel.gameSettings.value!!.gameTimeInSeconds * 1000).toLong())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun generateNewQuestion(){
        viewModel.question.value = viewModel.generateQuestionUseCase(viewModel.gameSettings.value?.maxSumValue
            ?: throw RuntimeException("Game Settings is empty"))
    }

    @SuppressLint("SetTextI18n")
    private fun observeViewModel(){
        viewModel.question.observe(viewLifecycleOwner){
            setupQuestionViews(it)
        }
        viewModel.currentResult.observe(viewLifecycleOwner){
            if (it.second >0) {
                val percentOfRightAnswers = viewModel.percentOfRightAnswers(it)
                binding.tvAnswersProgress.text = "Правильных ответов $percentOfRightAnswers % " +
                        "минимум(${viewModel.gameSettings.value?.minPercentOfRightAnswers}%)"
                binding.progressBar.setProgress(percentOfRightAnswers, true)
            }
        }
        viewModel.lastTime.observe(viewLifecycleOwner){
            binding.tvTimer.text = it.toString()
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
        binding.tvOption1.text = question.option[0].toString()
        binding.tvOption1.setOnClickListener {optionClickListener(question.option[0])}
        binding.tvOption2.text = question.option[1].toString()
        binding.tvOption2.setOnClickListener {optionClickListener(question.option[1])}
        binding.tvOption3.text = question.option[2].toString()
        binding.tvOption3.setOnClickListener {optionClickListener(question.option[2])}
        binding.tvOption4.text = question.option[3].toString()
        binding.tvOption4.setOnClickListener {optionClickListener(question.option[3])}
        binding.tvOption5.text = question.option[4].toString()
        binding.tvOption5.setOnClickListener {optionClickListener(question.option[4])}
        binding.tvOption6.text = question.option[5].toString()
        binding.tvOption6.setOnClickListener {optionClickListener(question.option[5])}
    }

    private fun optionClickListener(answer:Int){
        val visibleCount = binding.tvLeftNumber.text.toString().toInt()
        val sum = binding.tvSum.text.toString().toInt()
        viewModel.checkAnswer(answer, visibleCount,sum)
        generateNewQuestion()
    }

    private fun launchGameFinishFragment(gameResult: GameResult){

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    private fun parseArgs(){
        requireArguments().getParcelable<Level>(KEY_LEVEL)?. let {
            level = it
        }

    }

    companion object{

        private const val KEY_LEVEL = "level"

        fun newInstance(level:Level): GameFragment {
            return GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
        }
    }

}
