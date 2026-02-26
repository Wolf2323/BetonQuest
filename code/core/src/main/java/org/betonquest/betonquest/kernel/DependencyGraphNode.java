package org.betonquest.betonquest.kernel;

import java.util.Set;

/**
 * Represents a node in the dependency graph.
 */
public interface DependencyGraphNode {

    /**
     * Specifies an unmodifiable set of types that this node requires to be loaded before itself.
     * Essentially contains all classes that are dependencies of this node.
     *
     * @return all dependencies of this node
     */
    Set<Class<?>> requires();

    /**
     * Specifies an unmodifiable set of types that this node creates and handles in the loading process.
     * Essentially contains all classes that are provided by this node.
     *
     * @return all provided types of this node
     */
    Set<Class<?>> provides();
}
