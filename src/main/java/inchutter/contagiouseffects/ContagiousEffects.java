package inchutter.contagiouseffects;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContagiousEffects implements ModInitializer {
	public static final String MOD_ID = "contagious-effects";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		var config = ModConfigLoader.loadOrCreate();
		var cooldowns = new SpreadCooldownTracker(config.cooldownTicks());
		var spreadService = new EffectSpreadService(config, cooldowns);

		LOGGER.info("Contagious Effects initialized.");

		ServerTickEvents.END_SERVER_TICK.register(server -> {
			spreadService.onServerTick(server);
		});
	}
}