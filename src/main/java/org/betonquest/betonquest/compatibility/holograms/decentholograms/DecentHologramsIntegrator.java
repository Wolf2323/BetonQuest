package org.betonquest.betonquest.compatibility.holograms.decentholograms;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.UUID;
import java.util.regex.Matcher;

/**
 * Integrates with DecentHolograms.
 */
public class DecentHologramsIntegrator extends HologramIntegrator {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * Creates a new DecentHologramsIntegrator for DecentHolograms.
     *
     * @param config        the config accessor for the plugin
     * @param loggerFactory the logger factory.
     */
    public DecentHologramsIntegrator(final ConfigAccessor config, final BetonQuestLoggerFactory loggerFactory) {
        super(config, "DecentHolograms", "2.7.5");
        this.log = loggerFactory.create(getClass());
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = DHAPI.createHologram(UUID.randomUUID().toString(), location);
        hologram.enable();
        return new DecentHologramsHologram(hologram);
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) throws HookException {
        super.hook(questTypeRegistries, featureRegistries);
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            log.warn("Holograms from DecentHolograms will not be able to use BetonQuest variables in text lines "
                    + "without PlaceholderAPI plugin! Install it to use holograms with variables!");
        }
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack.objective.kills.left%" to
           "%betonquest_pack:objective.kills.left%" which is parsed by DecentHolograms as a PlaceholderAPI placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            try {
                final VariableID variable = new VariableID(pack, group);
                final Instruction instruction = variable.getInstruction();
                return "%betonquest_" + variable.getPackage().getQuestPath() + ":" + instruction + "%";
            } catch (final QuestException exception) {
                log.warn("Could not create variable '" + group + "' variable: " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
