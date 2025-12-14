package lol.sylvie.navigation.item.impl;

import com.mojang.datafixers.util.Pair;
import lol.sylvie.navigation.config.ConfigHandler;
import lol.sylvie.navigation.gui.LocationGui;
import lol.sylvie.navigation.item.LocatorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.biome.Biome;
import java.util.List;
import java.util.function.Function;

public class BiomeLocatorItem extends LocatorItem {
    public BiomeLocatorItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<LocationGui.Location> generateLocations(ServerLevel world) {
        Registry<Biome> biomeRegistry = world.registryAccess().lookupOrThrow(Registries.BIOME);
        return biomeRegistry.registryKeySet().stream().map(biome -> {
            Identifier biomeId = biome.identifier();
            String biomeName = Component.translatable("biome." + biomeId.getNamespace() + "." + biomeId.getPath()).getString();
            Function<BlockPos, BlockPos> biomeLocator = pos -> {
                Pair<BlockPos, Holder<Biome>> location = world.findClosestBiome3d(e -> e.is(biome), pos, ConfigHandler.STATE.range(), 32, 64);
                if (location == null) return null;
                return location.getFirst();
            };
            return new LocationGui.Location(LocationGui.Location.Type.BIOME, biomeName, biomeLocator);
        }).toList();
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return ConfigHandler.STATE.biome();
    }
}
