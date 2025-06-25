package lol.sylvie.navigation.config;

import com.google.gson.annotations.SerializedName;

public record ConfigState(@SerializedName("biome_locator_enabled") boolean biome, @SerializedName("structure_locator_enabled") boolean structure, @SerializedName("search_range") int range,  @SerializedName("search_cooldown") int cooldown) { }
