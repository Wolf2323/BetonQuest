package org.betonquest.betonquest.compatibility.protocollib.conversation;

import net.kyori.adventure.text.Component;
import org.apache.commons.lang3.tuple.Pair;
import org.betonquest.betonquest.api.common.component.FixedComponentLineWrapper;
import org.betonquest.betonquest.api.common.component.VariableReplacement;
import org.betonquest.betonquest.api.quest.QuestException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public class Display {
    private final Component npcName;

    private final List<Component> npcLines;

    private final List<Pair<List<Component>, List<Component>>> optionLines;

    private final Component scrollUp;

    private final Component scrollDown;

    private final Screen screen;

    public Display(final MenuConvIOSettings settings, final FixedComponentLineWrapper wrapper, final Component npcName,
                   final Component npcText, final List<Component> options) throws QuestException {
        this.npcName = getFormattedNpcName(settings, wrapper, npcName);
        this.npcLines = getFormattedNpcLines(settings, wrapper, npcText);
        this.optionLines = getFormattedOptionLines(settings, wrapper, options);
        this.scrollUp = settings.scrollUp();
        this.scrollDown = settings.scrollDown();
        this.screen = new Screen(getScreenSize(settings), options.size());
    }

    private Component getFormattedNpcName(final MenuConvIOSettings settings, final FixedComponentLineWrapper wrapper,
                                          final Component npcName) throws QuestException {
        final Component name = settings.npcNameFormat().resolve(new VariableReplacement("npc_name", npcName));
        return switch (settings.npcNameAlign()) {
            case "right" -> Component.text(" ".repeat(getRemainingSpace(wrapper, name))).append(name);
            case "center" -> Component.text(" ".repeat(getRemainingSpace(wrapper, name) / 2)).append(name);
            case "left" -> name;
            default -> throw new QuestException("Invalid npc name formatting '" + settings.npcNameAlign() + "'.");
        };
    }

    private int getRemainingSpace(final FixedComponentLineWrapper wrapper, final Component component) {
        return Math.max(0, (wrapper.getMaxLineWidth() - wrapper.width(component))) / wrapper.width(Component.text(" "));
    }

    private List<Component> getFormattedNpcLines(final MenuConvIOSettings settings, final FixedComponentLineWrapper wrapper, final Component npcText) {
        final VariableReplacement replacement = new VariableReplacement("npc_text", npcText);
        return wrapper.splitWidth(settings.npcText().resolve(replacement));
    }

    private List<Pair<List<Component>, List<Component>>> getFormattedOptionLines(final MenuConvIOSettings settings,
                                                                                 final FixedComponentLineWrapper wrapper,
                                                                                 final List<Component> options) {
        final List<Pair<List<Component>, List<Component>>> optionLines = new ArrayList<>();
        for (final Component option : options) {
            final VariableReplacement replacement = new VariableReplacement("option_text", option);
            final List<Component> optionUnselected = wrapper.splitWidth(settings.optionText().resolve(replacement),
                    getPrefixComponentSupplier(settings.optionWrap()));
            final List<Component> optionSelected = wrapper.splitWidth(settings.optionSelected().resolve(replacement),
                    getPrefixComponentSupplier(settings.optionSelectedWrap()));

            final int optionUnselectedSize = optionUnselected.size();
            final int optionSelectedSize = optionSelected.size();
            if (optionUnselectedSize < optionSelectedSize) {
                fillOptionLines(optionUnselected, optionSelectedSize - optionUnselectedSize);
            } else if (optionSelectedSize < optionUnselectedSize) {
                fillOptionLines(optionSelected, optionUnselectedSize - optionSelectedSize);
            }
            optionLines.add(Pair.of(optionUnselected, optionSelected));
        }
        return optionLines;
    }

    private void fillOptionLines(final List<Component> options, final int diff) {
        for (int i = 0; i < diff; i++) {
            options.add(Component.empty());
        }
    }

    private Supplier<Component> getPrefixComponentSupplier(final Component component) {
        final AtomicBoolean first = new AtomicBoolean(true);
        return () -> {
            if (first.get()) {
                first.set(false);
                return Component.empty();
            } else {
                return component;
            }
        };
    }

    private int getScreenSize(final MenuConvIOSettings settings) {
        int seperator = 0;
        if (settings.npcNameType().equalsIgnoreCase("chat")) {
            seperator++;
            if (settings.npcNameSeperator()) {
                seperator++;
            }
        }
        if (settings.optionsSeperator()) {
            seperator++;
        }
        final int optionsSize = optionLines.stream().mapToInt(list -> list.getKey().size()).sum();
        return seperator + npcLines.size() + +optionsSize;
    }

    public Component getDisplay(final int scroll) {

    }

    private class Screen {
        private final int screenSize;

        private final int optionSize;

        private int scroll;

        private int select;

        public Screen(final int screenSize, final int optionSize) {
            this.screenSize = screenSize;
            this.optionSize = optionSize;
            scroll = 0;
            select = -1;
        }

        public int getScroll() {
            return scroll;
        }

        public void setScroll(final int scroll) {
            if (scroll)
                this.scroll = scroll;
        }

        public int getSelect() {
            return select;
        }

        public void setSelect(final int select) {
            this.select = select;
        }
    }
}
