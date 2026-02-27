package org.betonquest.betonquest.lib.dependency;

import org.betonquest.betonquest.api.dependency.DependencyGraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DependencyGraphTest {

    private DependencyGraph<DependencyGraphNode> graph;

    private DependencyGraphNode node;

    private static DependencyGraphNode node(final Set<Class<?>> requirements, final Set<Class<?>> provided) {
        final DependencyGraphNode mock = mock(DependencyGraphNode.class);
        lenient().when(mock.requires()).thenReturn(requirements);
        lenient().when(mock.provides()).thenReturn(provided);
        return mock;
    }

    @BeforeEach
    void setUp() {
        final Map<DependencyGraphNode, Integer> inDegree = new HashMap<>();
        node = node(Set.of(), Set.of());
        inDegree.put(node, 3);
        inDegree.put(node(Set.of(), Set.of()), 2);
        final Map<DependencyGraphNode, Set<DependencyGraphNode>> dependents = new HashMap<>();
        dependents.put(node, Set.of(node(Set.of(), Set.of()), node(Set.of(), Set.of())));
        graph = new DependencyGraph<>(inDegree, dependents);
    }

    @Test
    void can_reduce_indegree() {
        assertEquals(3, graph.inDegree().get(node), "Indegree should be 3");
        graph.decrementInDegree(node);
        assertEquals(2, graph.inDegree().get(node), "Indegree should be 2");
    }

    @Test
    void there_are_no_zero_indegree_nodes_and_two_positive_indegree_nodes() {
        assertEquals(2, graph.getNodesWithPositiveInDegree().size(), "There should be two positive indegree nodes");
        assertEquals(0, graph.getNodesWithZeroInDegree().size(), "There should be no zero-in-degrees");
    }

    @Test
    void after_decrement_there_is_one_positive_indegree_node_left() {
        graph.decrementInDegree(node);
        graph.decrementInDegree(node);
        graph.decrementInDegree(node);
        assertEquals(1, graph.getNodesWithPositiveInDegree().size(), "There should be one positive indegree node");
        assertEquals(1, graph.getNodesWithZeroInDegree().size(), "There should be one zero-in-degree node");
    }
}
