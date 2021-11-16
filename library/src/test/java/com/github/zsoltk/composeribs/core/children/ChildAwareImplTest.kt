package com.github.zsoltk.composeribs.core.children

import androidx.lifecycle.Lifecycle
import com.github.zsoltk.composeribs.core.node.Node
import com.github.zsoltk.composeribs.core.node.build
import com.github.zsoltk.composeribs.core.routing.RoutingKey
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

// TODO: test different child lifecycle combinations (except ones MinimumCombinedLifecycle already tested)
// Put here specific cases which should not be tested for both before/after registration
class ChildAwareImplTest : ChildAwareTestBase() {

    @Test
    fun `whenChildAttached is invoked for promoted to eager node`() {
        root = Root(childMode = ChildEntry.ChildMode.LAZY).build()
        var capturedNode: Node? = null
        root.whenChildAttached<Child1> { _, child ->
            capturedNode = child
        }
        val routingKey = RoutingKey<Configuration>(Configuration.Child1())
        add(routingKey)
        val node = root.childOrCreate(routingKey).node
        assertEquals(node, capturedNode)
    }

    @Test
    fun `whenChildAttached lifecycle is destroyed properly for promoted to lazy node`() {
        // TODO Later when implemented
    }

    @Test
    fun `whenChildrenAttached is invoked for promoted to eager nodes`() {
        root = Root(childMode = ChildEntry.ChildMode.LAZY).build()
        val capturedNodes = HashSet<Pair<Node, Node>>()
        root.whenChildrenAttached<Child1, Child2> { _, c1, c2 ->
            capturedNodes += c1 to c2
        }
        val routingKey1 = RoutingKey<Configuration>(Configuration.Child1(id = 0))
        val routingKey2 = RoutingKey<Configuration>(Configuration.Child1(id = 1))
        val routingKey3 = RoutingKey<Configuration>(Configuration.Child2(id = 0))
        add(routingKey1, routingKey2, routingKey3)
        val node1 = root.childOrCreate(routingKey1).node
        val node2 = root.childOrCreate(routingKey2).node
        val node3 = root.childOrCreate(routingKey3).node
        assertEquals(
            setOf(
                node1 to node3,
                node2 to node3,
            ),
            capturedNodes
        )
    }

    @Test
    fun `whenChildrenAttached lifecycle is destroyed properly for promoted to lazy node`() {
        // TODO Later when implemented
    }

    @Test
    fun `ignores registration when parent lifecycle is destroyed`() {
        val routingKey1 = RoutingKey<Configuration>(Configuration.Child1())
        add(routingKey1)
        var capturedNode: Node? = null
        root.updateLifecycleState(Lifecycle.State.DESTROYED)
        root.whenChildAttached<Child1> { _, child ->
            capturedNode = child
        }
        assertNull(capturedNode)
    }

}