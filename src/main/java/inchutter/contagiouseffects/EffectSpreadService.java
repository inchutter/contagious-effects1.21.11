package inchutter.contagiouseffects;

import net.minecraft.server.MinecraftServer;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class EffectSpreadService {
	private final ModConfig config;
	private final SpreadCooldownTracker cooldowns;

	private long tickCounter = 0;

	public EffectSpreadService(ModConfig config, SpreadCooldownTracker cooldowns) {
		this.config = config;
		this.cooldowns = cooldowns;
	}

	public void onServerTick(MinecraftServer server) {
		tickCounter++;

		int interval = Math.max(1, config.scanIntervalTicks());
		if ((tickCounter % interval) != 0) {
			return;
		}

		List<ServerPlayer> players = server.getPlayerList().getPlayers();
		if (players.size() < 2) return;

		double range = Math.max(0.1, config.rangeBlocks());
		double rangeSq = range * range;

		for (ServerPlayer source : players) {
			if (!isEligible(source)) continue;

			Collection<MobEffectInstance> sourceEffects = source.getActiveEffects();
			if (sourceEffects.isEmpty()) continue;

			// Copy into a stable list to avoid concurrent modification edge cases.
			List<MobEffectInstance> effectsSnapshot = new ArrayList<>(sourceEffects);

			for (ServerPlayer target : players) {
				if (target == source) continue;
				if (target.level() != source.level()) continue;
				if (!isEligible(target)) continue;
				if (source.distanceToSqr(target) > rangeSq) continue;

				spreadEffects(source, target, effectsSnapshot);
			}
		}

		// Keep cooldown map from growing forever.
		cooldowns.cleanup(tickCounter);
	}

	private boolean isEligible(ServerPlayer player) {
		if (!player.isAlive()) return false;
		if (config.ignoreSpectator() && player.isSpectator()) return false;
		if (config.ignoreCreative() && player.isCreative()) return false;
		return true;
	}

	private void spreadEffects(ServerPlayer source, ServerPlayer target, List<MobEffectInstance> effects) {
		for (MobEffectInstance sourceInst : effects) {
			Holder<MobEffect> effect = sourceInst.getEffect();

			if (!cooldowns.canApply(source.getUUID(), target.getUUID(), effect, tickCounter)) {
				continue;
			}

			MobEffectInstance targetInst = target.getEffect(effect);

			if (config.applyIfStrongerOnly()) {
				if (!shouldOverride(targetInst, sourceInst)) {
					continue;
				}
			}

			MobEffectInstance copy = copyInstance(sourceInst);
			boolean applied = target.addEffect(copy, source);
			if (applied) {
				cooldowns.markApplied(source.getUUID(), target.getUUID(), effect, tickCounter);
			}
		}
	}

	private static boolean shouldOverride(MobEffectInstance current, MobEffectInstance incoming) {
		if (current == null) return true;

		int curAmp = current.getAmplifier();
		int inAmp = incoming.getAmplifier();
		if (inAmp > curAmp) return true;
		if (inAmp < curAmp) return false;

		// Same amplifier: only override if incoming is meaningfully longer.
		return incoming.getDuration() > (current.getDuration() + 20);
	}

	private static MobEffectInstance copyInstance(MobEffectInstance src) {
		// Keep the remaining duration from source at copy time.
		return new MobEffectInstance(
				src.getEffect(),
				src.getDuration(),
				src.getAmplifier(),
				src.isAmbient(),
				src.isVisible(),
				src.showIcon()
		);
	}
}

