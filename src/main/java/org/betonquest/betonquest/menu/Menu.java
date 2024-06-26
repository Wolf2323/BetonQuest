package org.betonquest.betonquest.menu;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.betonquest.betonquest.api.logger.BetonQuestLoggerFactory;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.profile.Profile;
import org.betonquest.betonquest.api.profile.ProfileProvider;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.api.quest.QuestTypeAPI;
import org.betonquest.betonquest.config.PluginMessage;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ItemID;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.betonquest.betonquest.item.QuestItem;
import org.betonquest.betonquest.menu.command.SimpleCommand;
import org.betonquest.betonquest.menu.config.SimpleYMLSection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class representing a menu.
 */
@SuppressWarnings({"PMD.ShortClassName", "PMD.CouplingBetweenObjects", "PMD.GodClass"})
public class Menu extends SimpleYMLSection implements Listener {
    /**
     * Custom {@link BetonQuestLogger} instance for this class.
     */
    private final BetonQuestLogger log;

    /**
     * The plugin configuration file.
     */
    private final ConfigAccessor pluginConfig;

    /**
     * The plugin message instance.
     */
    private final PluginMessage pluginMessage;

    /**
     * Quest Type API.
     */
    private final QuestTypeAPI questTypeAPI;

    /**
     * The profile provider instance.
     */
    private final ProfileProvider profileProvider;

    /**
     * The internal id of the menu.
     */
    private final MenuID menuID;

    /**
     * The height of the menu in slots.
     */
    private final int height;

    /**
     * The title of the menu.
     */
    private final VariableString title;

    /**
     * List of all slots objects as defined in the slots section.
     */
    private final List<Slots> slots;

    /**
     * Optional which contains the item this menu is bound to or is empty if none is bound.
     */
    @Nullable
    private final QuestItem boundItem;

    /**
     * Conditions which have to be matched to open the menu.
     */
    private final List<ConditionID> openConditions;

    /**
     * Events which are fired when the menu is opened.
     */
    private final List<EventID> openEvents;

    /**
     * Events which are fired when the menu is closed.
     */
    private final List<EventID> closeEvents;

    /**
     * Optional which contains the command this menu is bound to or is empty if none is bound.
     */
    @Nullable
    private final MenuBoundCommand boundCommand;

    /**
     * The RPGMenu "plugin" instance to open menus.
     */
    private final RPGMenu rpgMenu;

    /**
     * Creates a new Menu.
     *
     * @param log             the custom logger for this class
     * @param loggerFactory   the logger factory for new class specific custom logger
     * @param rpgMenu         the rpg menu instance to open menus
     * @param config          the configuration file of the plugin
     * @param pluginMessage   the plugin message instance
     * @param questTypeAPI    the Quest Type API
     * @param profileProvider the profile provider instance
     * @param menuID          the id of the menu
     * @throws InvalidConfigurationException if config options are missing or invalid
     */
    public Menu(final BetonQuestLogger log, final BetonQuestLoggerFactory loggerFactory, final RPGMenu rpgMenu,
                final ConfigAccessor config, final PluginMessage pluginMessage, final QuestTypeAPI questTypeAPI,
                final ProfileProvider profileProvider, final MenuID menuID) throws InvalidConfigurationException {
        super(menuID.getPackage(), menuID.getFullID(), menuID.getConfig());
        this.rpgMenu = rpgMenu;
        this.log = log;
        pluginConfig = config;
        this.pluginMessage = pluginMessage;
        this.questTypeAPI = questTypeAPI;
        this.profileProvider = profileProvider;
        this.menuID = menuID;
        //load size
        try {
            this.height = getNumber("height").getValue(null).intValue();
        } catch (final QuestException e) {
            throw new Invalid("height", e);
        }
        if (this.height < 1 || this.height > 6) {
            throw new Invalid("height");
        }
        //load title
        try {
            this.title = getString("title");
        } catch (final QuestException e) {
            throw new Invalid("title", e);
        }
        this.openConditions = getConditions("open_conditions", pack);
        this.openEvents = getEvents("open_events", pack);
        this.closeEvents = getEvents("close_events", pack);
        //load bound item
        this.boundItem = new OptionalSetting<QuestItem>() {
            @Override
            @SuppressWarnings("PMD.ShortMethodName")
            protected QuestItem of() throws Missing, Invalid {
                try {
                    return new QuestItem(new ItemID(Menu.this.pack, getString("bind").getValue(null)));
                } catch (final QuestException e) {
                    throw new Invalid("bind", e);
                }
            }
        }.get();
        //load bound command
        this.boundCommand = new OptionalSetting<MenuBoundCommand>() {
            @Override
            @SuppressWarnings("PMD.ShortMethodName")
            protected MenuBoundCommand of() throws Missing, Invalid {
                String command;
                try {
                    command = getString("command").getValue(null).trim();
                } catch (final QuestException e) {
                    throw new Invalid("command", e);
                }
                if (!command.matches("/*[0-9A-Za-z\\-]+")) {
                    throw new Invalid("command");
                }
                if (command.startsWith("/")) {
                    command = command.substring(1);
                }
                return new MenuBoundCommand(loggerFactory.create(MenuBoundCommand.class), command);
            }
        }.get();

        this.slots = loadSlots(loggerFactory);

        //load command and register listener
        if (this.boundCommand != null) {
            boundCommand.register();
        }
        if (this.boundItem != null) {
            Bukkit.getPluginManager().registerEvents(this, BetonQuest.getInstance());
        }
    }

