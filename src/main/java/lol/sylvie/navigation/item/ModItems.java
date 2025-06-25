package lol.sylvie.navigation.item;

import lol.sylvie.navigation.NavigationCompasses;
import eu.pb4.polymer.core.api.item.PolymerItemGroupUtils;
import lol.sylvie.navigation.item.impl.BiomeLocatorItem;
import lol.sylvie.navigation.item.impl.StructureLocatorItem;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.function.Function;

public class ModItems {
    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(Registries.ITEM_GROUP.getKey(), Identifier.of(NavigationCompasses.MOD_ID, "item_group"));
    public static final ItemGroup ITEM_GROUP = PolymerItemGroupUtils.builder()
            .icon(() -> new ItemStack(Items.COMPASS))
            .displayName(Text.translatable("itemGroup.navigation-compasses"))
            .build();

    public static final Item BIOME_LOCATOR = register(
            "biome_locator",
            BiomeLocatorItem::new,
            new Item.Settings().maxCount(1)
    );

    public static final Item STRUCTURE_LOCATOR = register(
            "structure_locator",
            StructureLocatorItem::new,
            new Item.Settings().maxCount(1)
    );

    public static Item register(String name, Function<Item.Settings, Item> itemFactory, Item.Settings settings) {
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(NavigationCompasses.MOD_ID, name));
        Item item = itemFactory.apply(settings.registryKey(itemKey));
        Registry.register(Registries.ITEM, itemKey, item);

        return item;
    }

    public static void initialize() {
        PolymerItemGroupUtils.registerPolymerItemGroup(ITEM_GROUP_KEY, ITEM_GROUP);

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(itemGroup -> {
            // Add items here:
            itemGroup.add(BIOME_LOCATOR);
            itemGroup.add(STRUCTURE_LOCATOR);
        });
    }
}