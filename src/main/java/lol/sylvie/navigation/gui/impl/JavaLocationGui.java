package lol.sylvie.navigation.gui.impl;

import lol.sylvie.navigation.NavigationCompasses;
import lol.sylvie.navigation.gui.LocationGui;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.dialog.ActionButton;
import net.minecraft.server.dialog.CommonButtonData;
import net.minecraft.server.dialog.CommonDialogData;
import net.minecraft.server.dialog.Dialog;
import net.minecraft.server.dialog.DialogAction;
import net.minecraft.server.dialog.Dialogs;
import net.minecraft.server.dialog.MultiActionDialog;
import net.minecraft.server.dialog.action.Action;
import net.minecraft.server.dialog.action.CustomAll;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JavaLocationGui {
    private final ServerPlayer player;
    private final ItemStack itemStack;
    private final List<LocationGui.Location> locations;

    public static final Identifier ACTION = Identifier.fromNamespaceAndPath(NavigationCompasses.MOD_ID, "select_location");
    public static final HashMap<UUID, List<LocationGui.Location>> AWAITING_RESPONSE = new HashMap<>();

    public JavaLocationGui(ServerPlayer player, ItemStack itemStack, List<LocationGui.Location> locations) {
        this.player = player;
        this.itemStack = itemStack;
        this.locations = locations;
    }

    public void sendBrowserDialog(String search) {
        List<LocationGui.Location> filtered = search.isEmpty() ? locations.stream().sorted().toList() :
                locations.stream()
                        .filter(location -> location.name().toLowerCase().contains(search.toLowerCase()))
                        .sorted()
                        .toList();

        ArrayList<ActionButton> buttons = getDialogActionButtonData(filtered);

        CommonDialogData data = new CommonDialogData(Component.translatable(itemStack.getItem().getDescriptionId()), Optional.empty(), true, false, DialogAction.CLOSE, List.of(), List.of());
        MultiActionDialog dialog = new MultiActionDialog(data, buttons, Optional.empty(), 1);

        AWAITING_RESPONSE.put(player.getUUID(), filtered);
        sendDialog(dialog);
    }

    private static @NotNull ArrayList<ActionButton> getDialogActionButtonData(List<LocationGui.Location> filtered) {
        ArrayList<ActionButton> buttons = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            LocationGui.Location location = filtered.get(i);

            CompoundTag compound = new CompoundTag();
            compound.putInt("index", i);

            Action action = new CustomAll(ACTION, Optional.of(compound));
            ActionButton data = new ActionButton(new CommonButtonData(Component.literal(location.name()), Dialogs.BIG_BUTTON_WIDTH), Optional.of(action));
            buttons.add(data);
        }
        return buttons;
    }

    private void sendDialog(Dialog dialog) {
        player.openDialog(new Holder.Direct<>(dialog));
    }

    public static void open(ServerPlayer player, ItemStack itemStack, List<LocationGui.Location> locations) {
        AWAITING_RESPONSE.remove(player.getUUID());
        new JavaLocationGui(player, itemStack, locations).sendBrowserDialog("");
    }
}
