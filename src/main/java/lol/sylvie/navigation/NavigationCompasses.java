package lol.sylvie.navigation;

import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import lol.sylvie.navigation.config.ConfigHandler;
import lol.sylvie.navigation.hud.NavigationHandler;
import lol.sylvie.navigation.item.ModItems;
import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NavigationCompasses implements ModInitializer {
	public static final String MOD_ID = "navigation-compasses";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ConfigHandler.initialize();

		ModItems.initialize();
		NavigationHandler.initialize();

		PolymerResourcePackUtils.addModAssets(MOD_ID);

		LOGGER.info("it's like chunkbase but worse");
	}
}