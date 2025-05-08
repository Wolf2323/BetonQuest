package org.betonquest.betonquest.compatibility.protocollib.conversation;

import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.common.component.VariableComponent;

/**
 * Menu conversation settings.
 */
public record MenuConvIOSettings(int configSelectionCooldown, int configRefreshDelay, int configLineLength,
                                 int configStartNewLines, boolean configNpcNameNewlineSeparator,
                                 boolean configNpcTextFillNewLines, String configControlSelect,
                                 String configControlCancel, String configControlMove, String configNpcNameAlign,
                                 String configNpcNameType, Component configNpcWrap, VariableComponent configNpcText,
                                 Component configNpcTextReset, Component configOptionWrap,
                                 VariableComponent configOptionText, Component configOptionTextReset,
                                 VariableComponent configOptionSelected, Component configOptionSelectedReset,
                                 Component configOptionSelectedWrap, VariableComponent configNpcNameFormat) {
}
