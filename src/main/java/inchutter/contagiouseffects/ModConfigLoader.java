package inchutter.contagiouseffects;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ModConfigLoader {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final String FILE_NAME = "contagious-effects.json";

	private ModConfigLoader() {}

	public static ModConfig loadOrCreate() {
		Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);

		if (!Files.exists(configPath)) {
			ModConfig defaults = ModConfig.defaults();
			write(configPath, defaults);
			return defaults;
		}

		try {
			String json = Files.readString(configPath, StandardCharsets.UTF_8);
			ModConfig cfg = GSON.fromJson(json, ModConfig.class);
			if (cfg == null) {
				ContagiousEffects.LOGGER.warn("Config file was empty/invalid JSON, using defaults.");
				return ModConfig.defaults();
			}
			return sanitize(cfg);
		} catch (Exception e) {
			ContagiousEffects.LOGGER.warn("Failed to read config, using defaults.", e);
			return ModConfig.defaults();
		}
	}

	private static ModConfig sanitize(ModConfig cfg) {
		double range = Double.isFinite(cfg.rangeBlocks()) ? cfg.rangeBlocks() : ModConfig.defaults().rangeBlocks();
		if (range < 0.1) range = 0.1;
		if (range > 64.0) range = 64.0;

		int interval = cfg.scanIntervalTicks();
		if (interval < 1) interval = 1;
		if (interval > 200) interval = 200;

		int cooldown = cfg.cooldownTicks();
		if (cooldown < 0) cooldown = 0;
		if (cooldown > 1200) cooldown = 1200;

		return new ModConfig(
				range,
				interval,
				cooldown,
				cfg.applyIfStrongerOnly(),
				cfg.ignoreSpectator(),
				cfg.ignoreCreative()
		);
	}

	public static void save(ModConfig cfg) {
		Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
		write(configPath, cfg);
	}

	private static void write(Path path, ModConfig cfg) {
		try {
			Files.createDirectories(path.getParent());
			Files.writeString(path, GSON.toJson(cfg), StandardCharsets.UTF_8);
		} catch (IOException e) {
			ContagiousEffects.LOGGER.warn("Failed to write default config to disk.", e);
		}
	}
}

