package io.github.techtastic.oc2rlegacy;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Blocks {
    private static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, OC2RLegacy.MODID);

    public static final RegistryObject<Block> TRANSPOSER = BLOCKS.register("transposer", () ->
            new Block(BlockBehaviour.Properties.copy(li.cil.oc2.common.block.Blocks.REDSTONE_INTERFACE.get())));

    public static void register(IEventBus bus) {
        BLOCKS.register(bus);
    }
}
