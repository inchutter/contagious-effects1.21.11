package inchutter.contagiouseffects;

/**
 * Plain config object (JSON-serialized).
 * All text/keys are intentionally English.
 */
public record ModConfig(
		double rangeBlocks,
		int scanIntervalTicks,
		int cooldownTicks,
		boolean applyIfStrongerOnly,
		boolean ignoreSpectator,
		boolean ignoreCreative
) {
	public static ModConfig defaults() {
		return new ModConfig(
				2.0,   // rangeBlocks
				10,    // scanIntervalTicks
				20,    // cooldownTicks
				true,  // applyIfStrongerOnly
				true,  // ignoreSpectator
				false  // ignoreCreative
		);
	}
}

