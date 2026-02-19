package org.betonquest.betonquest.quest;

import org.betonquest.betonquest.api.LanguageProvider;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.api.quest.npc.DefaultNpcHider;
import org.betonquest.betonquest.api.quest.objective.ObjectiveFactory;
import org.betonquest.betonquest.api.service.action.ActionManager;
import org.betonquest.betonquest.api.service.action.Actions;
import org.betonquest.betonquest.api.service.condition.ConditionManager;
import org.betonquest.betonquest.api.service.condition.Conditions;
import org.betonquest.betonquest.api.service.conversation.Conversations;
import org.betonquest.betonquest.api.service.instruction.Instructions;
import org.betonquest.betonquest.api.service.npc.NpcManager;
import org.betonquest.betonquest.api.service.objective.Objectives;
import org.betonquest.betonquest.api.service.placeholder.Placeholders;
import org.betonquest.betonquest.api.text.TextParser;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.data.PlayerDataStorage;
import org.betonquest.betonquest.database.GlobalData;
import org.betonquest.betonquest.database.PlayerDataFactory;
import org.betonquest.betonquest.database.Saver;
import org.betonquest.betonquest.kernel.processor.feature.CancelerProcessor;
import org.betonquest.betonquest.kernel.processor.feature.CompassProcessor;
import org.betonquest.betonquest.kernel.registry.quest.ActionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ConditionTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.ObjectiveTypeRegistry;
import org.betonquest.betonquest.kernel.registry.quest.PlaceholderTypeRegistry;
import org.betonquest.betonquest.quest.action.burn.BurnActionFactory;
import org.betonquest.betonquest.quest.action.cancel.CancelActionFactory;
import org.betonquest.betonquest.quest.action.chat.ChatActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestClearActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestGiveActionFactory;
import org.betonquest.betonquest.quest.action.chest.ChestTakeActionFactory;
import org.betonquest.betonquest.quest.action.command.CommandActionFactory;
import org.betonquest.betonquest.quest.action.command.OpSudoActionFactory;
import org.betonquest.betonquest.quest.action.command.SudoActionFactory;
import org.betonquest.betonquest.quest.action.compass.CompassActionFactory;
import org.betonquest.betonquest.quest.action.conversation.CancelConversationActionFactory;
import org.betonquest.betonquest.quest.action.conversation.ConversationActionFactory;
import org.betonquest.betonquest.quest.action.damage.DamageActionFactory;
import org.betonquest.betonquest.quest.action.door.DoorActionFactory;
import org.betonquest.betonquest.quest.action.drop.DropActionFactory;
import org.betonquest.betonquest.quest.action.effect.DeleteEffectActionFactory;
import org.betonquest.betonquest.quest.action.effect.EffectActionFactory;
import org.betonquest.betonquest.quest.action.entity.RemoveEntityActionFactory;
import org.betonquest.betonquest.quest.action.eval.EvalActionFactory;
import org.betonquest.betonquest.quest.action.experience.ExperienceActionFactory;
import org.betonquest.betonquest.quest.action.explosion.ExplosionActionFactory;
import org.betonquest.betonquest.quest.action.folder.FolderActionFactory;
import org.betonquest.betonquest.quest.action.give.GiveActionFactory;
import org.betonquest.betonquest.quest.action.hunger.HungerActionFactory;
import org.betonquest.betonquest.quest.action.item.ItemDurabilityActionFactory;
import org.betonquest.betonquest.quest.action.journal.GiveJournalActionFactory;
import org.betonquest.betonquest.quest.action.journal.JournalActionFactory;
import org.betonquest.betonquest.quest.action.kill.KillActionFactory;
import org.betonquest.betonquest.quest.action.language.LanguageActionFactory;
import org.betonquest.betonquest.quest.action.lever.LeverActionFactory;
import org.betonquest.betonquest.quest.action.lightning.LightningActionFactory;
import org.betonquest.betonquest.quest.action.log.LogActionFactory;
import org.betonquest.betonquest.quest.action.logic.FirstActionFactory;
import org.betonquest.betonquest.quest.action.logic.IfElseActionFactory;
import org.betonquest.betonquest.quest.action.notify.NotifyActionFactory;
import org.betonquest.betonquest.quest.action.notify.NotifyAllActionFactory;
import org.betonquest.betonquest.quest.action.npc.NpcTeleportActionFactory;
import org.betonquest.betonquest.quest.action.npc.UpdateVisibilityNowActionFactory;
import org.betonquest.betonquest.quest.action.objective.ObjectiveActionFactory;
import org.betonquest.betonquest.quest.action.party.PartyActionFactory;
import org.betonquest.betonquest.quest.action.point.DeleteGlobalPointActionFactory;
import org.betonquest.betonquest.quest.action.point.DeletePointActionFactory;
import org.betonquest.betonquest.quest.action.point.GlobalPointActionFactory;
import org.betonquest.betonquest.quest.action.point.PointActionFactory;
import org.betonquest.betonquest.quest.action.random.PickRandomActionFactory;
import org.betonquest.betonquest.quest.action.run.RunActionFactory;
import org.betonquest.betonquest.quest.action.run.RunForAllActionFactory;
import org.betonquest.betonquest.quest.action.run.RunIndependentActionFactory;
import org.betonquest.betonquest.quest.action.scoreboard.ScoreboardObjectiveActionFactory;
import org.betonquest.betonquest.quest.action.scoreboard.ScoreboardTagActionFactory;
import org.betonquest.betonquest.quest.action.setblock.SetBlockActionFactory;
import org.betonquest.betonquest.quest.action.spawn.SpawnMobActionFactory;
import org.betonquest.betonquest.quest.action.stage.StageActionFactory;
import org.betonquest.betonquest.quest.action.tag.TagGlobalActionFactory;
import org.betonquest.betonquest.quest.action.tag.TagPlayerActionFactory;
import org.betonquest.betonquest.quest.action.take.TakeActionFactory;
import org.betonquest.betonquest.quest.action.teleport.TeleportActionFactory;
import org.betonquest.betonquest.quest.action.time.TimeActionFactory;
import org.betonquest.betonquest.quest.action.variable.VariableActionFactory;
import org.betonquest.betonquest.quest.action.velocity.VelocityActionFactory;
import org.betonquest.betonquest.quest.action.weather.WeatherActionFactory;
import org.betonquest.betonquest.quest.condition.advancement.AdvancementConditionFactory;
import org.betonquest.betonquest.quest.condition.armor.ArmorConditionFactory;
import org.betonquest.betonquest.quest.condition.armor.ArmorRatingConditionFactory;
import org.betonquest.betonquest.quest.condition.biome.BiomeConditionFactory;
import org.betonquest.betonquest.quest.condition.block.BlockConditionFactory;
import org.betonquest.betonquest.quest.condition.burning.BurningConditionFactory;
import org.betonquest.betonquest.quest.condition.check.CheckConditionFactory;
import org.betonquest.betonquest.quest.condition.chest.ChestItemConditionFactory;
import org.betonquest.betonquest.quest.condition.conversation.ConversationConditionFactory;
import org.betonquest.betonquest.quest.condition.conversation.InConversationConditionFactory;
import org.betonquest.betonquest.quest.condition.effect.EffectConditionFactory;
import org.betonquest.betonquest.quest.condition.entity.EntityConditionFactory;
import org.betonquest.betonquest.quest.condition.eval.EvalConditionFactory;
import org.betonquest.betonquest.quest.condition.experience.ExperienceConditionFactory;
import org.betonquest.betonquest.quest.condition.facing.FacingConditionFactory;
import org.betonquest.betonquest.quest.condition.flying.FlyingConditionFactory;
import org.betonquest.betonquest.quest.condition.gamemode.GameModeConditionFactory;
import org.betonquest.betonquest.quest.condition.hand.HandConditionFactory;
import org.betonquest.betonquest.quest.condition.health.HealthConditionFactory;
import org.betonquest.betonquest.quest.condition.height.HeightConditionFactory;
import org.betonquest.betonquest.quest.condition.hunger.HungerConditionFactory;
import org.betonquest.betonquest.quest.condition.item.ItemConditionFactory;
import org.betonquest.betonquest.quest.condition.item.ItemDurabilityConditionFactory;
import org.betonquest.betonquest.quest.condition.journal.JournalConditionFactory;
import org.betonquest.betonquest.quest.condition.language.LanguageConditionFactory;
import org.betonquest.betonquest.quest.condition.location.LocationConditionFactory;
import org.betonquest.betonquest.quest.condition.logik.AlternativeConditionFactory;
import org.betonquest.betonquest.quest.condition.logik.ConjunctionConditionFactory;
import org.betonquest.betonquest.quest.condition.looking.LookingAtConditionFactory;
import org.betonquest.betonquest.quest.condition.moon.MoonPhaseConditionFactory;
import org.betonquest.betonquest.quest.condition.npc.NpcDistanceConditionFactory;
import org.betonquest.betonquest.quest.condition.npc.NpcLocationConditionFactory;
import org.betonquest.betonquest.quest.condition.number.NumberCompareConditionFactory;
import org.betonquest.betonquest.quest.condition.objective.ObjectiveConditionFactory;
import org.betonquest.betonquest.quest.condition.party.PartyConditionFactory;
import org.betonquest.betonquest.quest.condition.permission.PermissionConditionFactory;
import org.betonquest.betonquest.quest.condition.point.GlobalPointConditionFactory;
import org.betonquest.betonquest.quest.condition.point.PointConditionFactory;
import org.betonquest.betonquest.quest.condition.random.RandomConditionFactory;
import org.betonquest.betonquest.quest.condition.ride.RideConditionFactory;
import org.betonquest.betonquest.quest.condition.scoreboard.ScoreboardObjectiveConditionFactory;
import org.betonquest.betonquest.quest.condition.scoreboard.ScoreboardTagConditionFactory;
import org.betonquest.betonquest.quest.condition.slots.EmptySlotsConditionFactory;
import org.betonquest.betonquest.quest.condition.sneak.SneakConditionFactory;
import org.betonquest.betonquest.quest.condition.stage.StageConditionFactory;
import org.betonquest.betonquest.quest.condition.tag.GlobalTagConditionFactory;
import org.betonquest.betonquest.quest.condition.tag.TagConditionFactory;
import org.betonquest.betonquest.quest.condition.time.ingame.TimeConditionFactory;
import org.betonquest.betonquest.quest.condition.time.real.DayOfWeekConditionFactory;
import org.betonquest.betonquest.quest.condition.time.real.PartialDateConditionFactory;
import org.betonquest.betonquest.quest.condition.time.real.RealTimeConditionFactory;
import org.betonquest.betonquest.quest.condition.variable.VariableConditionFactory;
import org.betonquest.betonquest.quest.condition.weather.WeatherConditionFactory;
import org.betonquest.betonquest.quest.condition.world.WorldConditionFactory;
import org.betonquest.betonquest.quest.objective.action.ActionObjectiveFactory;
import org.betonquest.betonquest.quest.objective.arrow.ArrowShootObjectiveFactory;
import org.betonquest.betonquest.quest.objective.block.BlockObjectiveFactory;
import org.betonquest.betonquest.quest.objective.breed.BreedObjectiveFactory;
import org.betonquest.betonquest.quest.objective.brew.BrewObjectiveFactory;
import org.betonquest.betonquest.quest.objective.chestput.ChestPutObjectiveFactory;
import org.betonquest.betonquest.quest.objective.command.CommandObjectiveFactory;
import org.betonquest.betonquest.quest.objective.consume.ConsumeObjectiveFactory;
import org.betonquest.betonquest.quest.objective.crafting.CraftingObjectiveFactory;
import org.betonquest.betonquest.quest.objective.data.PointObjectiveFactory;
import org.betonquest.betonquest.quest.objective.data.TagObjectiveFactory;
import org.betonquest.betonquest.quest.objective.delay.DelayObjectiveFactory;
import org.betonquest.betonquest.quest.objective.die.DieObjectiveFactory;
import org.betonquest.betonquest.quest.objective.enchant.EnchantObjectiveFactory;
import org.betonquest.betonquest.quest.objective.equip.EquipItemObjectiveFactory;
import org.betonquest.betonquest.quest.objective.experience.ExperienceObjectiveFactory;
import org.betonquest.betonquest.quest.objective.fish.FishObjectiveFactory;
import org.betonquest.betonquest.quest.objective.interact.EntityInteractObjectiveFactory;
import org.betonquest.betonquest.quest.objective.jump.JumpObjectiveFactory;
import org.betonquest.betonquest.quest.objective.kill.KillPlayerObjectiveFactory;
import org.betonquest.betonquest.quest.objective.kill.MobKillObjectiveFactory;
import org.betonquest.betonquest.quest.objective.location.LocationObjectiveFactory;
import org.betonquest.betonquest.quest.objective.login.LoginObjectiveFactory;
import org.betonquest.betonquest.quest.objective.logout.LogoutObjectiveFactory;
import org.betonquest.betonquest.quest.objective.npc.NpcInteractObjectiveFactory;
import org.betonquest.betonquest.quest.objective.npc.NpcRangeObjectiveFactory;
import org.betonquest.betonquest.quest.objective.password.PasswordObjectiveFactory;
import org.betonquest.betonquest.quest.objective.pickup.PickupObjectiveFactory;
import org.betonquest.betonquest.quest.objective.resourcepack.ResourcepackObjectiveFactory;
import org.betonquest.betonquest.quest.objective.ride.RideObjectiveFactory;
import org.betonquest.betonquest.quest.objective.shear.ShearObjectiveFactory;
import org.betonquest.betonquest.quest.objective.smelt.SmeltingObjectiveFactory;
import org.betonquest.betonquest.quest.objective.stage.StageObjectiveFactory;
import org.betonquest.betonquest.quest.objective.step.StepObjectiveFactory;
import org.betonquest.betonquest.quest.objective.tame.TameObjectiveFactory;
import org.betonquest.betonquest.quest.objective.timer.TimerObjectiveFactory;
import org.betonquest.betonquest.quest.objective.variable.VariableObjectiveFactory;
import org.betonquest.betonquest.quest.placeholder.condition.ConditionPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.constant.ConstantPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.eval.EvalPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.item.ItemDurabilityPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.item.ItemPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.location.LocationPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.math.MathPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.name.PlayerNamePlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.name.QuesterPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.npc.NpcPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.objective.ObjectivePropertyPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.point.GlobalPointPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.point.PointPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.random.RandomNumberPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.sync.SyncPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.tag.GlobalTagPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.tag.TagPlaceholderFactory;
import org.betonquest.betonquest.quest.placeholder.version.VersionPlaceholderFactory;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;

