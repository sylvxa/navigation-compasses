package lol.sylvie.navigation.item.impl;

import com.mojang.datafixers.util.Pair;
import lol.sylvie.navigation.config.ConfigHandler;
import lol.sylvie.navigation.gui.LocationGui;
import lol.sylvie.navigation.item.LocatorItem;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.Structure;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StructureLocatorItem extends LocatorItem {
    public StructureLocatorItem(Settings settings) {
        super(settings);
    }

    @Override
    protected List<LocationGui.Location> generateLocations(ServerWorld world) {
        Registry<Structure> registry = world.getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
        return registry.getKeys().stream().map(structure -> {
            Identifier structureId = structure.getValue();
            String structureName = String.join(" ", Arrays.stream(structureId.getPath().split("_")).map(StringUtils::capitalize).toList());

            Function<BlockPos, BlockPos> structureLocator = pos -> {
                Pair<BlockPos, RegistryEntry<Structure>> location = world.getChunkManager().getChunkGenerator().locateStructure(world, RegistryEntryList.of(registry.getEntry(structureId).orElseThrow()), pos, ConfigHandler.STATE.range(), false);
                if (location == null) return null;
                return location.getFirst();
            };
            return new LocationGui.Location(LocationGui.Location.Type.STRUCTURE, structureName, structureLocator);
        }).toList();
    }

    @Override
    public boolean isEnabled(FeatureSet enabledFeatures) {
        return ConfigHandler.STATE.structure();
    }
}
