package pl.betoncraft.betonquest;

import au.com.grieve.multi_version_plugin.MultiVersionPlugin;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.List;

public class BetonQuestPlugin extends MultiVersionPlugin {

    static {
        List<String> versions = new ArrayList<>();

        // Add Server Versions
        String serverVersion = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3].substring(1).replace("_",".");
        switch(serverVersion.substring(0, StringUtils.ordinalIndexOf(serverVersion, ".", 2))) {
            case "1.8":
                versions.add("1_8");
            case "1.9":
                versions.add("1_9");
            case "1.10":
                versions.add("1_10");
            case "1.11":
                versions.add("1_11");
            case "1.12":
                versions.add("1_12");
            case "1.13":
                versions.add("1_13");
        }

        initPlugin("pl.betoncraft.betonquest", "BetonQuest", versions);
    }
}
