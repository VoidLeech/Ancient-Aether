package net.builderdog.ancient_aether.data.resources.registries;

import net.builderdog.ancient_aether.AncientAether;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

public class AncientAetherStructures {
    public static final ResourceKey<Structure> HOLYSTONE_RUIN = createKey("holystone_ruin");
    public static final ResourceKey<Structure> MYSTERIOUS_HENGE = createKey("mysterious_henge");
    public static final ResourceKey<Structure> VALKYRIE_CAMP_SKYROOT = createKey("valkyrie_camp_skyroot");
    public static final ResourceKey<Structure> VALKYRIE_CAMP_WYNDCAPS = createKey("valkyrie_camp_wyndcaps");
    public static final ResourceKey<Structure> VALKYRIE_SETTLEMENT_SKYROOT = createKey("valkyrie_settlement_skyroot");
    public static final ResourceKey<Structure> BRONZE_DUNGEON_WYNDCAPS = createKey("bronze_dungeon_wyndcaps");
    public static final ResourceKey<Structure> ANCIENT_DUNGEON = createKey("ancient_dungeon");
    public static final ResourceKey<Structure> SENTRY_LABORATORY = createKey("sentry_laboratory");

    private static ResourceKey<Structure> createKey(String name) {
        return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(AncientAether.MOD_ID, name));
    }
}