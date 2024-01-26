package net.builderdog.ancient_aether;

import com.aetherteam.aether.AetherConfig;
import net.builderdog.ancient_aether.block.AncientAetherBlocks;
import net.builderdog.ancient_aether.blockentity.AncientAetherBlockEntityTypes;
import net.builderdog.ancient_aether.client.AncientAetherSoundEvents;
import net.builderdog.ancient_aether.data.generators.AncientAetherBlockStateData;
import net.builderdog.ancient_aether.data.generators.AncientAetherItemModelData;
import net.builderdog.ancient_aether.data.generators.AncientAetherRecipeData;
import net.builderdog.ancient_aether.data.generators.AncientAetherRegistrySets;
import net.builderdog.ancient_aether.data.providers.AncientAetherLootTableProvider;
import net.builderdog.ancient_aether.entity.AncientAetherEntities;
import net.builderdog.ancient_aether.entity.moa.AncientAetherMoaTypes;
import net.builderdog.ancient_aether.item.AncientAetherItems;
import net.builderdog.ancient_aether.world.biomemodifier.AncientAetherBiomeModifierSerializers;
import net.builderdog.ancient_aether.world.biomes.AncientAetherRegion;
import net.builderdog.ancient_aether.world.biomes.AncientAetherSurfaceData;
import net.builderdog.ancient_aether.world.feature.AncientAetherFeatures;
import net.builderdog.ancient_aether.world.foliageplacer.AncientAetherFoliagePlacers;
import net.builderdog.ancient_aether.world.structure.AncientAetherStructureTypes;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import teamrazor.aeroblender.aether.AetherRuleCategory;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

