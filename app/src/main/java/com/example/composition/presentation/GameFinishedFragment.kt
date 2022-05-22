package com.example.composition.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult
import java.lang.RuntimeException

class GameFinishedFragment : Fragment() {
    private lateinit var gameResult:GameResult

    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
    get() = _binding ?: throw RuntimeException("FragmentGameFinishedBinding")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            buttonRetry.setOnClickListener {
                retryGame()
            }
            tvRequiredAnswers.text = getString(R.string.required_score,
                gameResult.gameSettings.minCountOfRightAnswers.toString())
            tvRequiredPercentage.text = getString(R.string.required_percentage,
                gameResult.gameSettings.minPercentOfRightAnswers.toString())
            tvScorePercentage.text = getString(R.string.score_percentage,
                gameResult.percentOfRightAnswers.toString())
            tvScoreAnswers.text = getString(R.string.score_answers,
                gameResult.countOfRightAnswers.toString())
            emojiResult.setImageResource(
                if (gameResult.isWin)R.drawable.ic_smile
                else R.drawable.ic_sad
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun parseArgs(){
        requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT)?.let {
            gameResult = it
        }
    }

    private fun retryGame(){
        findNavController().popBackStack()
    }

    companion object{
        const val KEY_GAME_RESULT = "game_result"
        fun newInstance(gameResult: GameResult): GameFinishedFragment {
            return GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_GAME_RESULT,gameResult)
                }
            }
        }
    }

}
