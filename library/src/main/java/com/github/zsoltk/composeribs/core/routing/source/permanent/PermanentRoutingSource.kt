package com.github.zsoltk.composeribs.core.routing.source.permanent

import android.os.Parcelable
import com.github.zsoltk.composeribs.core.SavedStateMap
import com.github.zsoltk.composeribs.core.routing.RoutingElement
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import com.github.zsoltk.composeribs.core.routing.RoutingSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

class PermanentRoutingSource<Key>(
    configuration: Set<Key> = emptySet(),
    savedStateMap: SavedStateMap?,
    private val key: String = PermanentRoutingSource::class.simpleName!!,
) : RoutingSource<Key, Int> {

    @Parcelize
    data class RoutingKeyImpl<Key>(
        override val routing: @RawValue Key,
    ) : RoutingKey<Key>, Parcelable

    constructor(
        savedStateMap: SavedStateMap?,
        vararg configuration: Key,
    ) : this(
        configuration = configuration.toSet(),
        savedStateMap = savedStateMap,
    )

    private val state = MutableStateFlow(
        savedStateMap.restore() ?: configuration.map { key ->
            RoutingElement(
                key = RoutingKeyImpl(routing = key),
                fromState = 0,
                targetState = 0,
            )
        }
    )

    override val all: StateFlow<List<RoutingElement<Key, Int>>>
        get() = state

    override val onScreen: StateFlow<List<RoutingElement<Key, Int>>>
        get() = state

    override val offScreen: StateFlow<List<RoutingElement<Key, Int>>> =
        MutableStateFlow(emptyList())

    override val canHandleBackPress: StateFlow<Boolean> =
        MutableStateFlow(false)

    override fun onBackPressed() {
        // no-op
    }

    override fun onTransitionFinished(key: RoutingKey<Key>) {
        // no-op
    }

    fun add(key: Key) {
        if (state.value.any { it.key == key }) return
        state.update { list ->
            if (list.any { it.key == key }) {
                list
            } else {
                list + RoutingElement(
                    key = RoutingKeyImpl(routing = key),
                    fromState = 0,
                    targetState = 0,
                )
            }
        }
    }

    override fun saveInstanceState(savedStateMap: MutableMap<String, Any>) {
        savedStateMap[key] = state.value
    }

    private fun SavedStateMap?.restore(): List<RoutingElement<Key, Int>>? =
        (this?.get(key) as? List<RoutingElement<Key, Int>>)

}
