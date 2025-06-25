package lol.sylvie.navigation.item.impl;

import com.mojang.datafixers.util.Pair;
import lol.sylvie.navigation.config.ConfigHandler;
import lol.sylvie.navigation.gui.LocationGui;
import lol.sylvie.navigation.item.LocatorItem;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.function.Function;

public class BiomeLocatorItem extends LocatorItem {
    public BiomeLocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<LocationGui.Location> generateLocations(ServerWorld world) {
        Registry<Biome> biomeRegistry = world.getRegistryManager().getOrThrow(RegistryKeys.BIOME);
        return biomeRegistry.getKeys().stream().map(biome -> {
            Identifier biomeId = biome.getValue();
            String biomeName = Text.translatable("biome." + biomeId.getNamespace() + "." + biomeId.getPath()).getString();
            Function<BlockPos, BlockPos> biomeLocator = pos -> {
                Pair<BlockPos, RegistryEntry<Biome>> location = world.locateBiome(e -> e.matchesKey(biome), pos, ConfigHandler.STATE.range(), 32, 64);
                if (location == null) return null;
                return location.getFirst();
            };
            return new LocationGui.Location(LocationGui.Location.Type.BIOME, biomeName, biomeLocator);
        }).toList();
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return ConfigHandler.STATE.biome();
    }
}
