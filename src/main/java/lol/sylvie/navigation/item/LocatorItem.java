package lol.sylvie.navigation.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import lol.sylvie.navigation.gui.LocationGui;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public abstract class LocatorItem extends SimplePolymerItem {
    public LocatorItem(Properties settings) {
        super(settings, Items.RECOVERY_COMPASS, true);
    }

    @Override
    public InteractionResult use(Level world, Player user, InteractionHand hand) {
        if (!(user instanceof ServerPlayer player))
            return super.use(world, user, hand);
        ItemStack itemStack = player.getItemInHand(hand);
        LocationGui.open(player, itemStack, generateLocations((ServerLevel) world));
        return InteractionResult.SUCCESS;
    }

    protected abstract List<LocationGui.Location> generateLocations(ServerLevel world);

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipFlag tooltipType, PacketContext context) {
        ItemStack displayStack = super.getPolymerItemStack(itemStack, tooltipType, context);
        if (!PolymerResourcePackUtils.hasMainPack(context)) {
            displayStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        return displayStack;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return PolymerResourcePackUtils.hasMainPack(context) ? super.getPolymerItemModel(stack, context) : null;
    }
}
