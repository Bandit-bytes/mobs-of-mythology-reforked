package net.pixeldreamstudios.mobs_of_mythology.entity.client.animator;

import mod.azure.azurelib.animation.AzAnimatorConfig;
import mod.azure.azurelib.animation.controller.AzAnimationController;
import mod.azure.azurelib.animation.controller.AzAnimationControllerContainer;
import mod.azure.azurelib.animation.impl.AzEntityAnimator;
import net.minecraft.resources.ResourceLocation;
import net.pixeldreamstudios.mobs_of_mythology.MobsOfMythology;
import net.pixeldreamstudios.mobs_of_mythology.entity.mobs.WendigoEntity;

public class WendigoAnimator extends AzEntityAnimator<WendigoEntity> {
    private static final ResourceLocation ANIMATIONS =
            MobsOfMythology.modResource("animations/entity/wendigo.animation.json");

    public WendigoAnimator() {
        super(AzAnimatorConfig.defaultConfig());
    }

    @Override
    public void registerControllers(AzAnimationControllerContainer<WendigoEntity> controllers) {
        controllers.add(AzAnimationController.builder(this, "base_controller").build());
        controllers.add(
                AzAnimationController.builder(this, "attack_controller")
                        .setTransitionLength(3)
                        .build()
        );
    }

    @Override
    public ResourceLocation getAnimationLocation(WendigoEntity animatable) {
        return ANIMATIONS;
    }
}
