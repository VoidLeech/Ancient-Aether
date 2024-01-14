package net.builderdog.ancient_aether.data.generators.worldgen.features;

import net.builderdog.ancient_aether.AncientAether;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class AncientAetherConfiguredFeatureData {
    public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> configuredFeature) {
        AncientAetherOreFeatures.bootstrap(configuredFeature);
        AncientAetherMiscFeatures.bootstrap(configuredFeature);
        AncientAetherTreeFeatures.bootstrap(configuredFeature);
        AncientAetherVegetationFeatures.bootstrap(configuredFeature);
    }

    public static ResourceKey<ConfiguredFeature<?, ?>> registerKey(String name) {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(AncientAether.MOD_ID, name));
    }

    static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> key, F feature, FC configuration) {
        context.register(key, new ConfiguredFeature<>(feature, configuration));
    }

    public static void register(BootstapContext<ConfiguredFeature<?, ?>> p_256637_, ResourceKey<ConfiguredFeature<?, ?>> p_256555_, Feature<NoneFeatureConfiguration> p_255921_) {
        register(p_256637_, p_256555_, p_255921_, FeatureConfiguration.NONE);
    }
}