    @SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.CognitiveComplexity"})
    private List<Slots> loadSlots(final BetonQuestLoggerFactory loggerFactory) throws InvalidConfigurationException {
        // load items
        final String itemsSection = "items";
        if (!config.isConfigurationSection(itemsSection)) {
            throw new Missing(itemsSection);
        }

        final Map<String, MenuItem> itemsMap = new HashMap<>();
        for (final String key : config.getConfigurationSection(itemsSection).getKeys(false)) {
            itemsMap.put(key, new MenuItem(loggerFactory.create(MenuItem.class), pack, menuID, key,
                    config.getConfigurationSection("items." + key), pluginConfig.getBoolean("default_close")));
        }

        //load slots
        final String slotsSection = "slots";
        if (!config.isConfigurationSection(slotsSection)) {
            throw new Missing(slotsSection);
        }
        final List<Slots> slots = new ArrayList<>();
        for (final String key : config.getConfigurationSection(slotsSection).getKeys(false)) {
            final List<MenuItem> itemsList = new ArrayList<>();
            //check if items from list are all valid
            try {
                for (final VariableString item : getStrings("slots." + key)) {
                    final String itemString = item.getValue(null);
                    if (itemsMap.containsKey(itemString)) {
                        itemsList.add(itemsMap.get(itemString));
                    } else {
                        throw new Invalid("slots." + key, "item " + itemString + " not found");
                    }
                }
            } catch (final QuestException e) {
                throw new Invalid(slotsSection, e);
            }
            // create a new slots object and add it to list
            try {
                slots.add(new Slots(key, itemsList));
            } catch (final IllegalArgumentException e) {
                throw new Invalid(slotsSection, e);
            }
        }
        //check for doubled assigned slots
        try {
            Slots.checkSlots(slots, this.getSize());
        } catch (final Slots.SlotException e) {
            throw new Invalid("slots." + e.getSlots(), e);
        }
        return slots;
    }

    /**
     * Checks whether a player of the {@link Profile} may open this menu.
     *
     * @param profile the {@link Profile} to check
     * @return true if all opening conditions are true, false otherwise
     */
    public boolean mayOpen(final Profile profile) {
        for (final ConditionID conditionID : openConditions) {
            if (!questTypeAPI.condition(profile, conditionID)) {
                log.debug(pack, "Denied opening of " + name + ": Condition " + conditionID + "returned false.");
                return false;
            }
        }
        return true;
    }

    /**
     * Unregisters listeners and commands for this menu.
     * <p>
     * Run this method on reload
     */
    public void unregister() {
        if (boundCommand != null) {
            boundCommand.unregister();
        }
        if (boundItem != null) {
            HandlerList.unregisterAll(this);
        }
    }

