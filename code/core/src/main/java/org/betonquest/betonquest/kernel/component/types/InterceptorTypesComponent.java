package org.betonquest.betonquest.kernel.component.types;

import org.betonquest.betonquest.api.dependency.DependencyProvider;
import org.betonquest.betonquest.conversation.interceptor.NonInterceptingInterceptorFactory;
import org.betonquest.betonquest.conversation.interceptor.SimpleInterceptorFactory;
import org.betonquest.betonquest.kernel.registry.feature.InterceptorRegistry;
import org.betonquest.betonquest.lib.dependency.component.AbstractCoreComponent;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading interceptor types.
 */
public class InterceptorTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new InterceptorTypesComponent.
     */
    public InterceptorTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(InterceptorRegistry.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final InterceptorRegistry interceptorRegistry = getDependency(InterceptorRegistry.class);

        interceptorRegistry.register("simple", new SimpleInterceptorFactory());
        interceptorRegistry.register("none", new NonInterceptingInterceptorFactory());
    }
}
