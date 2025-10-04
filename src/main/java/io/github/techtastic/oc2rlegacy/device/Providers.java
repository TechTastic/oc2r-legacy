package io.github.techtastic.oc2rlegacy.device;

import io.github.techtastic.oc2rlegacy.OC2RLegacy;
import io.github.techtastic.oc2rlegacy.device.block.transposer.TransposerDeviceProvider;
import li.cil.oc2.api.bus.device.provider.BlockDeviceProvider;
import li.cil.oc2.api.util.Registries;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Providers {
    private static final DeferredRegister<BlockDeviceProvider> BLOCK_PROVIDERS =
            DeferredRegister.create(Registries.BLOCK_DEVICE_PROVIDER, OC2RLegacy.MODID);

    public static final RegistryObject<BlockDeviceProvider> TRANSPOSER =
            BLOCK_PROVIDERS.register("transposer", TransposerDeviceProvider::new);

    public static void register(IEventBus bus) {
        BLOCK_PROVIDERS.register(bus);
    }
}
