package lol.sylvie.navigation.util;

import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;

public class FloodgateHelper {
    public static boolean FLOODGATE_PRESENT = false;
    static {
        try {
            Class.forName("org.geysermc.floodgate.api.FloodgateApi");
            FLOODGATE_PRESENT = true;
        } catch (ClassNotFoundException ignored) {}
    }

    public static boolean isFloodgatePlayer(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }
}
