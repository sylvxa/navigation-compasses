package lol.sylvie.navigation.gui.impl;

import lol.sylvie.navigation.gui.LocationGui;
import lol.sylvie.navigation.hud.NavigationHandler;
import lol.sylvie.navigation.util.FloodgateHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.util.FormImage;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;

// Technically this isn't necessary (Geyser does dialog translation), but it looks a tad nicer so
public class BedrockLocationGui {
    private final ServerPlayerEntity player;
    private final ItemStack itemStack;
    private final List<LocationGui.Location> locations;

    public BedrockLocationGui(ServerPlayerEntity player, ItemStack itemStack, List<LocationGui.Location> locations) {
        this.player = player;
        this.itemStack = itemStack;
        this.locations = locations;
    }

    public void sendSearchForm() {
        CustomForm.Builder builder = CustomForm.builder()
                .title("Location Search");
        builder.input("Name (not case-sensitive)", locations.getFirst().name());

        builder.validResultHandler((form, response) -> {
            String query = response.asInput();
            sendBrowserForm(query == null ? "" : query.strip());
        });

        builder.closedOrInvalidResultHandler((form, response) -> sendBrowserForm(""));

        sendForm(builder.build());
    }

    public void sendBrowserForm(String search) {
        List<LocationGui.Location> filtered = search.isEmpty() ? locations.stream().sorted().toList() :
                locations.stream()
                        .filter(location -> location.name().toLowerCase().contains(search.toLowerCase()))
                        .sorted()
                        .toList();

        SimpleForm.Builder builder = SimpleForm.builder()
                .title(Text.translatable(itemStack.getItem().getTranslationKey()).getString())
                .content(filtered.isEmpty() ? "No locations found! Try searching again." : "Select a location.");

        builder.button("Search", FormImage.of(FormImage.Type.PATH, "textures/ui/magnifyingGlass.png"));
        for (LocationGui.Location location : filtered) {
            builder.button(location.name(), FormImage.of(FormImage.Type.PATH, "textures/ui/icon_map.png"));
        }

        builder.validResultHandler((form, response) -> {
            int i = response.clickedButtonId();
            if (i == 0) { // Search button pressed
                sendSearchForm();
            } else {
                NavigationHandler.addLocation(player, filtered.get(i - 1));
            }
        });

        sendForm(builder.build());
    }

    protected void sendForm(Form form) {
        FloodgateApi.getInstance().sendForm(player.getUuid(), form);
    }

    public static boolean open(ServerPlayerEntity player, ItemStack itemStack, List<LocationGui.Location> locations) {
        if (!FloodgateHelper.isFloodgatePlayer(player.getUuid())) return false;

        new BedrockLocationGui(player, itemStack, locations).sendBrowserForm("");
        return true;
    }
}
