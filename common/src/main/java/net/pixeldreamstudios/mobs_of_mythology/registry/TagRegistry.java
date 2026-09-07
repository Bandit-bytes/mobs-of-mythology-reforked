package net.pixeldreamstudios.mobs_of_mythology.registry;

import dev.architectury.platform.Platform;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public record TagRegistry() {
    // BLOCK TAGS
    public static final TagKey<Block> BRONZE_BLOCKS = TagKey.create(Registries.BLOCK, Platform.isForge() ? new ResourceLocation("forge", "storage_blocks/bronze") : new ResourceLocation("c", "bronze_blocks"));

    // ITEM TAGS
    public static final TagKey<Item> PICKAXES = TagKey.create(Registries.ITEM, Platform.isForge() ? new ResourceLocation("forge", "pickaxes") : new ResourceLocation("c", "pickaxes"));
    public static final TagKey<Item> BRONZE_INGOTS = TagKey.create(Registries.ITEM, Platform.isForge() ? new ResourceLocation("forge", "ingots/bronze") : new ResourceLocation("c", "bronze_ingots"));

    // MOB BIOME SPAWN TAGS
    // These are mod-owned tags so Forge and Fabric use the exact same spawn selectors.
    // The JSON tag files bridge each loader's convention tags and can be extended by datapacks.
    public static final TagKey<Biome> KOBOLD_BIOMES = biomeTag("kobolds_spawn_in");
    public static final TagKey<Biome> DRAKE_BIOMES = biomeTag("drakes_spawn_in");
    public static final TagKey<Biome> CHUPACABRA_BIOMES = biomeTag("chupacabras_spawn_in");
    public static final TagKey<Biome> SPORELING_BIOMES = biomeTag("sporelings_spawn_in");
    public static final TagKey<Biome> WENDIGO_BIOMES = biomeTag("wendigos_spawn_in");

    private static TagKey<Biome> biomeTag(String path) {
        return TagKey.create(Registries.BIOME, new ResourceLocation("mobs_of_mythology", path));
    }
}
