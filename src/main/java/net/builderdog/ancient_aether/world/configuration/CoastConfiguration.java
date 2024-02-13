package net.builderdog.ancient_aether.world.configuration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

public record CoastConfiguration(BlockStateProvider block, FloatProvider radius, FloatProvider radius2, UniformInt yRange, HolderSet<Block> validBlocks) implements FeatureConfiguration {
    public static final Codec<CoastConfiguration> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BlockStateProvider.CODEC.fieldOf("block").forGetter(CoastConfiguration::block),
            FloatProvider.CODEC.fieldOf("radius").forGetter(CoastConfiguration::radius),
            FloatProvider.CODEC.fieldOf("radius2").forGetter(CoastConfiguration::radius2),
            UniformInt.CODEC.fieldOf("y_range").forGetter(CoastConfiguration::yRange),
            RegistryCodecs.homogeneousList(Registries.BLOCK).fieldOf("valid_blocks").forGetter(CoastConfiguration::validBlocks)
    ).apply(instance, CoastConfiguration::new));
}