@Mod(AncientAether.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class AncientAether {
    public static final String MOD_ID = "ancient_aether";
    public static final Path DIRECTORY = FMLPaths.CONFIGDIR.get().resolve(MOD_ID);

    public AncientAether() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::dataSetup);

        DeferredRegister<?>[] registers = {

                AncientAetherItems.ITEMS,
                AncientAetherMoaTypes.MOA_TYPES,
                AncientAetherBlocks.BLOCKS,
                AncientAetherFoliagePlacers.FOLIAGE_PLACERS,
                AncientAetherBlockEntityTypes.BLOCK_ENTITY_TYPES,
                AncientAetherStructureTypes.STRUCTURE_TYPES,
                AncientAetherEntities.ENTITY_TYPES,
                AncientAetherSoundEvents.SOUNDS,
                AncientAetherBiomeModifierSerializers.BIOME_MODIFIER_SERIALIZERS,
                AncientAetherFeatures.FEATURES
                };

        MinecraftForge.EVENT_BUS.register(this);

        DIRECTORY.toFile().mkdirs();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, AncientAetherConfig.COMMON_SPEC);

        AncientAetherBlocks.registerWoodTypes();

        for (DeferredRegister<?> register : registers) {
            register.register(modEventBus);
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            AncientAetherBlocks.registerPots();
            AncientAetherBlocks.registerFlammability();
            registerComposting();
        });

        event.enqueueWork(() -> {
            Regions.register(new AncientAetherRegion(new ResourceLocation(MOD_ID, "ancient_aether"), AncientAetherConfig.COMMON.ancient_aether_biome_weight.get()));

            SurfaceRuleManager.addSurfaceRules(AetherRuleCategory.THE_AETHER, MOD_ID, AncientAetherSurfaceData.makeRules());
        });
    }

    @SubscribeEvent
    public void serverSetup(ServerAboutToStartEvent event) {
        AetherConfig.SERVER.disable_eternal_day.set(true);
    }

    @SubscribeEvent
    public static void addPacks(AddPackFindersEvent event) {
        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            var resourcePath = ModList.get().getModFileById(AncientAether.MOD_ID).getFile().findResource("packs/ancient_aether_texture_tweaks");
            var pack = Pack.readMetaAndCreate("builtin/ancient_aether_texture_tweaks", Component.translatable("pack.ancient_aether.texture_tweaks.title"), true,
                    path -> new PathPackResources(path, resourcePath, true), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }

        if (event.getPackType() == PackType.CLIENT_RESOURCES) {
            var resourcePath = ModList.get().getModFileById(AncientAether.MOD_ID).getFile().findResource("packs/ancient_aether_programmer_art");
            var pack = Pack.readMetaAndCreate("builtin/ancient_aether_programmer_art", Component.translatable("pack.ancient_aether.programmer_art.title"), false,
                    path -> new PathPackResources(path, resourcePath, true), PackType.CLIENT_RESOURCES, Pack.Position.TOP, PackSource.BUILT_IN);
            event.addRepositorySource(consumer -> consumer.accept(pack));
        }

        if (event.getPackType() == PackType.SERVER_DATA) {
            var resourcePath = ModList.get().getModFileById(AncientAether.MOD_ID).getFile().findResource("packs/ancient_aether_worldgen_overrides");
            var pack = Pack.readMetaAndCreate("builtin/ancient_aether_worldgen_overrides", Component.translatable("pack.ancient_aether.worldgen_overrides.title"), true,
                    path -> new PathPackResources(path, resourcePath, true), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.SERVER);

            event.addRepositorySource(consumer -> consumer.accept(pack));
        }

        if(ModList.get().isLoaded("aether_genesis") && event.getPackType() == PackType.SERVER_DATA) {
            if (event.getPackType() == PackType.SERVER_DATA) {
                var resourcePath = ModList.get().getModFileById(AncientAether.MOD_ID).getFile().findResource("packs/aether_genesis_compat");
                var pack = Pack.readMetaAndCreate("builtin/aether_genesis_compat", Component.translatable("pack.ancient_aether.aether_genesis_compat.title"), true,
                        path -> new PathPackResources(path, resourcePath, true), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.SERVER);

                event.addRepositorySource(consumer -> consumer.accept(pack));
            }
        }

        if(ModList.get().isLoaded("lost_aether_content") && event.getPackType() == PackType.SERVER_DATA) {
            if (event.getPackType() == PackType.SERVER_DATA) {
                var resourcePath = ModList.get().getModFileById(AncientAether.MOD_ID).getFile().findResource("packs/lost_content_compat");
                var pack = Pack.readMetaAndCreate("builtin/lost_content_compat", Component.translatable("pack.ancient_aether.lost_content_compat.title"), true,
                        path -> new PathPackResources(path, resourcePath, true), PackType.SERVER_DATA, Pack.Position.TOP, PackSource.SERVER);

                event.addRepositorySource(consumer -> consumer.accept(pack));
            }
        }
    }

    public void dataSetup(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(true, AncientAetherLootTableProvider.create(packOutput));
        generator.addProvider(true, new AncientAetherBlockStateData(packOutput, fileHelper));
        generator.addProvider(true, new AncientAetherRecipeData(packOutput));
        generator.addProvider(true, new AncientAetherItemModelData(packOutput, fileHelper));
        generator.addProvider(event.includeServer(), new AncientAetherRegistrySets(packOutput, lookupProvider));
    }

    private void registerComposting() {
        addCompost(0.3F, AncientAetherBlocks.SKYROOT_PINE_LEAVES.get().asItem());
        addCompost(0.3F, AncientAetherBlocks.CRYSTAL_SKYROOT_LEAVES.get().asItem());
        addCompost(0.3F, AncientAetherBlocks.HIGHSPROOT_LEAVES.get().asItem());
        addCompost(0.3F, AncientAetherBlocks.ENCHANTED_SKYROOT_LEAVES.get().asItem());
        addCompost(0.3F, AncientAetherBlocks.SAKURA_LEAVES.get().asItem());
        addCompost(0.3F, AncientAetherBlocks.HIGHSPROOT_SAPLING.get());
        addCompost(0.3F, AncientAetherBlocks.CRYSTAL_SKYROOT_SAPLING.get());
        addCompost(0.3F, AncientAetherBlocks.SKYROOT_PINE_SAPLING.get());
        addCompost(0.3F, AncientAetherBlocks.SAKURA_SAPLING.get());
        addCompost(0.3F, AncientAetherBlocks.ENCHANTED_SKYROOT_SAPLING.get());
        addCompost(0.5F, AncientAetherItems.GRAPES.get());
        addCompost(0.65F, AncientAetherBlocks.SKY_BLUES.get());
        addCompost(0.65F, AncientAetherBlocks.SAKURA_BLOSSOMS.get());
        addCompost(0.65F, AncientAetherBlocks.WYND_THISTLE.get());
        addCompost(0.65F, AncientAetherBlocks.HIGHLAND_VIOLA.get());
    }

    private void addCompost(float chance, ItemLike item) {
        ComposterBlock.COMPOSTABLES.put(item.asItem(), chance);
    }

    @Mod.EventBusSubscriber
    public static class AncientAetherFuels {
        @SubscribeEvent
        public static void furnaceFuelBurnTimeEvent(FurnaceFuelBurnTimeEvent event) {
            ItemStack itemstack = event.getItemStack();
            if (itemstack.getItem() == AncientAetherBlocks.HIGHSPROOT_PLANKS.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.SAKURA_PLANKS.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.HIGHSPROOT_LOG_WALL.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.HIGHSPROOT_WOOD_WALL.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.STRIPPED_HIGHSPROOT_LOG_WALL.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.STRIPPED_HIGHSPROOT_WOOD_WALL.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.SAKURA_LOG_WALL.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.SAKURA_WOOD_WALL.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.STRIPPED_SAKURA_LOG_WALL.get().asItem())
                event.setBurnTime(300);
            else if (itemstack.getItem() == AncientAetherBlocks.STRIPPED_SAKURA_WOOD_WALL.get().asItem())
                event.setBurnTime(300);
        }
    }
}