package com.example.composition.domain.entity

data class Question(
    val sum: Int,
    val visibleNumber: Int,
    val option: List<Int>
)