import java.time.InstantSource;

/**
 * Registers the Conditions, Actions, Objectives and Placeholders that come with BetonQuest.
 */
@SuppressWarnings({"PMD.NcssCount", "PMD.AvoidDuplicateLiterals", "PMD.CouplingBetweenObjects"})
public class CoreQuestTypes {

    /**
     * Logger Factory to create new custom Logger from.
     */
    private final BetonQuestLoggerFactory loggerFactory;

    /**
     * Server used for primary server thread access.
     */
    private final Server server;

    /**
     * Plugin used for primary server thread access, type registration and general usage.
     */
    private final Plugin plugin;

    /**
     * The {@link PluginMessage} instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Storage for global data.
     */
    private final GlobalData globalData;

    /**
     * The database saver.
     */
    private final Saver saver;

    /**
     * Storage for player data.
     */
    private final PlayerDataStorage dataStorage;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The language provider to get the default language.
     */
    private final LanguageProvider languageProvider;

    /**
     * Factory to create new Player Data.
     */
    private final PlayerDataFactory playerDataFactory;

    /**
     * The betonquest instructions instance.
     */
    private final Instructions instructions;

    /**
     * The constructor manager params containing all managers.
     */
    private final ConstructorManagerParams managers;

    /**
     * The legacy feature params.
     */
    private final LegacyFeatureParams legacyFeatureParams;

