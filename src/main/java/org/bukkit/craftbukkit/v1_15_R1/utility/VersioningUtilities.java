package org.bukkit.craftbukkit.v1_15_R1.utility;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public final class VersioningUtilities {

    @NotNull
    public static String getBukkitVersion() {
        String result = "Unknown-Version";

        try (InputStream stream = Bukkit.class.getClassLoader().getResourceAsStream("META-INF/maven/org.spigotmc/spigot-api/pom.properties")) {
            if (stream != null) {
                Properties properties = new Properties();
                properties.load(stream);
                result = properties.getProperty("version");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    @NotNull
    public static String getServerVersion() {
        return VersioningUtilities.class.getPackage().getImplementationVersion();
    }

    private VersioningUtilities() {
    }
}
