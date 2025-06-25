package lol.sylvie.navigation.datagen.impl;

import lol.sylvie.navigation.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup registryLookup, RecipeExporter exporter) {
        return new RecipeGenerator(registryLookup, exporter) {
            @Override
            public void generate() {
                RegistryWrapper.Impl<Item> itemLookup = registries.getOrThrow(RegistryKeys.ITEM);
                createShaped(RecipeCategory.TOOLS, ModItems.BIOME_LOCATOR, 1)
                        .pattern("sps")
                        .pattern("dcd")
                        .pattern("sds")
                        .input('s', ItemTags.SAPLINGS)
                        .input('d', ItemTags.DIRT)
                        .input('p', ItemTags.SMALL_FLOWERS)
                        .input('c', Items.COMPASS)
                        .criterion(hasItem(ModItems.BIOME_LOCATOR), conditionsFromItem(ModItems.BIOME_LOCATOR))
                        .offerTo(exporter);

                createShaped(RecipeCategory.TOOLS, ModItems.STRUCTURE_LOCATOR, 1)
                        .pattern("wmw")
                        .pattern("bcb")
                        .pattern("wbw")
                        .input('m', Items.MAP)
                        .input('w', Items.COBWEB)
                        .input('b', ItemTags.STONE_BRICKS)
                        .input('c', Items.COMPASS)
                        .criterion(hasItem(ModItems.STRUCTURE_LOCATOR), conditionsFromItem(ModItems.STRUCTURE_LOCATOR))
                        .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "NavigationCompassesRecipeProvider";
    }
}