    /**
     * The text parser instance.
     */
    private final TextParser textParser;

    /**
     * The conversations api.
     */
    private final Conversations conversations;

    /**
     * Create a new Core Quest Types class for registering.
     *
     * @param constructorParams   the constructor parameters
     * @param managerParams       the constructor manager params containing all managers
     * @param legacyFeatureParams the legacy feature parameters
     */
    public CoreQuestTypes(final ConstructorParams constructorParams, final ConstructorManagerParams managerParams, final LegacyFeatureParams legacyFeatureParams) {
        this.loggerFactory = constructorParams.loggerFactory();
        this.plugin = constructorParams.plugin();
        this.server = plugin.getServer();
        this.managers = managerParams;
        this.textParser = constructorParams.textParser();
        this.pluginMessage = constructorParams.pluginMessage();
        this.globalData = constructorParams.globalData();
        this.saver = constructorParams.saver();
        this.dataStorage = constructorParams.playerDataStorage();
        this.playerDataFactory = constructorParams.playerDataFactory();
        this.profileProvider = constructorParams.profileProvider();
        this.languageProvider = constructorParams.languageProvider();
        this.instructions = constructorParams.instructions();
        this.conversations = constructorParams.conversations();
        this.legacyFeatureParams = legacyFeatureParams;
    }

