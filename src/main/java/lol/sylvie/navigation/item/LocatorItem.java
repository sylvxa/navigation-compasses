package lol.sylvie.navigation.item;

import eu.pb4.polymer.core.api.item.SimplePolymerItem;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import lol.sylvie.navigation.gui.LocationGui;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.List;

public abstract class LocatorItem extends SimplePolymerItem {
    public LocatorItem(Settings settings) {
        super(settings, Items.RECOVERY_COMPASS, true);
    }

    @Override
    public ActionResult use(World world, PlayerEntity user, Hand hand) {
        if (!(user instanceof ServerPlayerEntity player))
            return super.use(world, user, hand);
        ItemStack itemStack = player.getStackInHand(hand);
        LocationGui.open(player, itemStack, generateLocations((ServerWorld) world));
        return ActionResult.SUCCESS;
    }

    protected abstract List<LocationGui.Location> generateLocations(ServerWorld world);

    @Override
    public ItemStack getPolymerItemStack(ItemStack itemStack, TooltipType tooltipType, PacketContext context) {
        ItemStack displayStack = super.getPolymerItemStack(itemStack, tooltipType, context);
        if (!PolymerResourcePackUtils.hasMainPack(context)) {
            displayStack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        return displayStack;
    }

    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return PolymerResourcePackUtils.hasMainPack(context) ? super.getPolymerItemModel(stack, context) : null;
    }
}
