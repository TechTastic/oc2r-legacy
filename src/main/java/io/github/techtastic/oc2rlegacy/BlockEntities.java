package io.github.techtastic.oc2rlegacy;

import io.github.techtastic.oc2rlegacy.block.entity.HologramProjectorBE;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class BlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, OC2RLegacy.MODID);

    public static final RegistryObject<BlockEntityType<HologramProjectorBE>> HOLOGRAM_PROJECTOR =
            BLOCK_ENTITIES.register("hologram_projector", () -> BlockEntityType.Builder.of(
                    HologramProjectorBE::new,
                    Blocks.HOLOGRAM_PROJECTOR.get()
            ).build(null));

    public static void register(IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }
}
