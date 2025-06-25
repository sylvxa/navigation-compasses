package lol.sylvie.navigation.mixin;

import lol.sylvie.navigation.gui.LocationGui;
import lol.sylvie.navigation.gui.impl.JavaLocationGui;
import lol.sylvie.navigation.hud.NavigationHandler;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.c2s.common.CustomClickActionC2SPacket;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// you've gotta be kidding me
@Mixin(ServerCommonNetworkHandler.class)
public class ServerCommonNetworkHandlerMixin {
    @Inject(method = "onCustomClickAction", at = @At("TAIL"))
    public void handleDialogLocationSelect(CustomClickActionC2SPacket packet, CallbackInfo ci) {
        if (!(((Object) this instanceof ServerPlayNetworkHandler playNetworkHandler))) return;

        ServerPlayerEntity player = playNetworkHandler.player;
        if (packet.id().equals(JavaLocationGui.ACTION) && packet.payload().isPresent()) {
            NbtCompound element = packet.payload().get().asCompound().orElse(new NbtCompound());
            Optional<Integer> index = element.getInt("index");
            if (index.isPresent()) {
                UUID uuid = player.getUuid();
                List<LocationGui.Location> locations = JavaLocationGui.AWAITING_RESPONSE.get(uuid);
                if (locations == null) return;

                int actualIndex = index.get();
                if (actualIndex >= locations.size()) return;

                NavigationHandler.addLocation(player, locations.get(actualIndex));
                JavaLocationGui.AWAITING_RESPONSE.remove(uuid);
            }
        }
    }
}
