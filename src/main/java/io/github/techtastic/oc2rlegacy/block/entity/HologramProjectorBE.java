package io.github.techtastic.oc2rlegacy.block.entity;

import io.github.techtastic.oc2rlegacy.BlockEntities;
import io.github.techtastic.oc2rlegacy.device.block.holographic_projector.HologramProjectorDevice;
import li.cil.oc2.common.block.ProjectorBlock;
import li.cil.oc2.common.blockentity.ModBlockEntity;
import li.cil.oc2.common.capabilities.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;

import java.nio.ByteBuffer;

public class HologramProjectorBE extends ModBlockEntity {
    private final HologramProjectorDevice device = new HologramProjectorDevice(this);

    @OnlyIn(Dist.CLIENT)
    private ByteBuffer renderBuffer;

    private float scale = 1f;
    private Quaternionf rotation = new Quaternionf();

    public HologramProjectorBE(BlockPos p_155229_, BlockState p_155230_) {
        super(BlockEntities.HOLOGRAM_PROJECTOR.get(), p_155229_, p_155230_);
    }

    @OnlyIn(Dist.CLIENT)
    public void syncFramebuffer(ByteBuffer buffer) {
        this.renderBuffer = buffer;
    }

    @Override
    protected void collectCapabilities(@NotNull CapabilityCollector collector, @Nullable Direction direction) {
        collector.offer(Capabilities.device(), this.device);
    }
}
