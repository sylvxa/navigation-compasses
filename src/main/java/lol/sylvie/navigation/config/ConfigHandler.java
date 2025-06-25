package lol.sylvie.navigation.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import lol.sylvie.navigation.NavigationCompasses;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;

public class ConfigHandler {
	private static final File FILE = FabricLoader.getInstance().getConfigDir().resolve(NavigationCompasses.MOD_ID + ".json").toFile();
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	public static ConfigState STATE = new ConfigState(true, true, 5000, 20 * 30);

	public static void write() {
		try (FileWriter writer = new FileWriter(FILE)) {
			GSON.toJson(STATE, writer);
		} catch (IOException e) {
			NavigationCompasses.LOGGER.error("Couldn't write config file", e);
		}
	}

	public static void read() {
		if (!FILE.exists()) {
			write();
			return;
		}

		try (FileReader reader = new FileReader(FILE)) {
			STATE = GSON.fromJson(reader, ConfigState.class);
		} catch (IOException | JsonParseException e) {
			NavigationCompasses.LOGGER.error("Couldn't load config file", e);
			write();
		}
	}

	public static void initialize() {
		read();
		// If I add a new config field this adds it to the config file
		Runtime.getRuntime().addShutdownHook(new Thread(ConfigHandler::write));
	}
}
