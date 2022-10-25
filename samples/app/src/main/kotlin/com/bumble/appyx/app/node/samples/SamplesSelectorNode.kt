package com.bumble.appyx.app.node.samples

import android.os.Parcelable
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.dp
import com.bumble.appyx.app.node.cards.CardsExampleNode
import com.bumble.appyx.app.node.slideshow.WhatsAppyxSlideShow
import com.bumble.appyx.core.composable.ChildRenderer
import com.bumble.appyx.core.integrationpoint.LocalIntegrationPoint
import com.bumble.appyx.core.modality.BuildContext
import com.bumble.appyx.core.navigation.EmptyNavModel
import com.bumble.appyx.core.node.Node
import com.bumble.appyx.core.node.ParentNode
import com.bumble.appyx.core.node.node
import com.bumble.appyx.sample.navigtion.compose.ComposeNavigationRoot
import kotlinx.parcelize.Parcelize

class SamplesSelectorNode(
    buildContext: BuildContext,
    private val outputFunc: (Output) -> Unit
) : ParentNode<SamplesSelectorNode.NavTarget>(
    navModel = EmptyNavModel<NavTarget, Unit>(),
    buildContext = buildContext
) {
    sealed class NavTarget : Parcelable {
        @Parcelize
        object OnboardingScreen : NavTarget()

        @Parcelize
        object ComposeNavigationScreen : NavTarget()

        @Parcelize
        object CardsExample : NavTarget()
    }

    sealed class Output {
        object OpenCardsExample : Output()
        object OpenOnboarding : Output()
        object OpenComposeNavigation : Output()
    }

    @ExperimentalUnitApi
    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    override fun resolve(navTarget: NavTarget, buildContext: BuildContext): Node =
        when (navTarget) {
            is NavTarget.CardsExample -> CardsExampleNode(buildContext)
            is NavTarget.OnboardingScreen -> WhatsAppyxSlideShow(
                buildContext = buildContext,
                autoAdvanceDelayMs = 2500
            )
            is NavTarget.ComposeNavigationScreen -> {
                node(buildContext) {
                    // compose-navigation fetches the integration point via LocalIntegrationPoint
                    CompositionLocalProvider(
                        LocalIntegrationPoint provides integrationPoint,
                    ) {
                        ComposeNavigationRoot()
                    }
                }
            }
        }

    @Composable
    override fun View(modifier: Modifier) {
        val decorator: @Composable (child: ChildRenderer) -> Unit = remember {
            { childRenderer ->
                ScaledLayout {
                    childRenderer.invoke()
                }
            }
        }
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top)

        ) {
            item {
                SampleItem(
                    title = "Dating cards NavModel",
                    subtitle = "Swipe right on the NavModel concept",
                    onClick = { outputFunc(Output.OpenCardsExample) },
                ) {
                    PermanentChild(
                        navTarget = NavTarget.CardsExample,
                        decorator = decorator
                    )
                }
            }
            item {
                SampleItem(
                    title = "What is Appyx?",
                    subtitle = "Explore some of the main ideas of Appyx in a set of slides",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(16f / 9),
                    onClick = { outputFunc(Output.OpenOnboarding) },
                ) {
                    PermanentChild(
                        navTarget = NavTarget.OnboardingScreen,
                        decorator = decorator
                    )
                }
            }
            item {
                SampleItem(
                    title = "Compose Navigation",
                    subtitle = "See Appyx nodes interact with Jetpack Compose Navigation library",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(16f / 9),
                    onClick = { outputFunc(Output.OpenComposeNavigation) },
                ) {
                    PermanentChild(
                        navTarget = NavTarget.ComposeNavigationScreen,
                        decorator = decorator
                    )
                }
            }
        }
    }
}
