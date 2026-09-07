package net.pixeldreamstudios.mobs_of_mythology.entity.spawn;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;

/**
 * Shared spawn-rule helpers for the mod.
 *
 * <p>Do not use raw SKY light for night spawning. Raw sky light is still 15 under an open
 * sky at midnight. {@link LevelAccessor#getMaxLocalRawBrightness(BlockPos)} applies the
 * world's current sky-darkening value, while still accounting for nearby block light.</p>
 */
public final class MythSpawnRules {
    public static final int DEFAULT_DARKNESS_LIMIT = 7;

    private MythSpawnRules() {
    }

    public static boolean isDarkEnough(LevelAccessor level, BlockPos pos) {
        return isDarkEnough(level, pos, DEFAULT_DARKNESS_LIMIT);
    }

    public static boolean isDarkEnough(LevelAccessor level, BlockPos pos, int maxLight) {
        return level.getMaxLocalRawBrightness(pos) <= maxLight;
    }

    public static <T extends Mob> boolean checkDarkGroundSpawnRules(
            EntityType<T> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getDifficulty() != Difficulty.PEACEFUL
                && Mob.checkMobSpawnRules(entityType, level, spawnType, pos, random)
                && isDarkEnough(level, pos);
    }

    public static boolean isNight(LevelAccessor level) {
        long timeOfDay = Math.floorMod(level.dayTime(), 24000L);
        return timeOfDay >= 13000L && timeOfDay <= 23000L;
    }
}
