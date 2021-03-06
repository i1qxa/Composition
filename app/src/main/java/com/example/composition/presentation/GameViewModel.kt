package com.example.composition.presentation

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase
import java.lang.RuntimeException

class GameViewModel(application: Application, level: Level): AndroidViewModel(application) {
    private val repository = GameRepositoryImpl
    var question = MutableLiveData<Question>()
    val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private var _currentResult = MutableLiveData(Pair(0,0))
    val currentResult:LiveData<Pair<Int,Int>>
    get() = _currentResult
    var gameSettings = MutableLiveData<GameSettings>()
    val lastTime = MutableLiveData<String>()
    val isFinished = MutableLiveData(false)
    private var countOfQuestions = 0
    private var countOfRightAnswers = 0

    init {
        gameSettings.value = getGameSettingsUseCase(level)
        generateQuestion()
        _currentResult.value = Pair(0,0)
        startTimer(gameSettings.value!!.gameTimeInSeconds.toLong() * 1000)
    }

    fun generateQuestion(){
        question.value = generateQuestionUseCase(gameSettings.value!!.maxSumValue)
    }

    fun checkAnswer(answer:Int) {
        countOfQuestions = _currentResult.value!!.first + 1
        countOfRightAnswers = _currentResult.value!!.second
        if (answer + question.value!!.visibleNumber == question.value!!.sum){
            countOfRightAnswers++
        }
        _currentResult.value = Pair(countOfQuestions,countOfRightAnswers)
        generateQuestion()
    }

    fun percentOfRightAnswers(pair: Pair<Int,Int>): Int{
        return if(pair.first<=0) 0
        else ((pair.second.toDouble() / pair.first.toDouble()) * 100).toInt()
    }


    fun startTimer(timeForGameRound:Long){
        val timer = object : CountDownTimer(timeForGameRound ,1000){
            override fun onTick(p0: Long) {
                lastTime.value = formatTime(p0)
            }

            override fun onFinish() {
                isFinished.value = true
            }
        }
        timer.start()
    }

    private fun formatTime(time:Long):String{
        val seconds = time / MILLIS_IN_SECONDS
        val minutes = seconds / SECONDS_IN_MINUTES
        val lastSeconds = seconds - (minutes * SECONDS_IN_MINUTES)
        return String.format("%02d:%02d",minutes, lastSeconds)

    }

    fun getReadyGameResult():GameResult{
        if (gameSettings.value == null){
            throw RuntimeException("Game Settings is empty")
        }
        if (!checkGameResult()){
            return GameResult(0,0, gameSettings.value!!)
        }
        val currentRes = currentResult.value!!
        return GameResult(currentRes.second, currentRes.first,gameSettings.value!!)
    }
    private fun checkGameResult():Boolean{
        return currentResult.value != null
    }

    companion object{
        private const val MILLIS_IN_SECONDS = 1000L
        private const val SECONDS_IN_MINUTES = 60
    }

}