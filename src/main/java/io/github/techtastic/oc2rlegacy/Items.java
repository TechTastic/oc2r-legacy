package io.github.techtastic.oc2rlegacy;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class Items {
    private static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(Registries.ITEM, OC2RLegacy.MODID);

    public static final RegistryObject<Item> TRANSPOSER = ITEMS.register("transposer", () ->
            new BlockItem(Blocks.TRANSPOSER.get(), new Item.Properties()));

    public static final RegistryObject<Item> HOLOGRAM_PROJECTOR = ITEMS.register("hologram_projector", () ->
            new BlockItem(Blocks.HOLOGRAM_PROJECTOR.get(), new Item.Properties()));

    public static void register(IEventBus bus) {
        ITEMS.register(bus);
    }
}
