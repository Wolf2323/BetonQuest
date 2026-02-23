package org.betonquest.betonquest.kernel.component.types;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.betonquest.betonquest.api.text.TextParserRegistry;
import org.betonquest.betonquest.conversation.ConversationColors;
import org.betonquest.betonquest.kernel.AbstractCoreComponent;
import org.betonquest.betonquest.kernel.DependencyProvider;
import org.betonquest.betonquest.text.parser.LegacyParser;
import org.betonquest.betonquest.text.parser.MineDownParser;
import org.betonquest.betonquest.text.parser.MiniMessageParser;
import org.bukkit.ChatColor;

import java.util.Set;

/**
 * The {@link AbstractCoreComponent} loading text parser types.
 */
public class TextParserTypesComponent extends AbstractCoreComponent {

    /**
     * Create a new TextParserTypesComponent.
     */
    public TextParserTypesComponent() {
        super();
    }

    @Override
    public Set<Class<?>> requires() {
        return Set.of(TextParserRegistry.class, ConversationColors.class);
    }

    @Override
    protected void load(final DependencyProvider dependencyProvider) {
        final TextParserRegistry textParserRegistry = getDependency(TextParserRegistry.class);
        final ConversationColors conversationColors = getDependency(ConversationColors.class);

        final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.builder()
                .hexColors()
                .useUnusualXRepeatedCharacterHexFormat()
                .extractUrls()
                .build();
        textParserRegistry.register("legacy", new LegacyParser(legacySerializer));
        final MiniMessage miniMessage = MiniMessage.miniMessage();
        textParserRegistry.register("minimessage", new MiniMessageParser(miniMessage));
        final MiniMessage legacyMiniMessage = MiniMessage.builder()
                .preProcessor(input -> {
                    final TextComponent deserialize = legacySerializer.deserialize(ChatColor.translateAlternateColorCodes('&', input.replaceAll("(?<!\\\\)\\\\n", "\n")));
                    final String serialize = miniMessage.serialize(deserialize);
                    return serialize.replaceAll("\\\\<", "<");
                })
                .build();
        textParserRegistry.register("legacyminimessage", new MiniMessageParser(legacyMiniMessage));
        textParserRegistry.register("minedown", new MineDownParser());

        conversationColors.load();
    }
}
