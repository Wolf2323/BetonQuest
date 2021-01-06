package org.betonquest.betonquest.compatibility.fakeblock;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.compatibility.Integrator;


@SuppressWarnings("PMD.CommentRequired")
public class FakeBlockIntegrator implements Integrator {
    private final BetonQuest plugin;

    public FakeBlockIntegrator() {
        plugin = BetonQuest.getInstance();
    }

    @Override
    public void hook() {
        plugin.registerEvents("fakeblock", FakeBlockEvent.class);
    }

    @Override
    public void reload() {
        // Empty
    }

    @Override
    public void close() {
        // Empty
    }

}
