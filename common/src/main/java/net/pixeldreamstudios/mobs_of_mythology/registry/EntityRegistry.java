package net.pixeldreamstudios.mobs_of_mythology.registry;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.architectury.registry.level.entity.EntityAttributeRegistry;
import dev.architectury.registry.level.entity.SpawnPlacementsRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.pixeldreamstudios.mobs_of_mythology.MobsOfMythology;
import net.pixeldreamstudios.mobs_of_mythology.entity.mobs.*;
import net.pixeldreamstudios.mobs_of_mythology.entity.spawn.MythSpawnRules;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(MobsOfMythology.MOD_ID, Registries.ENTITY_TYPE);

    public static final RegistrySupplier<EntityType<AutomatonEntity>> AUTOMATON = ENTITIES.register("automaton", () ->
            EntityType.Builder.of(AutomatonEntity::new, MobCategory.CREATURE)
                    .sized(1.5f, 2.9f)
                    .build(new ResourceLocation(MobsOfMythology.MOD_ID, "automaton").toString()));

    public static final RegistrySupplier<EntityType<ChupacabraEntity>> CHUPACABRA = ENTITIES.register("chupacabra", () ->
            EntityType.Builder.of(ChupacabraEntity::new, MobCategory.MONSTER)
                    .sized(1.25f, 1.0f)
                    .build(new ResourceLocation(MobsOfMythology.MOD_ID, "chupacabra").toString()));

    public static final RegistrySupplier<EntityType<KoboldEntity>> KOBOLD = ENTITIES.register("kobold", () ->
            EntityType.Builder.of(KoboldEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 1.75f)
                    .build(new ResourceLocation(MobsOfMythology.MOD_ID, "kobold").toString()));

    public static final RegistrySupplier<EntityType<KoboldWarriorEntity>> KOBOLD_WARRIOR = ENTITIES.register("kobold_warrior", () ->
            EntityType.Builder.of(KoboldWarriorEntity::new, MobCategory.MONSTER)
                    .sized(0.75f, 1.75f)
                    .build(new ResourceLocation(MobsOfMythology.MOD_ID, "kobold_warrior").toString()));

    public static final RegistrySupplier<EntityType<DrakeEntity>> DRAKE = ENTITIES.register("drake", () ->
            EntityType.Builder.of(DrakeEntity::new, MobCategory.MONSTER)
                    .sized(1.25f, 1.0f)
                    .build(new ResourceLocation(MobsOfMythology.MOD_ID, "drake").toString()));

    public static final RegistrySupplier<EntityType<SporelingEntity>> SPORELING = ENTITIES.register("sporeling", () ->
            EntityType.Builder.of(SporelingEntity::new, MobCategory.CREATURE)
                    .sized(1.0f, 0.8f)
                    .build(new ResourceLocation(MobsOfMythology.MOD_ID, "sporeling").toString()));

    public static final RegistrySupplier<EntityType<WendigoEntity>> WENDIGO = ENTITIES.register("wendigo", () ->
            EntityType.Builder.of(WendigoEntity::new, MobCategory.MONSTER)
                    .sized(2.2f, 5.0f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(MobsOfMythology.MOD_ID, "wendigo").toString()));

    private static void initSpawns() {
        SpawnPlacementsRegistry.register(EntityRegistry.KOBOLD, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythSpawnRules::checkDarkGroundSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.KOBOLD_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(KOBOLD.get(), MobsOfMythology.config.koboldSpawnWeight, 2, 4)));

        SpawnPlacementsRegistry.register(EntityRegistry.KOBOLD_WARRIOR, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythSpawnRules::checkDarkGroundSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.KOBOLD_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(KOBOLD_WARRIOR.get(), MobsOfMythology.config.koboldWarriorSpawnWeight, 2, 3)));

        SpawnPlacementsRegistry.register(EntityRegistry.DRAKE, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythSpawnRules::checkDarkGroundSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.DRAKE_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(DRAKE.get(), MobsOfMythology.config.drakeSpawnWeight, 1, 1)));

        SpawnPlacementsRegistry.register(EntityRegistry.CHUPACABRA, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythSpawnRules::checkDarkGroundSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.CHUPACABRA_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(CHUPACABRA.get(), MobsOfMythology.config.chupacabraSpawnWeight, 1, 1)));

        SpawnPlacementsRegistry.register(EntityRegistry.SPORELING, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, MythSpawnRules::checkDarkGroundSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.SPORELING_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.CREATURE, new MobSpawnSettings.SpawnerData(SPORELING.get(), MobsOfMythology.config.sporelingSpawnWeight, 4, 6)));

        SpawnPlacementsRegistry.register(EntityRegistry.WENDIGO, SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WendigoEntity::checkWendigoSpawnRules);
        BiomeModifications.addProperties(b -> b.hasTag(TagRegistry.WENDIGO_BIOMES), (ctx, b) -> b.getSpawnProperties().addSpawn(MobCategory.MONSTER, new MobSpawnSettings.SpawnerData(WENDIGO.get(), MobsOfMythology.config.wendigoSpawnWeight, 1, 1)));
    }

    private static void initAttributes() {
        EntityAttributeRegistry.register(AUTOMATON, AutomatonEntity::createAttributes);
        EntityAttributeRegistry.register(CHUPACABRA, ChupacabraEntity::createAttributes);
        EntityAttributeRegistry.register(KOBOLD, KoboldEntity::createAttributes);
        EntityAttributeRegistry.register(KOBOLD_WARRIOR, KoboldWarriorEntity::createAttributes);
        EntityAttributeRegistry.register(DRAKE, DrakeEntity::createAttributes);
        EntityAttributeRegistry.register(SPORELING, SporelingEntity::createAttributes);
        EntityAttributeRegistry.register(WENDIGO, WendigoEntity::createAttributes);
    }

    public static void init() {
        ENTITIES.register();
        initAttributes();
        initSpawns();
    }

}
