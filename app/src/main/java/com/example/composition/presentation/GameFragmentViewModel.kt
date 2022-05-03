package com.example.composition.presentation

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Question
import com.example.composition.domain.usecases.GenerateQuestionUseCase
import com.example.composition.domain.usecases.GetGameSettingsUseCase

class GameFragmentViewModel:ViewModel() {
    private val repository = GameRepositoryImpl
    var question = MutableLiveData<Question>()
    val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    val getGameSettingsUseCase = GetGameSettingsUseCase(repository)
    private var _currentResult = MutableLiveData(Pair(0,0))
    val currentResult:LiveData<Pair<Int,Int>>
    get() = _currentResult
    var gameSettings = MutableLiveData<GameSettings>()

    lateinit var timer: MutableLiveData<CountDownTimer>

    fun checkAnswer(answer:Int, visibleCount:Int, sum:Int) {
        val countOfQuestions = _currentResult.value!!.first + 1
        var countOfRightAnswers = _currentResult.value!!.second
        if (answer + visibleCount == sum){
            countOfRightAnswers++
        }
        _currentResult.value = Pair(countOfQuestions,countOfRightAnswers)
    }



}