package org.betonquest.betonquest.lib.config.quest;

import org.betonquest.betonquest.api.config.ConfigAccessor;
import org.betonquest.betonquest.api.config.ConfigAccessorFactory;
import org.betonquest.betonquest.api.config.quest.QuestPackage;
import org.betonquest.betonquest.api.config.quest.QuestPackageCache;
import org.betonquest.betonquest.api.config.section.multi.MultiConfiguration;
import org.betonquest.betonquest.api.logger.BetonQuestLogger;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * This {@link QuestPackageImpl} represents all functionality based on a {@link Quest}.
 */
public class QuestPackageImpl extends QuestTemplate implements QuestPackage {

    /**
     * The cache for this {@link QuestPackage}.
     */
    private final QuestPackageCache cache;

    /**
     * Creates a new {@link QuestPackage}. For more information see {@link Quest}.
     *
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param questPath             the path that addresses this {@link QuestPackage}
     * @param root                  the root file of this {@link QuestPackage}
     * @param files                 all files contained by this {@link QuestPackage}
     * @param cache                 the cache for this {@link QuestPackage}
     * @throws InvalidConfigurationException thrown if a {@link QuestPackage} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    public QuestPackageImpl(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final String questPath,
                            final File root, final List<File> files, final QuestPackageCache cache) throws InvalidConfigurationException, FileNotFoundException {
        super(log, configAccessorFactory, questPath, root, files);
        this.cache = cache;
    }

    /**
     * Creates a new {@link QuestPackage}. For more information see {@link Quest}.
     *
     * @param log                   the logger that will be used for logging
     * @param configAccessorFactory the factory that will be used to create {@link ConfigAccessor}s
     * @param questPath             the path that addresses this {@link QuestPackage}
     * @param root                  the root file of this {@link QuestPackage}
     * @param files                 all files contained by this {@link QuestPackage}
     * @throws InvalidConfigurationException thrown if a {@link QuestPackage} could not be created
     *                                       or an exception occurred while creating the {@link MultiConfiguration}
     * @throws FileNotFoundException         thrown if a file could not be found during the creation
     *                                       of a {@link ConfigAccessor}
     */
    public QuestPackageImpl(final BetonQuestLogger log, final ConfigAccessorFactory configAccessorFactory, final String questPath,
                            final File root, final List<File> files) throws InvalidConfigurationException, FileNotFoundException {
        this(log, configAccessorFactory, questPath, root, files, createDefaultCache(configAccessorFactory, root, questPath));
    }

    private static QuestPackageCache createDefaultCache(final ConfigAccessorFactory configAccessorFactory, final File root, final String questPath) throws InvalidConfigurationException, FileNotFoundException {
        final File dir = root.isDirectory() ? root : root.getParentFile();
        final File cacheFile = new File(dir, ".cache.yml");
        if (!cacheFile.exists()) {
            final File parent = cacheFile.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new InvalidConfigurationException("Could not create cache directory for package '" + questPath + "'!");
            }
            try {
                if (!cacheFile.createNewFile()) {
                    throw new InvalidConfigurationException("Could not create cache file for package '" + questPath + "'!");
                }
            } catch (final IOException e) {
                throw new InvalidConfigurationException("Could not create cache file for package '" + questPath + "': " + e.getMessage(), e);
            }
        }
        return new QuestPackageCacheImpl(configAccessorFactory.create(cacheFile));
    }

    @Override
    public QuestPackageCache getCache() {
        return cache;
    }

    @Override
    public boolean hasTemplate(final String templatePath) {
        return getTemplates().contains(templatePath);
    }
}
