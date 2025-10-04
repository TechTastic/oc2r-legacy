package io.github.techtastic.oc2rlegacy.device.block.transposer;

import io.github.techtastic.oc2rlegacy.device.block.AbstractBlockRPCDevice;
import li.cil.oc2.api.bus.device.object.Callback;
import li.cil.oc2.api.bus.device.object.Parameter;
import li.cil.oc2.api.util.Side;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TransposerDevice extends AbstractBlockRPCDevice {
    private final ServerLevel level;
    private final BlockPos pos;

    protected TransposerDevice(ServerLevel level, BlockPos pos) {
        super("transposer");
        this.level = level;
        this.pos = pos;
    }

    @Callback
    public String getInventoryName(@Parameter("side") Side side) {
        BlockEntity be = getBlockEntity(side);
        if (getItemHandler(side) == null && getFluidHandler(side) == null)
            throw new RuntimeException("No inventory or tank at " + side + "!");
        if (be instanceof Nameable nameable)
            return nameable.getName().getString();
        return getBlockState(side).getBlock().getName().getString();
    }

    // Handle Fluids

    @Callback
    public int getTankLevel(@Parameter("side") Side side, @Parameter("tank") int tank) {
        IFluidHandler handler = getFluidHandler(side);
        if (handler == null)
            throw new RuntimeException("No tank at " + side + "!");
        return handler.getFluidInTank(tank).getAmount();
    }

    @Callback
    public int getTankCount(@Parameter("side") Side side) {
        IFluidHandler handler = getFluidHandler(side);
        if (handler == null)
            throw new RuntimeException("No tank at " + side + "!");
        return handler.getTanks();
    }

    @Callback
    public int getTankCapacity(@Parameter("side") Side side, @Parameter("tank") int tank) {
        IFluidHandler handler = getFluidHandler(side);
        if (handler == null)
            throw new RuntimeException("No tank at " + side + "!");
        return handler.getTankCapacity(tank);
    }

    @Callback
    public int transferFluid(@Parameter("sourceSide") Side sourceSide, @Parameter("sourceTank") int sourceTank, @Parameter("count") int count, @Parameter("sinkSide") Side sinkSide, @Parameter("sinkTank") int sinkTank) {
        IFluidHandler sourceHandler = getFluidHandler(sourceSide);
        if (sourceHandler == null)
            throw new RuntimeException("No tank at " + sourceSide + "!");
        IFluidHandler sinkHandler = getFluidHandler(sinkSide);
        if (sinkHandler == null)
            throw new RuntimeException("No tank at " + sinkSide + "!");

        boolean isValid = sinkHandler.isFluidValid(sinkTank, sourceHandler.getFluidInTank(sourceTank));
        boolean canExtract = !sourceHandler.drain(count, IFluidHandler.FluidAction.SIMULATE).isEmpty();
        FluidStack stack = sourceHandler.getFluidInTank(sourceTank).copy();
        stack.setAmount(count);
        boolean canInsert = sinkHandler.fill(stack, IFluidHandler.FluidAction.SIMULATE) < count;
        if (!isValid || !canExtract || !canInsert)
            return 0;

        stack = sourceHandler.drain(count, IFluidHandler.FluidAction.EXECUTE);
        return sinkHandler.fill(stack, IFluidHandler.FluidAction.EXECUTE);
    }

    @Callback
    public @NotNull FluidStack getFluidInTank(@Parameter("side") Side side, @Parameter("tank") int tank) {
        IFluidHandler handler = getFluidHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        return handler.getFluidInTank(tank);
    }

    // Handle Items

    @Callback
    public int getSlotStackSize(@Parameter("side") Side side, @Parameter("slot") int slot) {
        IItemHandler handler = getItemHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        return handler.getStackInSlot(slot).getCount();
    }

    @Callback
    public int getSlotMaxStackSize(@Parameter("side") Side side, @Parameter("slot") int slot) {
        IItemHandler handler = getItemHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        return handler.getSlotLimit(slot);
    }

    @Callback
    public int getInventorySize(@Parameter("side") Side side) {
        IItemHandler handler = getItemHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        return handler.getSlots();
    }

    @Callback
    public int transferItem(@Parameter("sourceSide") Side sourceSide, @Parameter("sourceSlot") int sourceSlot, @Parameter("count") int count, @Parameter("sinkSide") Side sinkSide, @Parameter("sinkSlot") int sinkSlot) {
        IItemHandler sourceHandler = getItemHandler(sourceSide);
        if (sourceHandler == null)
            throw new RuntimeException("No inventory at " + sourceSide + "!");
        IItemHandler sinkHandler = getItemHandler(sinkSide);
        if (sinkHandler == null)
            throw new RuntimeException("No inventory at " + sinkSide + "!");

        boolean isValid = sinkHandler.isItemValid(sinkSlot, sourceHandler.getStackInSlot(sourceSlot));
        boolean canExtract = !sourceHandler.extractItem(sourceSlot, count, true).isEmpty();
        ItemStack stack = sourceHandler.getStackInSlot(sourceSlot).copyWithCount(count);
        boolean canInsert = sinkHandler.insertItem(sinkSlot, stack, true).getCount() < count;
        if (!isValid || !canExtract || !canInsert)
            return 0;

        stack = sourceHandler.extractItem(sourceSlot, count, false);
        return sinkHandler.insertItem(sinkSlot, stack, false).getCount();
    }

    @Callback
    public boolean compareStacks(@Parameter("side") Side side, @Parameter("slotA") int slotA, @Parameter("slotB") int slotB, @Parameter("checkNBT") boolean checkNBT) {
        IItemHandler handler = getItemHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        if (checkNBT)
            return ItemStack.isSameItemSameTags(handler.getStackInSlot(slotA), handler.getStackInSlot(slotB));
        return ItemStack.isSameItem(handler.getStackInSlot(slotA), handler.getStackInSlot(slotB));
    }

    @Callback
    public boolean compareStacks(@Parameter("side") Side side, @Parameter("slotA") int slotA, @Parameter("slotB") int slotB) {
        return compareStacks(side, slotA, slotB, false);
    }

    @Callback
    public boolean areStacksEquivalent(@Parameter("side") Side side, @Parameter("slotA") int slotA, @Parameter("slotB") int slotB) {
        IItemHandler handler = getItemHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        return handler.getStackInSlot(slotA).getTags().allMatch(tag -> handler.getStackInSlot(slotB).is(tag));
    }

    @Callback
    public ItemStack getStackInSlot(@Parameter("side") Side side, @Parameter("slot") int slot) {
        IItemHandler handler = getItemHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        return handler.getStackInSlot(slot);
    }

    @Callback
    public List<ItemStack> getAllStacks(@Parameter("side") Side side) {
        IItemHandler handler = getItemHandler(side);
        if (handler == null)
            throw new RuntimeException("No inventory at " + side + "!");
        List<ItemStack> stacks = new ArrayList<>();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            stacks.add(slot, handler.getStackInSlot(slot));
        }
        return stacks;
    }

    // Utility

    @NotNull
    private BlockState getBlockState(Side side) {
        return this.level.getBlockState(this.pos.relative(side.getDirection()));
    }

    @Nullable
    private BlockEntity getBlockEntity(Side side) {
        return this.level.getBlockEntity(this.pos.relative(side.getDirection()));
    }

    @Nullable
    private IItemHandler getItemHandler(Side side) {
        BlockEntity be = getBlockEntity(side);
        if (be == null) return null;
        return be.getCapability(ForgeCapabilities.ITEM_HANDLER, side.getDirection().getOpposite()).map(handler -> handler).orElse(null);
    }

    @Nullable
    private IFluidHandler getFluidHandler(Side side) {
        BlockEntity be = getBlockEntity(side);
        if (be == null) return null;
        return be.getCapability(ForgeCapabilities.FLUID_HANDLER, side.getDirection().getOpposite()).map(handler -> handler).orElse(null);
    }
}
