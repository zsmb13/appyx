package com.bumble.appyx.interactions.core.model.transition

import com.bumble.appyx.interactions.core.Element
import com.bumble.appyx.interactions.core.plugin.SavesInstanceState
import java.io.Serializable
import kotlinx.coroutines.flow.StateFlow

interface TransitionModel<InteractionTarget, ModelState> : SavesInstanceState {

    val output: StateFlow<Output<ModelState>>

    sealed class Output<ModelState> : Serializable {
        abstract val currentTargetState: ModelState

        abstract val lastTargetState: ModelState

        abstract fun deriveKeyframes(stateTransition: StateTransition<ModelState>): Keyframes<ModelState>

        abstract fun deriveUpdate(stateTransition: StateTransition<ModelState>): Update<ModelState>

    }

    val elements: StateFlow<Set<Element<InteractionTarget>>>

    fun relaxExecutionMode()

    fun cleanUpElement(element: Element<InteractionTarget>)

    fun operation(
        operation: Operation<ModelState>,
        overrideMode: Operation.Mode? = null
    ): Boolean

    fun setProgress(progress: Float)

    fun onSettled(direction: SettleDirection, animate: Boolean = false)

    enum class SettleDirection {
        REVERT, COMPLETE
    }
}