package com.bumble.appyx.v2.core.lifecycle

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

internal class NodeLifecycleImpl(owner: LifecycleOwner) : NodeLifecycle {

    private val lifecycleRegistry = LifecycleRegistry(owner)

    override fun getLifecycle(): Lifecycle =
        lifecycleRegistry

    override fun updateLifecycleState(state: Lifecycle.State) {
        lifecycleRegistry.currentState = state
    }

}
