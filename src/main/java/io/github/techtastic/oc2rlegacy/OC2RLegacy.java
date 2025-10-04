package io.github.techtastic.oc2rlegacy;

import com.mojang.logging.LogUtils;
import io.github.techtastic.oc2rlegacy.device.Providers;
import io.github.techtastic.oc2rlegacy.device.block.holographic_projector.HologramFrambufferDeviceProvider;
import io.github.techtastic.oc2rlegacy.device.block.holographic_projector.HologramFramebufferDevice;
import li.cil.sedna.api.device.Device;
import li.cil.sedna.api.devicetree.DeviceTree;
import li.cil.sedna.api.devicetree.DeviceTreeProvider;
import li.cil.sedna.api.memory.MemoryMap;
import li.cil.sedna.devicetree.DeviceTreeRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.Optional;

@Mod(OC2RLegacy.MODID)
public class OC2RLegacy {
    public static final String MODID = "oc2rlegacy";
    private static final Logger LOGGER = LogUtils.getLogger();

    public OC2RLegacy(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        MinecraftForge.EVENT_BUS.register(this);

        BlockEntities.register(modEventBus);
        Blocks.register(modEventBus);
        Items.register(modEventBus);
        Providers.register(modEventBus);

        DeviceTreeRegistry.putProvider(HologramFramebufferDevice.class, new HologramFrambufferDeviceProvider());
    }
}
