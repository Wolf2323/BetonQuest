package org.betonquest.betonquest.compatibility.protocollib.conversation;

import org.bukkit.configuration.ConfigurationSection;

/**
 * Menu conversation settings.
 */
public record MenuConvIOSettings(int configSelectionCooldown, int configRefreshDelay, int configLineLength,
                                 int configStartNewLines, boolean configNpcNameNewlineSeparator,
                                 boolean configNpcTextFillNewLines, String configControlSelect,
                                 String configControlCancel, String configControlMove, String configNpcNameAlign,
                                 String configNpcNameType, String configNpcWrap, String configNpcText,
                                 String configNpcTextReset, String configOptionWrap, String configOptionText,
                                 String configOptionTextReset, String configOptionSelected,
                                 String configOptionSelectedReset, String configOptionSelectedWrap,
                                 String configNpcNameFormat) {

    /**
     * Creates a new instance of MenuConvIOSettings from a configuration section.
     *
     * @param config the configuration section to read settings from
     * @return a new instance of MenuConvIOSettings
     */
    public static MenuConvIOSettings fromConfigurationSection(final ConfigurationSection config) {
        final int configSelectionCooldown = config.getInt("selectionCooldown", 10);
        final int configRefreshDelay = config.getInt("refresh_delay", 180);
        final int configLineLength = config.getInt("line_length", 50);
        final int configStartNewLines = config.getInt("start_new_lines", 10);
        final boolean configNpcNameNewlineSeparator = config.getBoolean("npc_name_newline_separator", true);
        final boolean configNpcTextFillNewLines = config.getBoolean("npc_text_fill_new_lines", true);
        final String configControlSelect = config.getString("control_select", "jump,left_click");
        final String configControlCancel = config.getString("control_cancel", "sneak");
        final String configControlMove = config.getString("control_move", "scroll,move");
        final String configNpcNameAlign = config.getString("npc_name_align", "center");
        final String configNpcNameType = config.getString("npc_name_type", "chat");

        final String configNpcWrap = config.getString("npc_wrap", "&l &r").replace('&', '§');
        final String configNpcText = config.getString("npc_text", "&l &r&f{npc_text}").replace('&', '§');
        final String configNpcTextReset = config.getString("npc_text_reset", "&f").replace('&', '§');
        final String configOptionWrap = config.getString("option_wrap", "&r&l &l &l &l &r").replace('&', '§');
        final String configOptionText = config.getString("option_text", "&l &l &l &l &r&8[ &b{option_text}&8 ]").replace('&', '§');
        final String configOptionTextReset = config.getString("option_text_reset", "&b").replace('&', '§');
        final String configOptionSelected = config.getString("option_selected", "&l &r &r&7»&r &8[ &f&n{option_text}&8 ]").replace('&', '§');
        final String configOptionSelectedReset = config.getString("option_selected_reset", "&f").replace('&', '§');
        final String configOptionSelectedWrap = config.getString("option_selected_wrap", "&r&l &l &l &l &r&f&n").replace('&', '§');
        final String configNpcNameFormat = config.getString("npc_name_format", "&e{npc_name}&r").replace('&', '§');

        return new MenuConvIOSettings(configSelectionCooldown, configRefreshDelay, configLineLength,
                configStartNewLines, configNpcNameNewlineSeparator, configNpcTextFillNewLines, configControlSelect,
                configControlCancel, configControlMove, configNpcNameAlign, configNpcNameType, configNpcWrap,
                configNpcText, configNpcTextReset, configOptionWrap, configOptionText, configOptionTextReset,
                configOptionSelected, configOptionSelectedReset, configOptionSelectedWrap, configNpcNameFormat);
    }
}