    /**
     * Registers all Quest Types.
     *
     * @param conditionTypeRegistry   the condition type registry
     * @param actionTypeRegistry      the action type registry
     * @param objectiveTypeRegistry   the objective type registry
     * @param placeholderTypeRegistry the placeholder type registry
     */
    public void register(final ConditionTypeRegistry conditionTypeRegistry, final ActionTypeRegistry actionTypeRegistry,
                         final ObjectiveTypeRegistry objectiveTypeRegistry, final PlaceholderTypeRegistry placeholderTypeRegistry) {
        // When adding new types they need to be ordered by name in the corresponding method!
        registerConditions(conditionTypeRegistry);
        registerActions(actionTypeRegistry);
        registerObjectives(objectiveTypeRegistry);
        registerPlaceholders(placeholderTypeRegistry);
    }

    private void registerConditions(final ConditionTypeRegistry conditionTypes) {
        conditionTypes.register("advancement", new AdvancementConditionFactory(loggerFactory, server));
        conditionTypes.registerCombined("and", new ConjunctionConditionFactory(managers.conditions().manager()));
        conditionTypes.register("armor", new ArmorConditionFactory(loggerFactory));
        conditionTypes.register("biome", new BiomeConditionFactory(loggerFactory));
        conditionTypes.register("burning", new BurningConditionFactory(loggerFactory));
        conditionTypes.registerCombined("check", new CheckConditionFactory(instructions, conditionTypes));
        conditionTypes.registerCombined("chestitem", new ChestItemConditionFactory());
        conditionTypes.register("conversation", new ConversationConditionFactory(conversations));
        conditionTypes.register("dayofweek", new DayOfWeekConditionFactory(loggerFactory.create(DayOfWeekConditionFactory.class)));
        conditionTypes.register("effect", new EffectConditionFactory(loggerFactory));
        conditionTypes.register("empty", new EmptySlotsConditionFactory(loggerFactory));
        conditionTypes.registerCombined("entities", new EntityConditionFactory());
        conditionTypes.registerCombined("eval", new EvalConditionFactory(instructions, conditionTypes, server.getScheduler(), plugin));
        conditionTypes.register("experience", new ExperienceConditionFactory(loggerFactory));
        conditionTypes.register("facing", new FacingConditionFactory(loggerFactory));
        conditionTypes.register("fly", new FlyingConditionFactory(loggerFactory));
        conditionTypes.register("gamemode", new GameModeConditionFactory(loggerFactory));
        conditionTypes.registerCombined("globalpoint", new GlobalPointConditionFactory(globalData));
        conditionTypes.register("globaltag", new GlobalTagConditionFactory(globalData));
        conditionTypes.register("hand", new HandConditionFactory(loggerFactory));
        conditionTypes.register("health", new HealthConditionFactory(loggerFactory));
        conditionTypes.register("height", new HeightConditionFactory(loggerFactory));
        conditionTypes.register("hunger", new HungerConditionFactory(loggerFactory));
        conditionTypes.register("inconversation", new InConversationConditionFactory(conversations));
        conditionTypes.register("item", new ItemConditionFactory(loggerFactory, dataStorage));
        conditionTypes.register("itemdurability", new ItemDurabilityConditionFactory(loggerFactory));
        conditionTypes.register("journal", new JournalConditionFactory(dataStorage, loggerFactory));
        conditionTypes.register("language", new LanguageConditionFactory(dataStorage, languageProvider, pluginMessage));
        conditionTypes.register("location", new LocationConditionFactory(loggerFactory));
        conditionTypes.register("looking", new LookingAtConditionFactory(loggerFactory));
        conditionTypes.registerCombined("moonphase", new MoonPhaseConditionFactory());
        conditionTypes.register("npcdistance", new NpcDistanceConditionFactory(managers.npcManager(), loggerFactory));
        conditionTypes.registerCombined("npclocation", new NpcLocationConditionFactory(managers.npcManager()));
        conditionTypes.registerCombined("numbercompare", new NumberCompareConditionFactory());
        conditionTypes.register("objective", new ObjectiveConditionFactory(managers.objectives().manager()));
        conditionTypes.registerCombined("or", new AlternativeConditionFactory(managers.conditions().manager()));
        conditionTypes.register("partialdate", new PartialDateConditionFactory());
        conditionTypes.registerCombined("party", new PartyConditionFactory(managers.conditions().manager(), profileProvider));
        conditionTypes.register("permission", new PermissionConditionFactory(loggerFactory));
        conditionTypes.register("point", new PointConditionFactory(dataStorage));
        conditionTypes.registerCombined("random", new RandomConditionFactory());
        conditionTypes.register("rating", new ArmorRatingConditionFactory(loggerFactory));
        conditionTypes.register("realtime", new RealTimeConditionFactory());
        conditionTypes.register("ride", new RideConditionFactory(loggerFactory));
        conditionTypes.register("score", new ScoreboardObjectiveConditionFactory());
        conditionTypes.register("scoretag", new ScoreboardTagConditionFactory(loggerFactory));
        conditionTypes.register("sneak", new SneakConditionFactory(loggerFactory));
        conditionTypes.register("stage", new StageConditionFactory(managers.objectives().manager()));
        conditionTypes.register("tag", new TagConditionFactory(dataStorage));
        conditionTypes.registerCombined("testforblock", new BlockConditionFactory());
        conditionTypes.registerCombined("time", new TimeConditionFactory());
        conditionTypes.registerCombined("variable", new VariableConditionFactory(loggerFactory));
        conditionTypes.registerCombined("weather", new WeatherConditionFactory());
        conditionTypes.register("world", new WorldConditionFactory(loggerFactory));
    }

