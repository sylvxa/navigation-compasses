package lol.sylvie.navigation.item.impl;

import com.mojang.datafixers.util.Pair;
import lol.sylvie.navigation.config.ConfigHandler;
import lol.sylvie.navigation.gui.LocationGui;
import lol.sylvie.navigation.item.LocatorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class StructureLocatorItem extends LocatorItem {
    public StructureLocatorItem(Properties settings) {
        super(settings);
    }

    @Override
    protected List<LocationGui.Location> generateLocations(ServerLevel world) {
        Registry<Structure> registry = world.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        return registry.registryKeySet().stream().map(structure -> {
            Identifier structureId = structure.identifier();
            String structureName = String.join(" ", Arrays.stream(structureId.getPath().split("_")).map(StringUtils::capitalize).toList());

            Function<BlockPos, BlockPos> structureLocator = pos -> {
                Pair<BlockPos, Holder<Structure>> location = world.getChunkSource().getGenerator().findNearestMapStructure(world, HolderSet.direct(registry.get(structureId).orElseThrow()), pos, ConfigHandler.STATE.range(), false);
                if (location == null) return null;
                return location.getFirst();
            };
            return new LocationGui.Location(LocationGui.Location.Type.STRUCTURE, structureName, structureLocator);
        }).toList();
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return ConfigHandler.STATE.structure();
    }
}