    /**
     * Opens the menu on bound item interaction.
     *
     * @param event the event to process
     */
    @EventHandler
    public void onItemClick(final PlayerInteractEvent event) {
        //check if item is bound item
        if (boundItem == null || !boundItem.compare(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        final OnlineProfile onlineprofile = profileProvider.getProfile(event.getPlayer());
        if (!mayOpen(onlineprofile)) {
            event.getPlayer().sendMessage(pluginMessage.getMessage(onlineprofile, "menu.no_permission"));
            return;
        }
        //open the menu
        log.debug(pack, onlineprofile + " used bound item of menu " + this.menuID);
        rpgMenu.openMenu(onlineprofile, this.menuID);
    }

    /**
     * Runs all open events for the specified player of the {@link Profile}.
     *
     * @param profile the {@link Profile} to run the events for
     */
    @SuppressWarnings("PMD.AvoidDuplicateLiterals")
    public void runOpenEvents(final Profile profile) {
        log.debug(pack, "Menu " + menuID + ": Running open events");
        for (final EventID event : this.openEvents) {
            questTypeAPI.event(profile, event);
            log.debug(pack, "Menu " + menuID + ": Run event " + event);
        }
    }

    /**
     * Runs all close events for the specified player.
     *
     * @param player the player to run the events for
     */
    public void runCloseEvents(final Player player) {
        log.debug(pack, "Menu " + menuID + ": Running close events");
        for (final EventID event : this.closeEvents) {
            questTypeAPI.event(profileProvider.getProfile(player), event);
            log.debug(pack, "Menu " + menuID + ": Run event " + event);
        }
    }

    /**
     * @return the menu id of this menu
     */
    public MenuID getMenuID() {
        return menuID;
    }

    /**
     * @return the package this menu is located in
     */
    public QuestPackage getPackage() {
        return pack;
    }

    /**
     * @return the size of the menu in slots
     */
    public final int getSize() {
        return height * 9;
    }

    /**
     * @param profile the {@link Profile} of the player
     * @return the title of the menu
     */
    public String getTitle(final Profile profile) {
        try {
            return ChatColor.translateAlternateColorCodes('&', title.getValue(profile));
        } catch (final QuestException e) {
            log.warn(pack, "Could not get title for menu " + menuID + ": " + e.getMessage());
            return "";
        }
    }

    /**
     * @param profile the player of the {@link Profile} to get the items for
     * @return get the items for all slots
     */
    public MenuItem[] getItems(final Profile profile) {
        final MenuItem[] items = new MenuItem[this.getSize()];
        for (int i = 0; i < items.length; i++) {
            items[i] = this.getItem(profile, i);
        }
        return items;
    }

    /**
     * Get a menu item for a specific slot.
     *
     * @param profile the player {@link Profile} to get the item for
     * @param slot    for which the item should be get
     * @return menu item for that slot or null if none is specified
     */
    @Nullable
    public MenuItem getItem(final Profile profile, final int slot) {
        for (final Slots slots : this.slots) {
            if (slots.containsSlot(slot)) {
                return slots.getItem(profile, slot);
            }
        }
        return null;
    }

    /**
     * A command which can be used to open the gui.
     * To perform the command a player must match all open conditions.
     */
    private class MenuBoundCommand extends SimpleCommand {

        /**
         * Creates a new command for opening this menu.
         *
         * @param log  the custom logger
         * @param name the command name
         */
        public MenuBoundCommand(final BetonQuestLogger log, final String name) {
            super(log, name, 0);
        }

        @Override
        public boolean simpleCommand(final CommandSender sender, final String alias, final String[] args) {
            if (!(sender instanceof final Player player)) {
                sender.sendMessage("Command can only be run by players!");
                return false;
            }
            final OnlineProfile onlineProfile = profileProvider.getProfile(player);
            if (mayOpen(onlineProfile)) {
                log.debug(pack, onlineProfile + " run bound command of " + menuID);
                rpgMenu.openMenu(onlineProfile, menuID);
                return true;
            } else {
                sendMessage(sender, "no_permission");
                return false;
            }
        }
    }
}