    private void registerActions(final ActionTypeRegistry actionTypes) {
        final ActionManager actionManager = managers.actions().manager();
        final ConditionManager conditionManager = managers.conditions().manager();
        actionTypes.register("burn", new BurnActionFactory(loggerFactory));
        actionTypes.register("cancel", new CancelActionFactory(loggerFactory, legacyFeatureParams.cancelerProcessor()));
        actionTypes.register("cancelconversation", new CancelConversationActionFactory(loggerFactory, conversations));
        actionTypes.register("chat", new ChatActionFactory(loggerFactory));
        actionTypes.registerCombined("chestclear", new ChestClearActionFactory());
        actionTypes.registerCombined("chestgive", new ChestGiveActionFactory());
        actionTypes.registerCombined("chesttake", new ChestTakeActionFactory());
        actionTypes.register("compass", new CompassActionFactory(legacyFeatureParams.compassProcessor(), dataStorage));
        actionTypes.registerCombined("command", new CommandActionFactory(loggerFactory, server));
        actionTypes.register("conversation", new ConversationActionFactory(loggerFactory, conversations));
        actionTypes.register("damage", new DamageActionFactory(loggerFactory));
        actionTypes.register("deleffect", new DeleteEffectActionFactory(loggerFactory));
        actionTypes.registerCombined("deleteglobalpoint", new DeleteGlobalPointActionFactory(globalData));
        actionTypes.registerCombined("deletepoint", new DeletePointActionFactory(dataStorage, saver, profileProvider));
        actionTypes.registerCombined("door", new DoorActionFactory());
        actionTypes.registerCombined("drop", new DropActionFactory(profileProvider));
        actionTypes.register("effect", new EffectActionFactory(loggerFactory));
        actionTypes.registerCombined("eval", new EvalActionFactory(instructions, actionTypes, server.getScheduler(), plugin));
        actionTypes.register("experience", new ExperienceActionFactory(loggerFactory));
        actionTypes.registerCombined("explosion", new ExplosionActionFactory());
        actionTypes.registerCombined("folder", new FolderActionFactory(plugin, loggerFactory, server.getPluginManager(),
                actionManager, conditionManager));
        actionTypes.registerCombined("first", new FirstActionFactory(actionManager));
        actionTypes.register("give", new GiveActionFactory(loggerFactory, dataStorage, pluginMessage));
        actionTypes.register("givejournal", new GiveJournalActionFactory(loggerFactory, dataStorage));
        actionTypes.registerCombined("globaltag", new TagGlobalActionFactory(globalData));
        actionTypes.registerCombined("globalpoint", new GlobalPointActionFactory(globalData));
        actionTypes.register("hunger", new HungerActionFactory(loggerFactory));
        actionTypes.registerCombined("if", new IfElseActionFactory(actionManager, conditionManager));
        actionTypes.register("itemdurability", new ItemDurabilityActionFactory(loggerFactory));
        actionTypes.registerCombined("journal", new JournalActionFactory(loggerFactory, pluginMessage, dataStorage,
                InstantSource.system(), saver, profileProvider));
        actionTypes.register("kill", new KillActionFactory(loggerFactory));
        actionTypes.register("language", new LanguageActionFactory(dataStorage));
        actionTypes.registerCombined("lever", new LeverActionFactory());
        actionTypes.registerCombined("lightning", new LightningActionFactory());
        actionTypes.registerCombined("log", new LogActionFactory(loggerFactory));
        actionTypes.register("notify", new NotifyActionFactory(loggerFactory, textParser, dataStorage, languageProvider));
        actionTypes.registerCombined("notifyall", new NotifyAllActionFactory(loggerFactory, textParser, dataStorage, profileProvider, languageProvider));
        actionTypes.registerCombined("npcteleport", new NpcTeleportActionFactory(managers.npcManager()));
        actionTypes.registerCombined("objective", new ObjectiveActionFactory(plugin, loggerFactory, profileProvider, saver,
                managers.objectives().manager(), dataStorage, playerDataFactory));
        actionTypes.register("opsudo", new OpSudoActionFactory(loggerFactory, server));
        actionTypes.register("party", new PartyActionFactory(loggerFactory, profileProvider, actionManager, conditionManager));
        actionTypes.registerCombined("pickrandom", new PickRandomActionFactory(actionManager));
        actionTypes.register("point", new PointActionFactory(loggerFactory, dataStorage,
                pluginMessage));
        actionTypes.registerCombined("removeentity", new RemoveEntityActionFactory());
        actionTypes.registerCombined("run", new RunActionFactory(instructions, actionTypes));
        actionTypes.register("runForAll", new RunForAllActionFactory(profileProvider, actionManager, conditionManager));
        actionTypes.register("runIndependent", new RunIndependentActionFactory(actionManager));
        actionTypes.registerCombined("setblock", new SetBlockActionFactory());
        actionTypes.register("score", new ScoreboardObjectiveActionFactory());
        actionTypes.register("scoretag", new ScoreboardTagActionFactory(loggerFactory));
        actionTypes.registerCombined("spawn", new SpawnMobActionFactory());
        actionTypes.register("stage", new StageActionFactory(managers.objectives().manager()));
        actionTypes.register("sudo", new SudoActionFactory(loggerFactory, server));
        actionTypes.registerCombined("tag", new TagPlayerActionFactory(dataStorage, saver, profileProvider));
        actionTypes.register("take", new TakeActionFactory(loggerFactory, pluginMessage));
        actionTypes.register("teleport", new TeleportActionFactory(loggerFactory, conversations));
        actionTypes.registerCombined("time", new TimeActionFactory());
        actionTypes.register("updatevisibility", new UpdateVisibilityNowActionFactory(legacyFeatureParams.defaultNpcHider(), loggerFactory));
        actionTypes.register("variable", new VariableActionFactory(managers.objectives().manager()));
        actionTypes.register("velocity", new VelocityActionFactory(loggerFactory));
        actionTypes.registerCombined("weather", new WeatherActionFactory(loggerFactory));
    }

