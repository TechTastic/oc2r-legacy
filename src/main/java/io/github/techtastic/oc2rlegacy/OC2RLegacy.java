package io.github.techtastic.oc2rlegacy;

import com.mojang.logging.LogUtils;
import io.github.techtastic.oc2rlegacy.device.Providers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

@Mod(OC2RLegacy.MODID)
public class OC2RLegacy {
    public static final String MODID = "oc2rlegacy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public OC2RLegacy(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        Blocks.register(modEventBus);
        Items.register(modEventBus);
        Providers.register(modEventBus);
    }
}
