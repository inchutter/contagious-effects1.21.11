package inchutter.contagiouseffects;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Cooldown per (source, target, effect) to avoid constant re-application.
 */
public final class SpreadCooldownTracker {
	private final int cooldownTicks;
	private final Map<Key, Long> lastAppliedTickByKey = new HashMap<>();

	public SpreadCooldownTracker(int cooldownTicks) {
		this.cooldownTicks = Math.max(0, cooldownTicks);
	}

	public boolean canApply(UUID source, UUID target, Holder<MobEffect> effect, long currentTick) {
		if (cooldownTicks <= 0) return true;
		int effectId = BuiltInRegistries.MOB_EFFECT.getId(effect.value());
		if (effectId < 0) return true;

		Long last = lastAppliedTickByKey.get(new Key(source, target, effectId));
		return last == null || (currentTick - last) >= cooldownTicks;
	}

	public void markApplied(UUID source, UUID target, Holder<MobEffect> effect, long currentTick) {
		int effectId = BuiltInRegistries.MOB_EFFECT.getId(effect.value());
		if (effectId < 0) return;
		lastAppliedTickByKey.put(new Key(source, target, effectId), currentTick);
	}

	public void cleanup(long currentTick) {
		if (cooldownTicks <= 0) return;

		long expireAfter = Math.max(200L, (long) cooldownTicks * 5L);
		Iterator<Map.Entry<Key, Long>> it = lastAppliedTickByKey.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Key, Long> e = it.next();
			if ((currentTick - e.getValue()) > expireAfter) {
				it.remove();
			}
		}
	}

	private record Key(UUID source, UUID target, int effectId) {}
}

