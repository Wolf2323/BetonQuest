package org.betonquest.betonquest.compatibility.packetevents.interceptor;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

import java.util.function.Consumer;

public class ComponentTagger {
    private final TextComponent tag;

    public ComponentTagger(final String tag) {
        this.tag = Component.text(tag);
    }

    public Component prefixTag(final Component original) {
        return tag.append(original);
    }

    public boolean isTagged(final Component component) {
        return component instanceof final TextComponent textComponent
                && tag.content().equals(textComponent.content());
    }

    public Component removeTag(final Component component) {
        return component.children().get(0);
    }

    public boolean acceptIfTagged(final Component component, final Consumer<Component> untagged) {
        if (isTagged(component)) {
            untagged.accept(removeTag(component));
            return true;
        }
        return false;
    }
}
