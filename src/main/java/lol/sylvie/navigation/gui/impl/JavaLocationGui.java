package lol.sylvie.navigation.gui.impl;

import lol.sylvie.navigation.NavigationCompasses;
import lol.sylvie.navigation.gui.LocationGui;
import net.minecraft.dialog.*;
import net.minecraft.dialog.action.DialogAction;
import net.minecraft.dialog.action.DynamicCustomDialogAction;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.dialog.type.MultiActionDialog;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class JavaLocationGui {
    private final ServerPlayerEntity player;
    private final ItemStack itemStack;
    private final List<LocationGui.Location> locations;

    public static final Identifier ACTION = Identifier.of(NavigationCompasses.MOD_ID, "select_location");
    public static final HashMap<UUID, List<LocationGui.Location>> AWAITING_RESPONSE = new HashMap<>();

    public JavaLocationGui(ServerPlayerEntity player, ItemStack itemStack, List<LocationGui.Location> locations) {
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

        ArrayList<DialogActionButtonData> buttons = getDialogActionButtonData(filtered);

        DialogCommonData data = new DialogCommonData(Text.translatable(itemStack.getItem().getTranslationKey()), Optional.empty(), true, false, AfterAction.CLOSE, List.of(), List.of());
        MultiActionDialog dialog = new MultiActionDialog(data, buttons, Optional.empty(), 1);

        AWAITING_RESPONSE.put(player.getUuid(), filtered);
        sendDialog(dialog);
    }

    private static @NotNull ArrayList<DialogActionButtonData> getDialogActionButtonData(List<LocationGui.Location> filtered) {
        ArrayList<DialogActionButtonData> buttons = new ArrayList<>();
        for (int i = 0; i < filtered.size(); i++) {
            LocationGui.Location location = filtered.get(i);

            NbtCompound compound = new NbtCompound();
            compound.putInt("index", i);

            DialogAction action = new DynamicCustomDialogAction(ACTION, Optional.of(compound));
            DialogActionButtonData data = new DialogActionButtonData(new DialogButtonData(Text.literal(location.name()), Dialogs.BUTTON_WIDTH), Optional.of(action));
            buttons.add(data);
        }
        return buttons;
    }

    private void sendDialog(Dialog dialog) {
        player.openDialog(new RegistryEntry.Direct<>(dialog));
    }

    public static void open(ServerPlayerEntity player, ItemStack itemStack, List<LocationGui.Location> locations) {
        AWAITING_RESPONSE.remove(player.getUuid());
        new JavaLocationGui(player, itemStack, locations).sendBrowserDialog("");
    }
}