    private void registerObjectives(final FeatureRegistry<ObjectiveFactory> objectiveTypes) {
        objectiveTypes.register("action", new ActionObjectiveFactory());
        objectiveTypes.register("arrow", new ArrowShootObjectiveFactory());
        objectiveTypes.register("block", new BlockObjectiveFactory(loggerFactory, pluginMessage));
        objectiveTypes.register("breed", new BreedObjectiveFactory());
        objectiveTypes.register("brew", new BrewObjectiveFactory(profileProvider));
        objectiveTypes.register("chestput", new ChestPutObjectiveFactory(loggerFactory, pluginMessage));
        objectiveTypes.register("command", new CommandObjectiveFactory(managers.actions().manager()));
        objectiveTypes.register("consume", new ConsumeObjectiveFactory());
        objectiveTypes.register("craft", new CraftingObjectiveFactory());
        objectiveTypes.register("delay", new DelayObjectiveFactory());
        objectiveTypes.register("die", new DieObjectiveFactory());
        objectiveTypes.register("enchant", new EnchantObjectiveFactory());
        objectiveTypes.register("experience", new ExperienceObjectiveFactory(loggerFactory, pluginMessage));
        objectiveTypes.register("fish", new FishObjectiveFactory());
        objectiveTypes.register("interact", new EntityInteractObjectiveFactory());
        objectiveTypes.register("kill", new KillPlayerObjectiveFactory(managers.conditions().manager()));
        objectiveTypes.register("location", new LocationObjectiveFactory());
        objectiveTypes.register("login", new LoginObjectiveFactory());
        objectiveTypes.register("logout", new LogoutObjectiveFactory());
        objectiveTypes.register("mobkill", new MobKillObjectiveFactory());
        objectiveTypes.register("npcinteract", new NpcInteractObjectiveFactory());
        objectiveTypes.register("npcrange", new NpcRangeObjectiveFactory());
        objectiveTypes.register("password", new PasswordObjectiveFactory(managers.actions().manager()));
        objectiveTypes.register("pickup", new PickupObjectiveFactory());
        objectiveTypes.register("point", new PointObjectiveFactory(dataStorage));
        objectiveTypes.register("ride", new RideObjectiveFactory());
        objectiveTypes.register("shear", new ShearObjectiveFactory());
        objectiveTypes.register("smelt", new SmeltingObjectiveFactory());
        objectiveTypes.register("stage", new StageObjectiveFactory());
        objectiveTypes.register("step", new StepObjectiveFactory());
        objectiveTypes.register("tag", new TagObjectiveFactory(dataStorage));
        objectiveTypes.register("tame", new TameObjectiveFactory());
        objectiveTypes.register("timer", new TimerObjectiveFactory(managers.actions().manager()));
        objectiveTypes.register("variable", new VariableObjectiveFactory());
        objectiveTypes.register("equip", new EquipItemObjectiveFactory());
        objectiveTypes.register("jump", new JumpObjectiveFactory());
        objectiveTypes.register("resourcepack", new ResourcepackObjectiveFactory());
    }

