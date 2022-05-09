package com.example.composition.presentation

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase
import java.lang.RuntimeException

class GameFragmentViewModel:ViewModel() {
    private val repository = GameRepositoryImpl
    var question = MutableLiveData<Question>()
    val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private var _currentResult = MutableLiveData(Pair(0,0))
    val currentResult:LiveData<Pair<Int,Int>>
    get() = _currentResult
    var gameSettings = MutableLiveData<GameSettings>()
    val lastTime = MutableLiveData<Int>()
    val isFinished = MutableLiveData(false)

    fun checkAnswer(answer:Int, visibleCount:Int, sum:Int) {
        val countOfQuestions = _currentResult.value!!.first + 1
        var countOfRightAnswers = _currentResult.value!!.second
        if (answer + visibleCount == sum){
            countOfRightAnswers++
        }
        _currentResult.value = Pair(countOfQuestions,countOfRightAnswers)
    }

    fun percentOfRightAnswers(pair: Pair<Int,Int>): Int{
        return ((pair.second.toDouble() / pair.first.toDouble()) * 100).toInt()

    }

    fun startTimer(timeForGameRound:Long){
        val timer = object : CountDownTimer(timeForGameRound,1000){
            override fun onTick(p0: Long) {
                lastTime.value = p0.toInt() / 1000
            }

            override fun onFinish() {
                isFinished.value = true
            }
        }
        timer.start()
    }

    fun getReadyGameResult():GameResult{
        if (gameSettings.value == null){
            throw RuntimeException("Game Settings is empty")
        }
        if (!checkGameResult()){
            return GameResult(false,0,0, gameSettings.value!!)
        }
        val minPercentOfRightAnswers = gameSettings.value!!.minPercentOfRightAnswers
        val minCountOfRightAnswers = gameSettings.value!!.minCountOfRightAnswers
        val currentRes = currentResult.value!!
        val isWin = percentOfRightAnswers(currentRes) >= minPercentOfRightAnswers &&
                currentRes.first >= minCountOfRightAnswers
        return GameResult(isWin,currentRes.first, currentRes.second,gameSettings.value!!)
    }
    private fun checkGameResult():Boolean{
        return currentResult.value != null
    }


}