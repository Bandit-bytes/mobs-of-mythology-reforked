package net.pixeldreamstudios.mobs_of_mythology.forge;

import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.pixeldreamstudios.mobs_of_mythology.MobsOfMythology;

@Mod(MobsOfMythology.MOD_ID)
public class MobsOfMythologyForge {
    public MobsOfMythologyForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        EventBuses.registerModEventBus(MobsOfMythology.MOD_ID, modEventBus);
        MobsOfMythology.init();
    }
}