    private void registerPlaceholders(final PlaceholderTypeRegistry placeholderTypes) {
        placeholderTypes.register("condition", new ConditionPlaceholderFactory(managers.conditions().manager(), pluginMessage));
        placeholderTypes.registerCombined("constant", new ConstantPlaceholderFactory());
        placeholderTypes.registerCombined("eval", new EvalPlaceholderFactory());
        placeholderTypes.register("globalpoint", new GlobalPointPlaceholderFactory(globalData));
        placeholderTypes.register("globaltag", new GlobalTagPlaceholderFactory(globalData, pluginMessage));
        placeholderTypes.registerCombined("item", new ItemPlaceholderFactory(dataStorage));
        placeholderTypes.register("itemdurability", new ItemDurabilityPlaceholderFactory());
        placeholderTypes.register("location", new LocationPlaceholderFactory());
        placeholderTypes.registerCombined("math", new MathPlaceholderFactory(managers.placeholders().manager()));
        placeholderTypes.registerCombined("npc", new NpcPlaceholderFactory(conversations, managers.npcManager()));
        placeholderTypes.register("objective", new ObjectivePropertyPlaceholderFactory(managers.objectives().manager()));
        placeholderTypes.register("point", new PointPlaceholderFactory(dataStorage));
        placeholderTypes.register("player", new PlayerNamePlaceholderFactory());
        placeholderTypes.register("quester", new QuesterPlaceholderFactory(conversations));
        placeholderTypes.registerCombined("randomnumber", new RandomNumberPlaceholderFactory());
        placeholderTypes.registerCombined("sync", new SyncPlaceholderFactory());
        placeholderTypes.register("tag", new TagPlaceholderFactory(dataStorage, pluginMessage));
        placeholderTypes.register("version", new VersionPlaceholderFactory(plugin));
    }

