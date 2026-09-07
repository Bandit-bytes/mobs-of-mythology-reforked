package net.pixeldreamstudios.mobs_of_mythology.entity.mobs;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.LandRandomPos;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.pixeldreamstudios.mobs_of_mythology.MobsOfMythology;
import net.pixeldreamstudios.mobs_of_mythology.entity.constant.WendigoAnimations;
import net.pixeldreamstudios.mobs_of_mythology.entity.spawn.MythSpawnRules;
import net.pixeldreamstudios.mobs_of_mythology.registry.SoundRegistry;
import net.pixeldreamstudios.mobs_of_mythology.registry.TagRegistry;

import java.util.EnumSet;


public class WendigoEntity extends Monster {
    private static final double COMMIT_DISTANCE_SQR = 10.0D * 10.0D;

    public final WendigoAnimations dispatcher = new WendigoAnimations(this);

    private boolean stalking = true;
    private int revealCooldown;
    private int calmTicks;
    private boolean noticeSoundPlayed;
    private int revealRoarDelay = -1;
    private BaseAnim baseAnim = BaseAnim.IDLE;

    private enum BaseAnim { IDLE, STALK, RUN }

    public WendigoEntity(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
        this.xpReward = 35;

        this.setMaxUpStep(1.25F);
        this.navigation.setCanFloat(true);
        this.navigation.setMaxVisitedNodesMultiplier(2.5F);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, MobsOfMythology.config.wendigoHealth)
                .add(Attributes.ATTACK_DAMAGE, MobsOfMythology.config.wendigoAttackDamage)
                .add(Attributes.ARMOR, MobsOfMythology.config.wendigoArmor)
                .add(Attributes.FOLLOW_RANGE, 64.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.34D)
                .add(Attributes.ATTACK_KNOCKBACK, 1.5D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.45D);
    }

    public static boolean checkWendigoSpawnRules(
            EntityType<WendigoEntity> entityType,
            ServerLevelAccessor level,
            MobSpawnType spawnType,
            BlockPos pos,
            RandomSource random
    ) {

        return level.getDifficulty() != Difficulty.PEACEFUL
                && MythSpawnRules.isNight(level)
                && isForestBiome(level, pos)
                && Mob.checkMobSpawnRules(entityType, level, spawnType, pos, random);
    }

    private static boolean isForestBiome(LevelAccessor level, BlockPos pos) {
        return level.getBiome(pos).is(TagRegistry.WENDIGO_BIOMES);
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnType) {
        if (spawnType == MobSpawnType.NATURAL || spawnType == MobSpawnType.CHUNK_GENERATION) {
            BlockPos pos = this.blockPosition();
            return level.getDifficulty() != Difficulty.PEACEFUL
                    && MythSpawnRules.isNight(level)
                    && isForestBiome(level, pos)
                    && super.checkSpawnRules(level, spawnType);
        }

        return super.checkSpawnRules(level, spawnType);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new StalkPlayerGoal());
        this.goalSelector.addGoal(2, new RevealedMeleeAttackGoal());
        this.goalSelector.addGoal(6, new WaterAvoidingRandomStrollGoal(this, 0.8D));
        this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 12.0F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false));
    }

    public boolean isStalking() {
        return stalking;
    }

    private void playNoticeScreech() {
        if (this.level().isClientSide || this.noticeSoundPlayed) {
            return;
        }

        this.noticeSoundPlayed = true;
        // sounds.json contains two screeches under this single event, so Minecraft
        // randomly selects one each time a Wendigo first notices a player. Use the
        // level directly so the sound still plays even though stalking immediately
        // makes the Wendigo itself silent.
        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundRegistry.WENDIGO_NOTICE.get(),
                SoundSource.HOSTILE,
                2.25F,
                0.96F + this.random.nextFloat() * 0.08F
        );
    }


    private void playRevealRoar() {
        if (this.level().isClientSide) {
            return;
        }

        this.level().playSound(
                null,
                this.getX(),
                this.getY(),
                this.getZ(),
                SoundRegistry.WENDIGO_ROAR.get(),
                SoundSource.HOSTILE,
                2.5F,
                1.0F
        );
    }


    private void commitToKill(LivingEntity target, boolean attacked) {
        if (target != null && target.isAlive()) {
            this.setTarget(target);
        }

        boolean wasStalking = this.stalking;
        this.stalking = false;
        this.setSilent(false);
        this.setAggressive(true);

        this.revealCooldown = 0;
        this.navigation.stop();

        if (!this.level().isClientSide && wasStalking && !attacked) {
            this.dispatcher.reveal();

            this.revealRoarDelay = 8;
        }

        if (target != null && target.isAlive()) {
            this.navigation.moveTo(target, attacked ? 1.55D : 1.40D);
        }
    }

    private void resetToStalking() {
        this.stalking = true;
        this.setAggressive(false);
        this.setSilent(false);
        this.revealCooldown = 0;
        this.revealRoarDelay = -1;
        this.calmTicks = 0;
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        boolean hurt = super.hurt(source, amount);
        if (hurt && !this.level().isClientSide && source.getEntity() instanceof LivingEntity attacker) {
            commitToKill(attacker, true);
        }
        return hurt;
    }

    @Override
    public boolean doHurtTarget(Entity entity) {
        if (!this.level().isClientSide) {
            this.dispatcher.attack();
        }

        boolean hit = super.doHurtTarget(entity);
        if (hit && entity instanceof LivingEntity) {

            this.heal(1.0F);
        }
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.level().isClientSide) {
            return;
        }

        if (this.revealCooldown > 0) {
            --this.revealCooldown;
        }

        if (this.revealRoarDelay >= 0 && --this.revealRoarDelay < 0) {
            this.playRevealRoar();
        }

        LivingEntity target = this.getTarget();
        if (target == null || !target.isAlive()) {
            this.setAggressive(false);
            if (!this.stalking && ++this.calmTicks >= 200) {
                resetToStalking();
            }
        } else {
            this.calmTicks = 0;
        }

        boolean moving = this.getDeltaMovement().horizontalDistanceSqr() > 1.0E-4D;
        BaseAnim next = !moving ? BaseAnim.IDLE : (this.stalking && !this.isAggressive() ? BaseAnim.STALK : BaseAnim.RUN);

        if (next != this.baseAnim) {
            this.baseAnim = next;
            switch (next) {
                case STALK -> this.dispatcher.stalk();
                case RUN -> this.dispatcher.run();
                default -> this.dispatcher.idle();
            }
        }
    }

    @Override
    public void die(DamageSource source) {
        if (!this.level().isClientSide) {
            this.dispatcher.death();
        }
        super.die(source);
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("Stalking", this.stalking);
        tag.putInt("RevealCooldown", this.revealCooldown);
        tag.putBoolean("NoticeSoundPlayed", this.noticeSoundPlayed);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("Stalking")) {
            this.stalking = tag.getBoolean("Stalking");
        }
        this.revealCooldown = tag.getInt("RevealCooldown");
        this.noticeSoundPlayed = tag.getBoolean("NoticeSoundPlayed");
        this.setAggressive(!this.stalking);
    }

    @Override
    protected SoundEvent getAmbientSound() {

        return this.stalking ? null : SoundEvents.WITHER_SKELETON_AMBIENT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.WITHER_SKELETON_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.WITHER_SKELETON_DEATH;
    }

    @Override
    protected void playStepSound(BlockPos pos, BlockState state) {
        if (!this.stalking) {
            this.playSound(SoundEvents.WITHER_SKELETON_STEP, 0.45F, 0.65F);
        }
    }

    private boolean playerIsLookingAtMe(Player player) {
        if (!player.hasLineOfSight(this)) {
            return false;
        }

        Vec3 view = player.getViewVector(1.0F).normalize();
        Vec3 toWendigo = this.getEyePosition().subtract(player.getEyePosition()).normalize();
        return view.dot(toWendigo) > 0.80D;
    }

    private final class StalkPlayerGoal extends Goal {
        private int repathDelay;
        private int failedPathTicks;
        private int movementCheckDelay;
        private double lastX;
        private double lastZ;

        private StalkPlayerGoal() {
            this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        }

        @Override
        public boolean canUse() {
            LivingEntity target = WendigoEntity.this.getTarget();
            return WendigoEntity.this.stalking && target instanceof Player && target.isAlive();
        }

        @Override
        public boolean canContinueToUse() {
            return canUse();
        }

        @Override
        public void start() {
            this.repathDelay = 0;
            this.failedPathTicks = 0;
            this.movementCheckDelay = 10;
            this.lastX = WendigoEntity.this.getX();
            this.lastZ = WendigoEntity.this.getZ();
            WendigoEntity.this.playNoticeScreech();
            WendigoEntity.this.setSilent(true);
            WendigoEntity.this.setAggressive(false);
        }

        @Override
        public void stop() {
            if (WendigoEntity.this.stalking) {
                WendigoEntity.this.navigation.stop();
                WendigoEntity.this.setSilent(false);
            }
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public void tick() {
            LivingEntity livingTarget = WendigoEntity.this.getTarget();
            if (!(livingTarget instanceof Player player)) {
                return;
            }

            double distanceSqr = WendigoEntity.this.distanceToSqr(player);

            if (distanceSqr <= COMMIT_DISTANCE_SQR) {
                WendigoEntity.this.commitToKill(player, false);
                return;
            }

            WendigoEntity.this.getLookControl().setLookAt(player, 30.0F, 30.0F);

            if (WendigoEntity.this.playerIsLookingAtMe(player)) {

                WendigoEntity.this.navigation.stop();
                Vec3 velocity = WendigoEntity.this.getDeltaMovement();
                WendigoEntity.this.setDeltaMovement(0.0D, velocity.y, 0.0D);
                WendigoEntity.this.setAggressive(false);
                this.failedPathTicks = 0;
                this.repathDelay = 0;
                this.lastX = WendigoEntity.this.getX();
                this.lastZ = WendigoEntity.this.getZ();
                return;
            }


            WendigoEntity.this.setAggressive(true);

            if (WendigoEntity.this.horizontalCollision && WendigoEntity.this.onGround()) {
                WendigoEntity.this.getJumpControl().jump();
            }

            if (--this.repathDelay <= 0) {
                this.repathDelay = 5 + WendigoEntity.this.random.nextInt(5);
                boolean started = WendigoEntity.this.navigation.moveTo(player, 1.28D);

                if (started) {
                    this.failedPathTicks = Math.max(0, this.failedPathTicks - 8);
                } else {
                    this.failedPathTicks += 8;
                    this.repathDelay = 2;
                }
            }

            if (WendigoEntity.this.navigation.isStuck()) {
                this.failedPathTicks += 2;
            }

            if (--this.movementCheckDelay <= 0) {
                this.movementCheckDelay = 10;
                double dx = WendigoEntity.this.getX() - this.lastX;
                double dz = WendigoEntity.this.getZ() - this.lastZ;
                double movedSqr = dx * dx + dz * dz;

                if (WendigoEntity.this.navigation.isInProgress() && movedSqr < 0.04D) {
                    this.failedPathTicks += 10;
                } else if (movedSqr >= 0.04D) {
                    this.failedPathTicks = Math.max(0, this.failedPathTicks - 10);
                }

                this.lastX = WendigoEntity.this.getX();
                this.lastZ = WendigoEntity.this.getZ();
            }

            if (this.failedPathTicks >= 24) {
                Vec3 recovery = LandRandomPos.getPosTowards(
                        WendigoEntity.this,
                        12,
                        6,
                        player.position()
                );

                WendigoEntity.this.navigation.stop();
                if (recovery != null) {
                    WendigoEntity.this.navigation.moveTo(recovery.x, recovery.y, recovery.z, 1.35D);
                } else {
                    WendigoEntity.this.navigation.moveTo(player, 1.35D);
                }

                this.failedPathTicks = 0;
                this.repathDelay = 2;
            }
        }
    }

    private final class RevealedMeleeAttackGoal extends MeleeAttackGoal {
        private int stuckTicks;
        private int movementCheckDelay;
        private double lastX;
        private double lastZ;

        private RevealedMeleeAttackGoal() {
            super(WendigoEntity.this, 1.30D, true);
        }

        @Override
        public boolean canUse() {
            return !WendigoEntity.this.stalking
                    && WendigoEntity.this.revealCooldown <= 0
                    && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return !WendigoEntity.this.stalking && super.canContinueToUse();
        }

        @Override
        public void start() {
            this.stuckTicks = 0;
            this.movementCheckDelay = 10;
            this.lastX = WendigoEntity.this.getX();
            this.lastZ = WendigoEntity.this.getZ();
            super.start();
        }

        @Override
        public void tick() {
            super.tick();

            LivingEntity target = WendigoEntity.this.getTarget();
            if (target == null) {
                return;
            }

            if (WendigoEntity.this.horizontalCollision && WendigoEntity.this.onGround()) {
                WendigoEntity.this.getJumpControl().jump();
            }

            if (WendigoEntity.this.navigation.isStuck()) {
                this.stuckTicks += 2;
            }

            if (--this.movementCheckDelay <= 0) {
                this.movementCheckDelay = 10;
                double dx = WendigoEntity.this.getX() - this.lastX;
                double dz = WendigoEntity.this.getZ() - this.lastZ;
                double movedSqr = dx * dx + dz * dz;

                if (WendigoEntity.this.navigation.isInProgress()
                        && WendigoEntity.this.distanceToSqr(target) > 9.0D
                        && movedSqr < 0.04D) {
                    this.stuckTicks += 10;
                } else if (movedSqr >= 0.04D) {
                    this.stuckTicks = Math.max(0, this.stuckTicks - 10);
                }

                this.lastX = WendigoEntity.this.getX();
                this.lastZ = WendigoEntity.this.getZ();
            }

            if (this.stuckTicks >= 30 && WendigoEntity.this.distanceToSqr(target) > 9.0D) {
                Vec3 recovery = LandRandomPos.getPosTowards(
                        WendigoEntity.this,
                        10,
                        5,
                        target.position()
                );

                if (recovery != null) {
                    WendigoEntity.this.navigation.stop();
                    WendigoEntity.this.navigation.moveTo(recovery.x, recovery.y, recovery.z, 1.35D);
                } else {
                    WendigoEntity.this.navigation.recomputePath();
                }

                this.stuckTicks = 0;
            }
        }
    }
}
