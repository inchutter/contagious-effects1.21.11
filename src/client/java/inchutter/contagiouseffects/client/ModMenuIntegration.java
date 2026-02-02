package inchutter.contagiouseffects.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import inchutter.contagiouseffects.ModConfig;
import inchutter.contagiouseffects.ModConfigLoader;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;
import java.util.concurrent.atomic.AtomicReference;

public class ModMenuIntegration implements ModMenuApi {
	@Override
	public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return parent -> {
			final AtomicReference<ModConfig> current = new AtomicReference<>(ModConfigLoader.loadOrCreate());

			ConfigBuilder builder = ConfigBuilder.create()
					.setParentScreen(parent)
					.setTitle(Component.literal("Contagious Effects Config"));

			builder.setSavingRunnable(() -> ModConfigLoader.save(current.get()));

			ConfigEntryBuilder entryBuilder = builder.entryBuilder();
			ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));

			// rangeBlocks
			general.addEntry(entryBuilder
					.startDoubleField(Component.literal("Range (blocks)"), current.get().rangeBlocks())
					.setDefaultValue(ModConfig.defaults().rangeBlocks())
					.setTooltip(Component.literal("Maximum distance in blocks where effects can spread."))
					.setSaveConsumer(value -> {
						current.set(new ModConfig(
								value,
								current.get().scanIntervalTicks(),
								current.get().cooldownTicks(),
								current.get().applyIfStrongerOnly(),
								current.get().ignoreSpectator(),
								current.get().ignoreCreative()
						));
					})
					.build());

			// scanIntervalTicks
			general.addEntry(entryBuilder
					.startIntField(Component.literal("Scan interval (ticks)"), current.get().scanIntervalTicks())
					.setDefaultValue(ModConfig.defaults().scanIntervalTicks())
					.setTooltip(Component.literal("How often to check for nearby players and spread effects."))
					.setMin(1)
					.setMax(200)
					.setSaveConsumer(value -> {
						current.set(new ModConfig(
								current.get().rangeBlocks(),
								value,
								current.get().cooldownTicks(),
								current.get().applyIfStrongerOnly(),
								current.get().ignoreSpectator(),
								current.get().ignoreCreative()
						));
					})
					.build());

			// cooldownTicks
			general.addEntry(entryBuilder
					.startIntField(Component.literal("Cooldown (ticks)"), current.get().cooldownTicks())
					.setDefaultValue(ModConfig.defaults().cooldownTicks())
					.setTooltip(Component.literal("Delay before the same effect can spread again between two players."))
					.setMin(0)
					.setMax(1200)
					.setSaveConsumer(value -> {
						current.set(new ModConfig(
								current.get().rangeBlocks(),
								current.get().scanIntervalTicks(),
								value,
								current.get().applyIfStrongerOnly(),
								current.get().ignoreSpectator(),
								current.get().ignoreCreative()
						));
					})
					.build());

			// applyIfStrongerOnly
			general.addEntry(entryBuilder
					.startBooleanToggle(Component.literal("Only overwrite if stronger"), current.get().applyIfStrongerOnly())
					.setDefaultValue(ModConfig.defaults().applyIfStrongerOnly())
					.setTooltip(Component.literal("If enabled, existing effects are only replaced by stronger/longer ones."))
					.setSaveConsumer(value -> {
						current.set(new ModConfig(
								current.get().rangeBlocks(),
								current.get().scanIntervalTicks(),
								current.get().cooldownTicks(),
								value,
								current.get().ignoreSpectator(),
								current.get().ignoreCreative()
						));
					})
					.build());

			// ignoreSpectator
			general.addEntry(entryBuilder
					.startBooleanToggle(Component.literal("Ignore spectators"), current.get().ignoreSpectator())
					.setDefaultValue(ModConfig.defaults().ignoreSpectator())
					.setTooltip(Component.literal("If enabled, spectators do not spread or receive effects."))
					.setSaveConsumer(value -> {
						current.set(new ModConfig(
								current.get().rangeBlocks(),
								current.get().scanIntervalTicks(),
								current.get().cooldownTicks(),
								current.get().applyIfStrongerOnly(),
								value,
								current.get().ignoreCreative()
						));
					})
					.build());

			// ignoreCreative
			general.addEntry(entryBuilder
					.startBooleanToggle(Component.literal("Ignore creative players"), current.get().ignoreCreative())
					.setDefaultValue(ModConfig.defaults().ignoreCreative())
					.setTooltip(Component.literal("If enabled, creative players do not spread or receive effects."))
					.setSaveConsumer(value -> {
						current.set(new ModConfig(
								current.get().rangeBlocks(),
								current.get().scanIntervalTicks(),
								current.get().cooldownTicks(),
								current.get().applyIfStrongerOnly(),
								current.get().ignoreSpectator(),
								value
						));
					})
					.build());

			return builder.build();
		};
	}
}

