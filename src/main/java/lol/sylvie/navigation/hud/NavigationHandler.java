package lol.sylvie.navigation.hud;

import lol.sylvie.navigation.gui.LocationGui;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.item.Item;
import net.minecraft.network.packet.s2c.play.WaypointS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.waypoint.Waypoint;
import net.minecraft.world.waypoint.WaypointStyles;

import java.text.DecimalFormat;
import java.util.*;

public class NavigationHandler {
    public static HashMap<UUID, HashMap<LocationGui.Location.Type, ResolvedLocation>> WAYPOINT_MAP = new HashMap<>();
    private static final DecimalFormat FORMAT = new DecimalFormat("0.0");

    public static void initialize() {
        ServerTickEvents.START_WORLD_TICK.register(serverWorld -> serverWorld.getPlayers().stream().filter(p -> WAYPOINT_MAP.containsKey(p.getUuid())).forEach(NavigationHandler::handleOverlay));

        ServerPlayConnectionEvents.JOIN.register((networkHandler, packetSender, server) -> {
            ServerPlayerEntity player = networkHandler.player;
            HashMap<LocationGui.Location.Type, ResolvedLocation> waypoints = WAYPOINT_MAP.get(player.getUuid());
            if (waypoints != null)
                waypoints.values().forEach(r -> r.startTracking(player));
        });
    }

    public static void addLocation(ServerPlayerEntity player, LocationGui.Location location) {
        BlockPos pos = location.position().apply(player.getBlockPos());
        if (pos == null) {
            player.sendMessage(Text.translatable("error.navigation-compasses.not_found").formatted(Formatting.RED), true);
            return;
        }
        ResolvedLocation resolvedLocation = new ResolvedLocation(location, location.generateUUID(), pos, player.getWorld().getRegistryKey());
        HashMap<LocationGui.Location.Type, ResolvedLocation> locations = WAYPOINT_MAP.computeIfAbsent(player.getUuid(), (k) -> new HashMap<>());
        if (locations.containsKey(location.type()))
            locations.get(location.type()).stopTracking(player);
        locations.put(location.type(), resolvedLocation);
        location.activateCooldown(player);
        resolvedLocation.startTracking(player);
    }

    public static void removeLocation(ServerPlayerEntity player, ResolvedLocation location) {
        HashMap<LocationGui.Location.Type, ResolvedLocation> locations = WAYPOINT_MAP.get(player.getUuid());
        locations.remove(location.location.type());
        location.stopTracking(player);
    }

    public static void handleOverlay(ServerPlayerEntity player) {
        ArrayList<ResolvedLocation> choppingBlock = new ArrayList<>();
        Text text = null;
        List<ResolvedLocation> locations = WAYPOINT_MAP.get(player.getUuid()).values().stream().toList();

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

            double distance = player.getPos().multiply(1, 0, 1).distanceTo(location.result.toCenterPos().multiply(1, 0, 1));
            String info = String.format("%s: %s, %s, %s / %s blocks away", data.name(), location.result.getX(), location.result.getY(), location.result.getZ(), FORMAT.format(distance));

            text = Text.literal(info).formatted(data.type().getColor());

            if (distance < 8) {
                text = Text.translatable("message.navigation-compasses.arrived").formatted(Formatting.GREEN);
                choppingBlock.add(location);
            }

            break;
        }

        if (text != null)
            player.sendMessage(text, true);

        choppingBlock.forEach(l -> removeLocation(player, l));
    }

	public static final class ResolvedLocation {
		private final LocationGui.Location location;
		private final UUID source;
		private final BlockPos result;
		private final RegistryKey<World> worldKey;
        private boolean trackedLastTick = false;

		public ResolvedLocation(LocationGui.Location location, UUID source, BlockPos result, RegistryKey<World> worldKey) {
			this.location = location;
			this.source = source;
			this.result = result;
			this.worldKey = worldKey;
		}

		public void startTracking(ServerPlayerEntity player) {
			Waypoint.Config config = new Waypoint.Config();
			config.style = WaypointStyles.BOWTIE;
			config.color = Optional.ofNullable(location.type().getColor().getColorValue());
			player.networkHandler.sendPacket(WaypointS2CPacket.trackPos(this.source, config, this.result));
		}

		public void stopTracking(ServerPlayerEntity player) {
			player.networkHandler.sendPacket(WaypointS2CPacket.untrack(this.source));
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

		public RegistryKey<World> worldKey() {
			return worldKey;
		}

        public void setTrackedLastTick(boolean trackedLastTick) {
            this.trackedLastTick = trackedLastTick;
        }

        public boolean isPlayerInDimension(ServerPlayerEntity player) {
            return player.getWorld().getRegistryKey().equals(this.worldKey);
        }
	}
}
