package lol.sylvie.navigation.gui;

import lol.sylvie.navigation.config.ConfigHandler;
import lol.sylvie.navigation.gui.impl.BedrockLocationGui;
import lol.sylvie.navigation.gui.impl.JavaLocationGui;
import lol.sylvie.navigation.item.ModItems;
import lol.sylvie.navigation.util.FloodgateHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class LocationGui {
    public static void open(ServerPlayerEntity player, ItemStack itemStack, List<Location> locations) {
        if (!(FloodgateHelper.FLOODGATE_PRESENT && BedrockLocationGui.open(player, itemStack, locations)))
            JavaLocationGui.open(player, itemStack, locations);
    }

    public record Location(Type type, String name, Function<BlockPos, BlockPos> position) implements Comparable<Location> {
        public UUID generateUUID() {
            return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        }

        public void activateCooldown(ServerPlayerEntity player) {
            player.getItemCooldownManager().set(type.item.getDefaultStack(), ConfigHandler.STATE.cooldown());
        }

        @Override
        public int compareTo(@NotNull LocationGui.Location location) {
            return this.name.compareTo(location.name);
        }

        public enum Type {
            BIOME(Formatting.GREEN, ModItems.BIOME_LOCATOR),
            STRUCTURE(Formatting.GRAY, ModItems.STRUCTURE_LOCATOR);

            private final Formatting color;
            private final Item item;

            Type(Formatting color, Item item) {
                this.color = color;
                this.item = item;
            }

            public Formatting getColor() {
                return this.color;
            }

            public Item getItem() {
                return item;
            }
        }
    }
}
