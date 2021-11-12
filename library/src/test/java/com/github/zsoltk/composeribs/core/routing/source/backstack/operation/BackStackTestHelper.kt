package com.github.zsoltk.composeribs.core.routing.source.backstack.operation

import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStack.LocalRoutingKey
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackElement
import com.github.zsoltk.composeribs.core.routing.source.backstack.BackStackOperation

internal sealed class Routing {
    object Routing1 : Routing()
    object Routing2 : Routing()
    object Routing3 : Routing()
    data class Routing4(val dummy: String) : Routing()
}

internal fun <T : Routing> backStackElement(
    element: T,
    uuid: Int,
    fromState: BackStack.TransitionState,
    targetState: BackStack.TransitionState,
    operation: BackStackOperation<T>
) = BackStackElement(
    key = backStackKey(
        element = element,
        uuid = uuid
    ),
    fromState = fromState,
    targetState = targetState,
    operation = operation
)

internal fun <T : Routing> backStackKey(
    element: T,
    uuid: Int,
) = LocalRoutingKey(
    routing = element,
    uuid = uuid
)
