package org.betonquest.betonquest.compatibility.luckperms;

import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.IntegratorFactory;
import org.betonquest.betonquest.database.GlobalData;

/**
 * Factory for creating {@link LuckPermsIntegrator} instances.
 */
public class LuckPermsIntegratorFactory implements IntegratorFactory {

    /**
     * The global data.
     */
    private final GlobalData globalData;

    /**
     * Creates a new instance of the factory.
     *
     * @param globalData the global data
     */
    public LuckPermsIntegratorFactory(final GlobalData globalData) {
        this.globalData = globalData;
    }

    @Override
    public Integrator getIntegrator() {
        return new LuckPermsIntegrator(globalData);
    }
}
