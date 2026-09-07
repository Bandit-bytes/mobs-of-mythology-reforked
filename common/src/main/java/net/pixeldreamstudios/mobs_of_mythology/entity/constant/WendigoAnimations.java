package net.pixeldreamstudios.mobs_of_mythology.entity.constant;

import mod.azure.azurelib.animation.dispatch.command.AzCommand;
import mod.azure.azurelib.animation.play_behavior.AzPlayBehaviors;
import net.minecraft.world.entity.Entity;

public final class WendigoAnimations {
    public static final String BASE_CONTROLLER = "base_controller";
    public static final String ACTION_CONTROLLER = "attack_controller";

    private static final AzCommand IDLE = AzCommand.create(BASE_CONTROLLER, "idle", AzPlayBehaviors.LOOP);
    private static final AzCommand STALK = AzCommand.create(BASE_CONTROLLER, "walk", AzPlayBehaviors.LOOP);
    private static final AzCommand RUN = AzCommand.create(BASE_CONTROLLER, "run", AzPlayBehaviors.LOOP);
    private static final AzCommand ATTACK = AzCommand.create(ACTION_CONTROLLER, "attack", AzPlayBehaviors.PLAY_ONCE);
    private static final AzCommand REVEAL = AzCommand.create(ACTION_CONTROLLER, "thing", AzPlayBehaviors.PLAY_ONCE);
    private static final AzCommand DEATH = AzCommand.create(ACTION_CONTROLLER, "death", AzPlayBehaviors.PLAY_ONCE);

    private final Entity entity;

    public WendigoAnimations(Entity entity) {
        this.entity = entity;
    }

    public void idle() { IDLE.sendForEntity(entity); }
    public void stalk() { STALK.sendForEntity(entity); }
    public void run() { RUN.sendForEntity(entity); }
    public void attack() { ATTACK.sendForEntity(entity); }
    public void reveal() { REVEAL.sendForEntity(entity); }
    public void death() { DEATH.sendForEntity(entity); }
}
