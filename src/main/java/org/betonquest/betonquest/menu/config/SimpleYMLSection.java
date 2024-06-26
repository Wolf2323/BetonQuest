package org.betonquest.betonquest.menu.config;

import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.id.ConditionID;
import org.betonquest.betonquest.id.EventID;
import org.betonquest.betonquest.id.ID;
import org.betonquest.betonquest.instruction.argument.IDArgument;
import org.betonquest.betonquest.instruction.variable.VariableBoolean;
import org.betonquest.betonquest.instruction.variable.VariableNumber;
import org.betonquest.betonquest.instruction.variable.VariableString;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.jetbrains.annotations.Nullable;

import java.io.Serial;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class to help parsing of yml config files.
 */
@SuppressWarnings({"PMD.PreserveStackTrace", "PMD.AbstractClassWithoutAbstractMethod"})
public abstract class SimpleYMLSection {
    /**
     * Prefix for exception messages.
     */
    public static final String RPG_MENU_CONFIG_SETTING = "RPGMenuConfig setting ";

    /**
     * Quest Package this section is in.
     */
    protected final QuestPackage pack;

    /**
     * Backing section containing values.
     */
    protected final ConfigurationSection config;

    /**
     * The Identifier of this section.
     */
    protected final String name;

    /**
     * Creates a new section for getting validated values.
     *
     * @param pack   the pack the section is in
     * @param name   the name of the
     * @param config the backing config providing values
     * @throws InvalidConfigurationException if the backing configuration is empty
     */
    public SimpleYMLSection(final QuestPackage pack, final String name, final ConfigurationSection config) throws InvalidConfigurationException {
        this.pack = pack;
        this.config = config;
        this.name = name;
        if (config.getKeys(false).isEmpty()) {
            throw new InvalidSimpleConfigException("RPGMenuConfig is invalid or empty!");
        }
    }

    private String getRawString(final String key) throws Missing {
        final String string = config.getString(key);
        if (string == null) {
            throw new Missing(key);
        } else {
            return string;
        }
    }