    /**
     * The constructor parameters for {@link CoreQuestTypes}.
     *
     * @param loggerFactory     the logger factory to create loggers for individual types
     * @param plugin            the plugin instance to use in the bukkit api
     * @param pluginMessage     the plugin message instance to handle internal messages
     * @param instructions      the instructions api
     * @param profileProvider   the profile provider to manage profiles
     * @param saver             the database saver instance
     * @param textParser        the text parser
     * @param globalData        the global data
     * @param playerDataFactory the player data storage
     * @param playerDataStorage player data storage
     * @param languageProvider  the language provider
     * @param conversations     the conversations api
     */
    public record ConstructorParams(BetonQuestLoggerFactory loggerFactory, Plugin plugin, PluginMessage pluginMessage,
                                    Instructions instructions, ProfileProvider profileProvider, Saver saver,
                                    TextParser textParser, GlobalData globalData, PlayerDataFactory playerDataFactory,
                                    PlayerDataStorage playerDataStorage, LanguageProvider languageProvider,
                                    Conversations conversations) {

    }

    /**
     * The constructor manager params.
     *
     * @param actions      the action manager
     * @param conditions   the condition manager
     * @param objectives   the objective manager
     * @param placeholders the placeholder manager
     * @param npcManager   the npc manager
     */
    public record ConstructorManagerParams(Actions actions, Conditions conditions,
                                           Objectives objectives, Placeholders placeholders,
                                           NpcManager npcManager) {

    }

    /**
     * The legacy feature params.
     *
     * @param cancelerProcessor the canceler processor
     * @param compassProcessor  the compass processor
     * @param defaultNpcHider   the default npc hider
     */
    public record LegacyFeatureParams(CancelerProcessor cancelerProcessor, CompassProcessor compassProcessor,
                                      DefaultNpcHider defaultNpcHider) {

    }
}
