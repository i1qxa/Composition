package com.example.composition.presentation

import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.example.composition.R

@BindingAdapter("requiredAnswers")
fun bindRequiredAnswers(textView: TextView,count:Int){
    textView.text = String.format(textView.context.getString(R.string.required_score,
        count.toString()))
}
@BindingAdapter("requiredPercent")
fun bindRequiredPercent(textView: TextView,count:Int){
    textView.text = String.format(textView.context.getString(R.string.required_percentage,
        count.toString()))
}
@BindingAdapter("countAnswers")
fun bindAnswers(textView: TextView,count:Int){
    textView.text = String.format(textView.context.getString(R.string.score_answers,
        count.toString()))
}
@BindingAdapter("percent")
fun bindPercent(textView: TextView,count:Int){
    textView.text = String.format(textView.context.getString(R.string.score_percentage,
        count.toString()))
}
@BindingAdapter("changeImage")
fun bindImage(imageView: ImageView,isWin:Boolean){
    imageView.setImageResource(
        if (isWin)    R.drawable.ic_smile
        else R.drawable.ic_sad
    )
}