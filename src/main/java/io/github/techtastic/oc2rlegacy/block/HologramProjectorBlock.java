package io.github.techtastic.oc2rlegacy.block;

import io.github.techtastic.oc2rlegacy.block.entity.HologramProjectorBE;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HologramProjectorBlock extends BaseEntityBlock {
    public HologramProjectorBlock(Properties p_49224_) {
        super(p_49224_);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos blockPos, @NotNull BlockState blockState) {
        return new HologramProjectorBE(blockPos, blockState);
    }
}
