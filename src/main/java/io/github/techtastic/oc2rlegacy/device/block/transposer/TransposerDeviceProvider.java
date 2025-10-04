package io.github.techtastic.oc2rlegacy.device.block.transposer;

import io.github.techtastic.oc2rlegacy.Blocks;
import li.cil.oc2.api.bus.device.Device;
import li.cil.oc2.api.bus.device.provider.BlockDeviceQuery;
import li.cil.oc2.api.util.Invalidatable;
import li.cil.oc2.common.bus.device.provider.util.AbstractBlockDeviceProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TransposerDeviceProvider extends AbstractBlockDeviceProvider {
    @Override
    public @NotNull Invalidatable<Device> getDevice(@NotNull BlockDeviceQuery query) {
        BlockState state = query.getLevel().getBlockState(query.getQueryPosition());
        if (query.getLevel() instanceof ServerLevel level && state.is(Blocks.TRANSPOSER.get()))
            return Invalidatable.of(new TransposerDevice(level, query.getQueryPosition()));
        return Invalidatable.empty();
    }
}
