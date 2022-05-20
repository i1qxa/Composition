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
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import java.lang.RuntimeException

class GameFragment : Fragment() {
    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(requireActivity().application)
        )[GameViewModel :: class.java]
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
