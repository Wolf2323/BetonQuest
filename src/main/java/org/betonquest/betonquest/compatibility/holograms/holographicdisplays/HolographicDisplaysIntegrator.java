package org.betonquest.betonquest.compatibility.holograms.holographicdisplays;

import me.filoghost.holographicdisplays.api.HolographicDisplaysAPI;
import me.filoghost.holographicdisplays.api.hologram.Hologram;
import me.filoghost.holographicdisplays.api.hologram.PlaceholderSetting;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.holograms.BetonHologram;
import org.betonquest.betonquest.compatibility.holograms.HologramIntegrator;
import org.betonquest.betonquest.compatibility.holograms.HologramProvider;
import org.betonquest.betonquest.id.VariableID;
import org.betonquest.betonquest.instruction.Instruction;
import org.betonquest.betonquest.kernel.processor.quest.VariableProcessor;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import java.util.regex.Matcher;

/**
 * Integrates with HolographicDisplays.
 */
public class HolographicDisplaysIntegrator extends HologramIntegrator {
    /**
     * The plugin instance of BetonQuest.
     */
    private final Plugin plugin;

    /**
     * The logger factory used to create loggers for this class.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The variable processor used to process variables in holograms.
     */
    private final VariableProcessor variableProcessor;

    /**
     * The profile provider used to get player profiles.
     */
    private final ProfileProvider profileProvider;

    /**
     * Creates a new HolographicDisplaysIntegrator for HolographicDisplays.
     *
     * @param plugin            the plugin instance of BetonQuest
     * @param config            zhe config accessor for the plugin
     * @param loggerFactory     the logger factory used to create loggers
     * @param log               the logger that will be used for logging
     * @param variableProcessor the variable processor used to process variables in holograms
     * @param profileProvider   the profile provider used to get player profiles
     */
    public HolographicDisplaysIntegrator(final Plugin plugin, final ConfigAccessor config, final BetonQuestLoggerFactory loggerFactory,
                                         final BetonQuestLogger log, final VariableProcessor variableProcessor,
                                         final ProfileProvider profileProvider) {
        super(config, "HolographicDisplays", "3.0.0", "SNAPSHOT-b");
        this.plugin = plugin;
        this.loggerFactory = loggerFactory;
        this.log = log;
        this.variableProcessor = variableProcessor;
        this.profileProvider = profileProvider;
    }

    @Override
    public BetonHologram createHologram(final Location location) {
        final Hologram hologram = HolographicDisplaysAPI.get(plugin).createHologram(location);
        hologram.setPlaceholderSetting(PlaceholderSetting.ENABLE_ALL);
        return new HolographicDisplaysHologram(hologram);
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) throws HookException {
        super.hook(questTypeRegistries, featureRegistries);
        if (!Bukkit.getPluginManager().isPluginEnabled("ProtocolLib")) {
            log.warn("Holograms from HolographicDisplays won't be able to hide from players without ProtocolLib plugin! "
                    + "Install it to use conditioned holograms.");
        }
        final HolographicDisplaysAPI api = HolographicDisplaysAPI.get(plugin);
        api.registerIndividualPlaceholder("bq", new HologramPlaceholder(
                loggerFactory.create(HologramPlaceholder.class), variableProcessor, profileProvider));
        api.registerGlobalPlaceholder("bqg", new HologramGlobalPlaceholder(
                loggerFactory.create(HologramGlobalPlaceholder.class), variableProcessor));
    }

    @Override
    public String parseVariable(final QuestPackage pack, final String text) {
        /* We must convert a normal BetonQuest variable such as "%pack:objective.kills.left%" to
           "{bq:pack:objective.kills.left}" which is parsed by HolographicDisplays as a custom API placeholder. */
        final Matcher matcher = HologramProvider.VARIABLE_VALIDATOR.matcher(text);
        return matcher.replaceAll(match -> {
            final String group = match.group();
            try {
                final VariableID variable = new VariableID(pack, group);
                final Instruction instruction = variable.getInstruction();
                final String prefix = variableProcessor.get(variable).allowsPlayerless() ? "{bqg:" : "{bq:";
                return prefix + variable.getPackage().getQuestPath() + ":" + instruction + "}";
            } catch (final QuestException exception) {
                log.warn("Could not create variable '" + group + "' variable: " + exception.getMessage(), exception);
            }
            return group;
        });
    }
}
