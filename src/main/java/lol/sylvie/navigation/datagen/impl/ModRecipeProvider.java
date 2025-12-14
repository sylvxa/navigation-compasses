package lol.sylvie.navigation.datagen.impl;

import lol.sylvie.navigation.item.ModItems;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput exporter) {
        return new RecipeProvider(registryLookup, exporter) {
            @Override
            public void buildRecipes() {
                HolderLookup.RegistryLookup<Item> itemLookup = registries.lookupOrThrow(Registries.ITEM);
                shaped(RecipeCategory.TOOLS, ModItems.BIOME_LOCATOR, 1)
                        .pattern("sps")
                        .pattern("dcd")
                        .pattern("sds")
                        .define('s', ItemTags.SAPLINGS)
                        .define('d', ItemTags.DIRT)
                        .define('p', ItemTags.SMALL_FLOWERS)
                        .define('c', Items.COMPASS)
                        .unlockedBy(getHasName(ModItems.BIOME_LOCATOR), has(ModItems.BIOME_LOCATOR))
                        .save(output);

                shaped(RecipeCategory.TOOLS, ModItems.STRUCTURE_LOCATOR, 1)
                        .pattern("wmw")
                        .pattern("bcb")
                        .pattern("wbw")
                        .define('m', Items.MAP)
                        .define('w', Items.COBWEB)
                        .define('b', ItemTags.STONE_BRICKS)
                        .define('c', Items.COMPASS)
                        .unlockedBy(getHasName(ModItems.STRUCTURE_LOCATOR), has(ModItems.STRUCTURE_LOCATOR))
                        .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return "NavigationCompassesRecipeProvider";
    }
}
