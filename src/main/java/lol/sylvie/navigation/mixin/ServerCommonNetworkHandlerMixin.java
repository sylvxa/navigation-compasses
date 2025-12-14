package lol.sylvie.navigation.mixin;

import lol.sylvie.navigation.gui.LocationGui;
import lol.sylvie.navigation.gui.impl.JavaLocationGui;
import lol.sylvie.navigation.hud.NavigationHandler;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.common.ServerboundCustomClickActionPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// you've gotta be kidding me
@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonNetworkHandlerMixin {
    @Inject(method = "handleCustomClickAction", at = @At("TAIL"))
    public void handleDialogLocationSelect(ServerboundCustomClickActionPacket packet, CallbackInfo ci) {
        if (!(((Object) this instanceof ServerGamePacketListenerImpl playNetworkHandler))) return;

        ServerPlayer player = playNetworkHandler.player;
        if (packet.id().equals(JavaLocationGui.ACTION) && packet.payload().isPresent()) {
            CompoundTag element = packet.payload().get().asCompound().orElse(new CompoundTag());
            Optional<Integer> index = element.getInt("index");
            if (index.isPresent()) {
                UUID uuid = player.getUUID();
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
