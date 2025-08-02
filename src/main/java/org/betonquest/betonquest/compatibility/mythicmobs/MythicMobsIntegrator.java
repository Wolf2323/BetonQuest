package org.betonquest.betonquest.compatibility.mythicmobs;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.compatibility.Compatibility;
import org.betonquest.betonquest.compatibility.HookException;
import org.betonquest.betonquest.compatibility.Integrator;
import org.betonquest.betonquest.compatibility.UnsupportedVersionException;
import org.betonquest.betonquest.compatibility.mythicmobs.condition.MythicMobDistanceConditionFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.event.MythicSpawnMobEventFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsInteractCatcher;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsNpcFactory;
import org.betonquest.betonquest.compatibility.mythicmobs.npc.MythicMobsReverseIdentifier;
import org.betonquest.betonquest.compatibility.mythicmobs.objective.MythicMobKillObjectiveFactory;
import org.betonquest.betonquest.compatibility.protocollib.hider.MythicHider;
import org.betonquest.betonquest.kernel.registry.feature.FeatureRegistries;
import org.betonquest.betonquest.kernel.registry.quest.NpcTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.QuestTypeRegistries;
import org.betonquest.betonquest.quest.PrimaryServerThreadData;
import org.betonquest.betonquest.versioning.UpdateStrategy;
import org.betonquest.betonquest.versioning.Version;
import org.betonquest.betonquest.versioning.VersionComparator;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

/**
 * Integrator for MythicMobs.
 */
public class MythicMobsIntegrator implements Integrator {

    /**
     * The plugin instance.
     */
    private final Plugin plugin;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The compatibility instance to use for checking other hooks.
     */
    private final Compatibility compatibility;

    /**
     * The server instance.
     */
    private final Server server;

    /**
     * The default constructor.
     *
     * @param plugin          the plugin instance
     * @param profileProvider the profile provider instance
     * @param compatibility   the compatibility instance to use for checking other hooks
     * @param server          the server instance
     */
    public MythicMobsIntegrator(final Plugin plugin, final ProfileProvider profileProvider, final Compatibility compatibility, final Server server) {
        this.plugin = plugin;
        this.profileProvider = profileProvider;
        this.compatibility = compatibility;
        this.server = server;
    }

    @Override
    public void hook(final QuestTypeRegistries questTypeRegistries, final FeatureRegistries featureRegistries) throws HookException {
        validateVersion();

        final BukkitAPIHelper apiHelper = new BukkitAPIHelper();

        final PrimaryServerThreadData data = new PrimaryServerThreadData(server, server.getScheduler(), plugin);
        questTypeRegistries.condition().register("mythicmobdistance", new MythicMobDistanceConditionFactory(apiHelper, data));
        questTypeRegistries.objective().register("mmobkill", new MythicMobKillObjectiveFactory());
        questTypeRegistries.event().registerCombined("mspawnmob", new MythicSpawnMobEventFactory(apiHelper, data, compatibility));
        final NpcTypeRegistry npcTypes = featureRegistries.npc();
        server.getPluginManager().registerEvents(new MythicMobsInteractCatcher(profileProvider, npcTypes, apiHelper), plugin);
        npcTypes.register("mythicmobs", new MythicMobsNpcFactory(MythicBukkit.inst().getMobManager()));
        npcTypes.registerIdentifier(new MythicMobsReverseIdentifier());
    }

    /**
     * Aborts the hooking process if the installed version of MythicMobs is invalid.
     *
     * @throws UnsupportedVersionException if the installed version of MythicMobs is < 5.0.0.
     */
    private void validateVersion() throws UnsupportedVersionException {
        final Plugin mythicMobs = Bukkit.getPluginManager().getPlugin("MythicMobs");
        final String versionWithCommit = mythicMobs.getDescription().getVersion();
        final String[] parts = versionWithCommit.split("-");
        final Version mythicMobsVersion = new Version(parts[0]);
        final VersionComparator comparator = new VersionComparator(UpdateStrategy.MAJOR, "-");
        if (comparator.isOtherNewerThanCurrent(mythicMobsVersion, new Version("5.0.0"))) {
            throw new UnsupportedVersionException(mythicMobs, "5.0.0+");
        }
    }

    @Override
    public void postHook() {
        if (compatibility.getHooked().contains("ProtocolLib")) {
            MythicHider.start();
        }
    }

    @Override
    public void reload() {
        if (MythicHider.getInstance() != null) {
            MythicHider.start();
        }
    }

    @Override
    public void close() {
        // Empty
    }
}