    /**
     * Parse string from a config file.
     *
     * @param key where to search
     * @return the {@link VariableString} from the config
     * @throws Missing        If string is not given
     * @throws QuestException If the {@link VariableString} could not be created
     */
    protected final VariableString getString(final String key) throws Missing, QuestException {
        return new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, getRawString(key));
    }

    /**
     * Parse a list of strings from a config file.
     *
     * @param key where to search
     * @return the list of {@link VariableString} from the config
     * @throws Missing        If no list is not given
     * @throws QuestException If one {@link VariableString} could not be created
     */
    protected final List<VariableString> getStringList(final String key) throws Missing, QuestException {
        final List<String> stringList = config.getStringList(key);
        if (stringList.isEmpty()) {
            throw new Missing(key);
        } else {
            final List<VariableString> list = new ArrayList<>();
            for (final String string : stringList) {
                list.add(new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, string));
            }
            return list;
        }
    }

    /**
     * Parse a list of multiple strings, separated by ',' from a config file.
     *
     * @param key where to search
     * @return the list of {@link VariableString} from the config
     * @throws Missing        If no strings are given
     * @throws QuestException If one {@link VariableString} could not be created
     */
    protected final List<VariableString> getStrings(final String key) throws Missing, QuestException {
        final List<VariableString> list = new ArrayList<>();
        final String string = getRawString(key);
        final String[] args = string.split(",");
        for (final String arg : args) {
            final String argTrim = arg.trim();
            if (!argTrim.isEmpty()) {
                list.add(new VariableString(BetonQuest.getInstance().getVariableProcessor(), pack, argTrim));
            }
        }
        return list;
    }

    /**
     * Parse a number from a config file.
     *
     * @param key where to search
     * @return the {@link VariableNumber} from the config
     * @throws Missing        If nothing is given
     * @throws QuestException If the {@link VariableNumber} could not be created
     */
    protected final VariableNumber getNumber(final String key) throws Missing, QuestException {
        return new VariableNumber(BetonQuest.getInstance().getVariableProcessor(), pack, getRawString(key));
    }

    /**
     * Parse a boolean from a config file.
     *
     * @param key where to search
     * @return the {@link VariableBoolean} from the config
     * @throws Missing        If nothing is given
     * @throws QuestException If the {@link VariableBoolean} could not be created
     */
    protected final VariableBoolean getBoolean(final String key) throws Missing, QuestException {
        final String stringBoolean = getRawString(key).trim();
        return new VariableBoolean(BetonQuest.getInstance().getVariableProcessor(), pack, stringBoolean);
    }

    /**
     * Parse a list of events from a config file.
     *
     * @param key  where to search
     * @param pack configuration package of this file
     * @return requested EventIDs or empty list when not present
     * @throws Invalid if one of the events can't be found
     */
    protected final List<EventID> getEvents(final String key, final QuestPackage pack) throws Invalid {
        return getID(key, pack, EventID::new);
    }

    /**
     * Parse a list of conditions from a config file.
     *
     * @param key  where to search
     * @param pack configuration package of this file
     * @return requested ConditionIDs or empty list when not present
     * @throws Invalid if one of the conditions can't be found
     */
    protected final List<ConditionID> getConditions(final String key, final QuestPackage pack) throws Invalid {
        return getID(key, pack, ConditionID::new);
    }

    private <T extends ID> List<T> getID(final String key, final QuestPackage pack, final IDArgument<T> argument) throws Invalid {
        final List<VariableString> strings;
        try {
            strings = getStrings(key);
        } catch (final Missing | QuestException ignored) {
            return List.of();
        }
        final List<T> ids = new ArrayList<>(strings.size());
        for (final VariableString string : strings) {
            try {
                ids.add(argument.convert(pack, string.getValue(null)));
            } catch (final QuestException e) {
                throw new Invalid(key, e);
            }
        }
        return ids;
    }

    /**
     * A config setting which uses a given default value if not set.
     *
     * @param <T> the type of the setting
     */
    @SuppressWarnings("PMD.CommentRequired")
    protected abstract class DefaultSetting<T> {

        private final T value;

        @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
        public DefaultSetting(final T defaultValue) throws Invalid {
            value = parse(defaultValue);
        }

        private T parse(final T defaultValue) throws Invalid {
            try {
                return of();
            } catch (final Missing missing) {
                return defaultValue;
            }
        }

        @SuppressWarnings("PMD.ShortMethodName")
        protected abstract T of() throws Missing, Invalid;

        public final T get() {
            return value;
        }
    }

    /**
     * A config setting which doesn't throw a Missing exception if not specified.
     *
     * @param <T> the type of the setting
     */
    @SuppressWarnings("PMD.CommentRequired")
    protected abstract class OptionalSetting<T> {
        @Nullable
        private final T optional;

        @SuppressWarnings("PMD.ConstructorCallsOverridableMethod")
        public OptionalSetting() throws Invalid {
            optional = parse();
        }

        @Nullable
        private T parse() throws Invalid {
            try {
                return of();
            } catch (final Missing missing) {
                return null;
            }
        }

        @SuppressWarnings("PMD.ShortMethodName")
        protected abstract T of() throws Missing, Invalid;

        @Nullable
        public final T get() {
            return optional;
        }
    }

    /**
     * Thrown when the config could not be loaded due to an error.
     */
    public class InvalidSimpleConfigException extends InvalidConfigurationException {
        @Serial
        private static final long serialVersionUID = 5231741827329435199L;

        /**
         * Creates a new Invalid Simple config exception.
         *
         * @param cause the cause of the exception
         */
        public InvalidSimpleConfigException(final String cause) {
            super("Could not load '" + name + "': " + cause);
        }

        /**
         * The name of the throwing section.
         *
         * @return the throwing section's name
         */
        public final String getName() {
            return name;
        }
    }

    /**
     * Thrown when a setting is missing.
     */
    public class Missing extends InvalidSimpleConfigException {
        @Serial
        private static final long serialVersionUID = 1827433702663413827L;

        /**
         * Creates a new Missing exception.
         *
         * @param missingSetting the missing setting
         */
        public Missing(final String missingSetting) {
            super(RPG_MENU_CONFIG_SETTING + missingSetting + " is missing!");
        }

        /**
         * Creates a new Missing exception.
         *
         * @param missingSetting the missing setting
         * @param cause          the reason why it is missing
         */
        public Missing(final String missingSetting, final Throwable cause) {
            super(RPG_MENU_CONFIG_SETTING + missingSetting + " is missing: " + cause.getMessage());
        }
    }

    /**
     * Thrown when a setting is invalid.
     */
    public class Invalid extends InvalidSimpleConfigException {
        @Serial
        private static final long serialVersionUID = -4898301219445719212L;

        /**
         * Creates a new Invalid exception.
         *
         * @param invalidSetting the invalid setting
         */
        public Invalid(final String invalidSetting) {
            super(RPG_MENU_CONFIG_SETTING + invalidSetting + " is invalid!");
        }

        /**
         * Creates a new Invalid exception.
         *
         * @param invalidSetting the invalid setting
         * @param cause          the reason why it is invalid
         */
        public Invalid(final String invalidSetting, final String cause) {
            super(RPG_MENU_CONFIG_SETTING + invalidSetting + " is invalid: " + cause);
        }

        /**
         * Creates a new Invalid exception.
         *
         * @param invalidSetting the invalid setting
         * @param cause          the reason why it is invalid
         */
        public Invalid(final String invalidSetting, final Throwable cause) {
            super(RPG_MENU_CONFIG_SETTING + invalidSetting + " is invalid: " + cause.getMessage());
        }
    }
}
