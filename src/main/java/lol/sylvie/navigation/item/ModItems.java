package lol.sylvie.navigation.item;

import lol.sylvie.navigation.NavigationCompasses;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import lol.sylvie.navigation.item.impl.BiomeLocatorItem;
import lol.sylvie.navigation.item.impl.StructureLocatorItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import java.util.function.Function;

public class ModItems {
    public static final ResourceKey<CreativeModeTab> ITEM_GROUP_KEY = ResourceKey.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), Identifier.fromNamespaceAndPath(NavigationCompasses.MOD_ID, "item_group"));
    public static final CreativeModeTab ITEM_GROUP = PolymerItemGroupUtils.builder()
            .icon(() -> new ItemStack(Items.COMPASS))
            .title(Component.translatable("itemGroup.navigation-compasses"))
            .build();

    public static final Item BIOME_LOCATOR = register(
            "biome_locator",
            BiomeLocatorItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static final Item STRUCTURE_LOCATOR = register(
            "structure_locator",
            StructureLocatorItem::new,
            new Item.Properties().stacksTo(1)
    );

    public static Item register(String name, Function<Item.Properties, Item> itemFactory, Item.Properties settings) {
        ResourceKey<Item> itemKey = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(NavigationCompasses.MOD_ID, name));
        Item item = itemFactory.apply(settings.setId(itemKey));
        Registry.register(BuiltInRegistries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(ITEM_GROUP_KEY, ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(itemGroup -> {
            // Add items here:
            itemGroup.accept(BIOME_LOCATOR);
            itemGroup.accept(STRUCTURE_LOCATOR);
        });
    }
}