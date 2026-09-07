package net.pixeldreamstudios.mobs_of_mythology.entity.client.renderer;

import mod.azure.azurelib.render.entity.AzEntityRenderer;
import mod.azure.azurelib.render.entity.AzEntityRendererConfig;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.pixeldreamstudios.mobs_of_mythology.MobsOfMythology;
import net.pixeldreamstudios.mobs_of_mythology.entity.client.animator.WendigoAnimator;
import net.pixeldreamstudios.mobs_of_mythology.entity.mobs.WendigoEntity;

public class WendigoRenderer extends AzEntityRenderer<WendigoEntity> {
    private static final ResourceLocation MODEL =
            MobsOfMythology.modResource("geo/entity/wendigo.geo.json");
    private static final ResourceLocation TEXTURE =
            MobsOfMythology.modResource("textures/entity/wendigo.png");

    public WendigoRenderer(EntityRendererProvider.Context context) {
        super(
                AzEntityRendererConfig.<WendigoEntity>builder(MODEL, TEXTURE)
                        .setAnimatorProvider(WendigoAnimator::new)
                        .setShadowRadius(0.9F)
                        .build(),
                context
        );
    }

    @Override
    public ResourceLocation getTextureLocation(WendigoEntity entity) {
        return new ResourceLocation(MobsOfMythology.MOD_ID, "textures/entity/wendigo.png");
    }
}
