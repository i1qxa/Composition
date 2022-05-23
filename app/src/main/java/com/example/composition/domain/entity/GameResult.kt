package com.example.composition.domain.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class GameResult(
    val countOfRightAnswers: Int,
    val countOfQuestions: Int,
    val gameSettings: GameSettings
):Parcelable{
    val percentOfRightAnswers =
        if(countOfQuestions<=0) 0
        else ((countOfRightAnswers.toDouble()/countOfQuestions)*100).toInt()
    val isWin = (countOfRightAnswers>=gameSettings.minCountOfRightAnswers)&&
            (percentOfRightAnswers>=gameSettings.minPercentOfRightAnswers)
    val countOfRightAnswersString:String
        get() = countOfRightAnswers.toString()
    val percentOfRightAnswersString:String
        get() = percentOfRightAnswers.toString()

}