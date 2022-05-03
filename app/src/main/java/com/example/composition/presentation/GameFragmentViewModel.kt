package com.example.composition.presentation

import androidx.lifecycle.ViewModel
import com.example.composition.data.GameRepositoryImpl

class GameFragmentViewModel:ViewModel() {
    private val repository = GameRepositoryImpl
}