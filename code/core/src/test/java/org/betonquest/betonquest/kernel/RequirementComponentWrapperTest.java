package org.betonquest.betonquest.kernel;

import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.kernel.component.ActionsComponent;
import org.betonquest.betonquest.kernel.component.ArgumentParsersComponent;
import org.betonquest.betonquest.kernel.component.AsyncSaverComponent;
import org.betonquest.betonquest.kernel.component.CancelersComponent;
import org.betonquest.betonquest.kernel.component.ConditionsComponent;
import org.betonquest.betonquest.kernel.component.FontRegistryComponent;
import org.betonquest.betonquest.kernel.component.InstructionsComponent;
import org.betonquest.betonquest.kernel.component.ObjectivesComponent;
import org.betonquest.betonquest.kernel.component.PlaceholdersComponent;
import org.betonquest.betonquest.logger.util.BetonQuestLoggerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(BetonQuestLoggerService.class)
@ExtendWith(MockitoExtension.class)
class RequirementComponentWrapperTest {

    private static final List<Class<?>> RANDOM_CLASSES_TO_PICK = new ArrayList<>(List.of(ActionsComponent.class,
            ConditionsComponent.class, ObjectivesComponent.class, PlaceholdersComponent.class, ArgumentParsersComponent.class,
            AsyncSaverComponent.class, CancelersComponent.class, InstructionsComponent.class, FontRegistryComponent.class));

    private DefaultCoreComponentLoader loader;

    private CoreComponent dummyComponent;

    @Mock
    private BetonQuestLogger logger;

    private static Stream<Arguments> requirementOptions() {
        return SimpleSubSetHelper.allKSubSets(RANDOM_CLASSES_TO_PICK).stream().map(Arguments::of);
    }

    @BeforeEach
    void setUp() {
        loader = new DefaultCoreComponentLoader(logger);
        dummyComponent = new RawDummyComponent(false);
    }

    @Test
    void normal_without_Wrapper() {
        loader.register(dummyComponent);
        loader.load();
        assertTrue(dummyComponent.isLoaded(), "Component should be loaded");
    }

    @Test
    void normal_with_wrapper_success() {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, RequirementComponentWrapper.class);
        loader.register(wrapped);
        loader.init(RequirementComponentWrapper.class, mock(RequirementComponentWrapper.class));
        loader.load();
        assertTrue(dummyComponent.isLoaded(), "Component should be loaded");
    }

    @ParameterizedTest
    @MethodSource("requirementOptions")
    void normal_with_wrapper_fail(final Collection<Class<?>> requirementClasses) {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, requirementClasses.toArray(new Class<?>[0]));
        loader.register(wrapped);
        assertThrows(IllegalStateException.class, loader::load);
        assertFalse(dummyComponent.isLoaded(), "Component should not be loaded");
    }

    @ParameterizedTest
    @MethodSource("requirementOptions")
    void check_for_requirements(final Collection<Class<?>> requirementClasses) {
        final RequirementComponentWrapper wrapped = new RequirementComponentWrapper(dummyComponent, requirementClasses.toArray(new Class<?>[0]));
        assertTrue(requirementClasses.stream().allMatch(wrapped::requires), "Wrapped component should require all requirement classes");
        assertFalse(wrapped.canLoad(), "Wrapped component should not be loadable");
    }
}
