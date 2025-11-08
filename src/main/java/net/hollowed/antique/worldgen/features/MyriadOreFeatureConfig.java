package net.hollowed.antique.worldgen.features;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.structure.rule.RuleTest;
import net.minecraft.world.gen.feature.FeatureConfig;

import java.util.List;

public record MyriadOreFeatureConfig(List<Target> targets, int size, float discardOnAirChance) implements FeatureConfig {

	public static final Codec<MyriadOreFeatureConfig> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
							Codec.list(Target.CODEC).fieldOf("targets").forGetter(config -> config.targets),
							Codec.intRange(0, 64).fieldOf("size").forGetter(config -> config.size),
							Codec.floatRange(0.0F, 1.0F).fieldOf("discard_chance_on_air_exposure").forGetter(config -> config.discardOnAirChance)
					)
					.apply(instance, MyriadOreFeatureConfig::new)
	);

	public static class Target {
		public static final Codec<Target> CODEC = RecordCodecBuilder.create(
				instance -> instance.group(
								RuleTest.TYPE_CODEC.fieldOf("target").forGetter(target -> target.target),
								BlockState.CODEC.fieldOf("state").forGetter(target -> target.state),
								RuleTest.TYPE_CODEC.fieldOf("cluster_target").forGetter(target -> target.clusterTarget),
								BlockState.CODEC.fieldOf("cluster_state").forGetter(target -> target.clusterState)
						)
						.apply(instance, Target::new)
		);
		public final RuleTest target;
		public final BlockState state;

		public final RuleTest clusterTarget;
		public final BlockState clusterState;

		Target(RuleTest target, BlockState state, RuleTest clusterTarget, BlockState clusterState) {
			this.target = target;
			this.state = state;

			this.clusterTarget = clusterTarget;
			this.clusterState = clusterState;
		}
	}
}
