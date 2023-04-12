package com.bumble.appyx.transitionmodel.spotlight.operation

import androidx.compose.animation.core.AnimationSpec
import com.bumble.appyx.interactions.Parcelize
import com.bumble.appyx.interactions.RawValue
import com.bumble.appyx.interactions.core.model.transition.BaseOperation
import com.bumble.appyx.interactions.core.model.transition.Operation
import com.bumble.appyx.interactions.core.asElement
import com.bumble.appyx.transitionmodel.spotlight.Spotlight
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.CREATED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.DESTROYED
import com.bumble.appyx.transitionmodel.spotlight.SpotlightModel.State.ElementState.STANDARD

@Parcelize
// TODO cleanup SpotlightModel.State.positions if a position doesn't contain more elements
class UpdateElements<InteractionTarget : Any>(
    private val items: @RawValue List<InteractionTarget>,
    private val initialActiveIndex: Float? = null,
    override val mode: Operation.Mode = Operation.Mode.KEYFRAME
) : BaseOperation<SpotlightModel.State<InteractionTarget>>() {

    override fun isApplicable(state: SpotlightModel.State<InteractionTarget>): Boolean =
        true

    override fun createFromState(baseLineState: SpotlightModel.State<InteractionTarget>): SpotlightModel.State<InteractionTarget> =
        baseLineState.copy(
            positions = baseLineState.positions.mapIndexed { index, position ->
                position.copy(
                    elements = position.elements + (items[index].asElement() to CREATED)
                )
            },
        )

    override fun createTargetState(fromState: SpotlightModel.State<InteractionTarget>): SpotlightModel.State<InteractionTarget> =
        fromState.copy(
            positions = fromState.positions.map { position ->
                position.copy(
                    elements = position.elements.mapValues { (_, elementState) ->
                        when (elementState) {
                            CREATED -> STANDARD
                            STANDARD -> DESTROYED
                            DESTROYED -> DESTROYED
                        }
                    }
                )
            },
            activeIndex = initialActiveIndex ?: fromState.activeIndex
        )
}

fun <InteractionTarget : Any> Spotlight<InteractionTarget>.updateElements(
    items: List<InteractionTarget>,
    initialActiveIndex: Float? = null,
    animationSpec: AnimationSpec<Float> = defaultAnimationSpec,
    mode: Operation.Mode = Operation.Mode.KEYFRAME
) {
    operation(
        operation = UpdateElements(
            items = items,
            initialActiveIndex = initialActiveIndex,
            mode = mode
        ),
        animationSpec = animationSpec,
    )
}