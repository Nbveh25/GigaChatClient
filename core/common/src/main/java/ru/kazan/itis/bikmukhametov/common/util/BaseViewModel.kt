package ru.kazan.itis.bikmukhametov.common.util

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

abstract class BaseViewModel<State, Action>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state = _state.asStateFlow()

    abstract fun onAction(action: Action)

    protected fun updateState(reducer: State.() -> State) {
        _state.value = _state.value.reducer()
    }
}
