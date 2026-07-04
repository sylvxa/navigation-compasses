package lol.sylvie.navigation.hud;

import lol.sylvie.navigation.gui.LocationGui;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.protocol.game.ClientboundTrackedWaypointPacket;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.waypoints.Waypoint;
import net.minecraft.world.waypoints.WaypointStyleAssets;
import org.jspecify.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.*;

public class NavigationHandler {
    public static HashMap<UUID, HashMap<LocationGui.Location.Type, ResolvedLocation>> WAYPOINT_MAP = new HashMap<>();
    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    public static void initialize() {
        ServerTickEvents.START_LEVEL_TICK.register(serverWorld -> serverWorld.players().stream().filter(p -> WAYPOINT_MAP.containsKey(p.getUUID())).forEach(NavigationHandler::handleOverlay));

        ServerPlayConnectionEvents.JOIN.register((networkHandler, packetSender, server) -> {
            ServerPlayer player = networkHandler.player;
            HashMap<LocationGui.Location.Type, ResolvedLocation> waypoints = WAYPOINT_MAP.get(player.getUUID());
            if (waypoints != null) waypoints.values().forEach(r -> r.startTracking(player));
        });
    }

    public static void addLocation(ServerPlayer player, LocationGui.Location location) {
        BlockPos pos = location.position().apply(player.blockPosition());
        if (pos == null) {
            player.sendOverlayMessage(Component.translatable("error.navigation-compasses.not_found").withStyle(ChatFormatting.RED));
            return;
        }
        ResolvedLocation resolvedLocation = new ResolvedLocation(location, location.generateUUID(), pos, player.level().dimension());
        HashMap<LocationGui.Location.Type, ResolvedLocation> locations = WAYPOINT_MAP.computeIfAbsent(player.getUUID(), (k) -> new HashMap<>());
        if (locations.containsKey(location.type())) locations.get(location.type()).stopTracking(player);
        locations.put(location.type(), resolvedLocation);
        location.activateCooldown(player);
        resolvedLocation.startTracking(player);
    }

    public static void removeLocation(ServerPlayer player, ResolvedLocation location) {
        HashMap<LocationGui.Location.Type, ResolvedLocation> locations = WAYPOINT_MAP.get(player.getUUID());
        locations.remove(location.location.type());
        location.stopTracking(player);
    }

    public static void handleOverlay(ServerPlayer player) {
        ArrayList<ResolvedLocation> choppingBlock = new ArrayList<>();
        Component text = null;
        List<ResolvedLocation> locations = WAYPOINT_MAP.get(player.getUUID()).values().stream().toList();

        for (ResolvedLocation location : locations) {
            boolean sameDimension = location.isPlayerInDimension(player);
            if (!sameDimension) {
                if (location.trackedLastTick) {
                    location.setTrackedLastTick(false);
                    location.stopTracking(player);
                }
                continue;
            } else if (!location.trackedLastTick) {
                location.setTrackedLastTick(true);
                location.startTracking(player);
            }

            LocationGui.Location data = location.location;
            Item compass = data.type().getItem();

            if (!player.isHolding(compass)) continue;

            double distance = player.position().multiply(1, 0, 1).distanceTo(Vec3.atCenterOf(location.result).multiply(1, 0, 1));
            String info = String.format("%s: %s, %s, %s / %s blocks away", data.name(), location.result.getX(), location.result.getY(), location.result.getZ(), FORMAT.format(distance));

            text = Component.literal(info).withStyle(data.type().getColor());

            if (distance < 8) {
                text = Component.translatable("message.navigation-compasses.arrived").withStyle(ChatFormatting.GREEN);
                choppingBlock.add(location);
            }

            break;
        }

        if (text != null) player.sendOverlayMessage(text);

        choppingBlock.forEach(l -> removeLocation(player, l));
    }

    public static final class ResolvedLocation {
        private final LocationGui.Location location;
        private final UUID source;
        private final BlockPos result;
        private final ResourceKey<Level> worldKey;
        private boolean trackedLastTick = false;

        public ResolvedLocation(LocationGui.Location location, UUID source, BlockPos result, ResourceKey<Level> worldKey) {
            this.location = location;
            this.source = source;
            this.result = result;
            this.worldKey = worldKey;
        }

        public void startTracking(ServerPlayer player) {
            Waypoint.Icon config = new Waypoint.Icon();
            config.style = WaypointStyleAssets.BOWTIE;
            config.color = Optional.ofNullable(TextColor.fromLegacyFormat(location.type().getColor()))
                    .map(TextColor::getValue);
            player.connection.send(ClientboundTrackedWaypointPacket.addWaypointPosition(this.source, config, this.result));
        }

        public void stopTracking(ServerPlayer player) {
            player.connection.send(ClientboundTrackedWaypointPacket.removeWaypoint(this.source));
        }

        public LocationGui.Location location() {
            return location;
        }

        public UUID source() {
            return source;
        }

        public BlockPos result() {
            return result;
        }

        public ResourceKey<Level> worldKey() {
            return worldKey;
        }

        public void setTrackedLastTick(boolean trackedLastTick) {
            this.trackedLastTick = trackedLastTick;
        }

        public boolean isPlayerInDimension(ServerPlayer player) {
            return player.level().dimension().equals(this.worldKey);
        }
    }
}
