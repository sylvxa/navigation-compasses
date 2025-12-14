package lol.sylvie.navigation.gui;

import lol.sylvie.navigation.config.ConfigHandler;
import lol.sylvie.navigation.gui.impl.BedrockLocationGui;
import lol.sylvie.navigation.gui.impl.JavaLocationGui;
import lol.sylvie.navigation.item.ModItems;
import lol.sylvie.navigation.util.FloodgateHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

public class LocationGui {
    public static void open(ServerPlayer player, ItemStack itemStack, List<Location> locations) {
        if (!(FloodgateHelper.FLOODGATE_PRESENT && BedrockLocationGui.open(player, itemStack, locations)))
            JavaLocationGui.open(player, itemStack, locations);
    }

    public record Location(Type type, String name, Function<BlockPos, BlockPos> position) implements Comparable<Location> {
        public UUID generateUUID() {
            return UUID.nameUUIDFromBytes(name.getBytes(StandardCharsets.UTF_8));
        }

        public void activateCooldown(ServerPlayer player) {
            player.getCooldowns().addCooldown(type.item.getDefaultInstance(), ConfigHandler.STATE.cooldown());
        }

        @Override
        public int compareTo(@NotNull LocationGui.Location location) {
            return this.name.compareTo(location.name);
        }

        public enum Type {
            BIOME(ChatFormatting.GREEN, ModItems.BIOME_LOCATOR),
            STRUCTURE(ChatFormatting.GRAY, ModItems.STRUCTURE_LOCATOR);

            private final ChatFormatting color;
            private final Item item;

            Type(ChatFormatting color, Item item) {
                this.color = color;
                this.item = item;
            }

            public ChatFormatting getColor() {
                return this.color;
            }

            public Item getItem() {
                return item;
            }
        }
    }
}
