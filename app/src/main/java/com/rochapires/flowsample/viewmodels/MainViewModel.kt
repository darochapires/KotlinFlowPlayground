package com.rochapires.flowsample.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val _stateFlow = MutableStateFlow(0)
    val stateFlow = _stateFlow.asStateFlow()

    private val _sharedFlow = MutableSharedFlow<Int>(5)
    val sharedFlow = _sharedFlow.asSharedFlow()

    init {
        //uncomment to test state flow
        //collectFlow()

        squareNumber(3)
        viewModelScope.launch {
            sharedFlow.collect {
                delay(2000L)
                println("FIRST FLOW: The received number is $it")

            }
        }
        viewModelScope.launch {
            sharedFlow.collect {
                delay(3000L)
                println("SECOND FLOW: The received number is $it")

            }
        }
    }

    fun squareNumber(number: Int) {
        viewModelScope.launch {
            _sharedFlow.emit(number * number)
        }
    }

    fun incrementCounter() {
        _stateFlow.value += 1
    }

    val countDownFlow = flow {
        val startValue = 10
        var currentValue = startValue
        while (currentValue >= 0) {
            delay(1000L)
            currentValue--
            emit(currentValue)
        }
    }

    private fun collectFlow() {
        viewModelScope.launch {
            val count = countDownFlow
                .filter { time ->
                    time % 2 == 0
                }
                .map { time ->
                    time  + 2
                }
                .onEach { time ->
                    println(time)
                }
                .count {
                    it % 2 == 0
                }
            println("Count is $count")

            val reduceResult = countDownFlow
                .fold(100) { accumulator, value ->
                    accumulator + value
                }

            val flow = flow {
                delay(250L)
                emit("Appetizer")
                delay(1000L)
                emit("Main Dish")
                delay(100L)
                emit("Dessert")
            }
            viewModelScope.launch {
                flow.onEach { dish ->
                    println("FLOW: $dish is delivered")
                }
                    .buffer()
                    .collect{ dish ->
                        println("FLOW: Now eating $dish")
                        delay(1500L)
                        println("FLOW: Finished eating $dish")
                    }
            }
        }
    }
}