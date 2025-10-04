package io.github.techtastic.oc2rlegacy.device.block.holographic_projector;

import io.github.techtastic.oc2rlegacy.block.entity.HologramProjectorBE;
import li.cil.oc2.api.bus.device.object.Callback;
import li.cil.oc2.api.bus.device.object.ObjectDevice;
import li.cil.oc2.api.bus.device.rpc.RPCDevice;
import li.cil.oc2.api.bus.device.rpc.RPCMethodGroup;
import li.cil.oc2.api.bus.device.vm.VMDevice;
import li.cil.oc2.api.bus.device.vm.VMDeviceLoadResult;
import li.cil.oc2.api.bus.device.vm.context.VMContext;
import li.cil.oc2.common.bus.device.util.IdentityProxy;
import li.cil.oc2.common.bus.device.util.OptionalAddress;
import li.cil.oc2.common.serialization.BlobStorage;
import li.cil.oc2.common.util.NBTTagIds;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.UUID;

public class HologramProjectorDevice extends IdentityProxy<HologramProjectorBE> implements VMDevice/*, RPCDevice*/ {
    private static final String ADDRESS_TAG_NAME = "address";
    private static final String BLOB_HANDLE_TAG_NAME = "blob";

    private final HologramProjectorBE hologram;
    private HologramFramebufferDevice framebuffer;
    //private final ObjectDevice device;

    private final OptionalAddress address = new OptionalAddress();
    @Nullable private UUID blobHandle;

    public HologramProjectorDevice(HologramProjectorBE identity) {
        super(identity);
        this.hologram = identity;
        //this.device = new ObjectDevice(this, "hologram");
    }

    // Framebuffer Device Stuff

    public boolean hasChanges() {
        final HologramFramebufferDevice framebufferDevice = this.framebuffer;
        return framebufferDevice != null && framebufferDevice.hasChanges();
    }

    @Override
    public @NotNull VMDeviceLoadResult mount(@NotNull VMContext context) {
        if (!this.allocateDevice(context))
            return VMDeviceLoadResult.fail();

        assert this.framebuffer != null;
        if (!this.address.claim(context, this.framebuffer))
            return VMDeviceLoadResult.fail();

        return VMDeviceLoadResult.success();
    }

    @Override
    public void unmount() {
        final HologramFramebufferDevice framebufferDevice = this.framebuffer;
        this.framebuffer = null;
        if (framebufferDevice != null)
            framebufferDevice.close();

        if (this.blobHandle != null)
            BlobStorage.close(this.blobHandle);
    }

    @Override
    public void dispose() {
        if (this.blobHandle != null) {
            BlobStorage.delete(this.blobHandle);
            this.blobHandle = null;
        }

        this.address.clear();
    }

    @Override
    public @NotNull CompoundTag serializeNBT() {
        final CompoundTag tag = new CompoundTag();

        if (this.blobHandle != null)
            tag.putUUID(BLOB_HANDLE_TAG_NAME, this.blobHandle);
        if (this.address.isPresent())
            tag.putLong(ADDRESS_TAG_NAME, this.address.getAsLong());

        return tag;
    }

    @Override
    public void deserializeNBT(final CompoundTag tag) {
        if (tag.hasUUID(BLOB_HANDLE_TAG_NAME))
            this.blobHandle = tag.getUUID(BLOB_HANDLE_TAG_NAME);
        if (tag.contains(ADDRESS_TAG_NAME, NBTTagIds.TAG_LONG))
            this.address.set(tag.getLong(ADDRESS_TAG_NAME));
    }

    private boolean allocateDevice(VMContext context) {
        if (!context.getMemoryAllocator().claimMemory(4096)) {
            return false;
        } else {
            try {
                this.framebuffer = this.createFrameBufferDevice();
                return true;
            } catch (IOException var3) {
                return false;
            }
        }
    }

    private HologramFramebufferDevice createFrameBufferDevice() throws IOException {
        this.blobHandle = BlobStorage.validateHandle(this.blobHandle);
        final FileChannel channel = BlobStorage.getOrOpen(this.blobHandle);
        return new HologramFramebufferDevice(channel);
    }

    // Hologram Device Stuff

    /*@Callback
    public void test() {}

    @Override
    public @NotNull List<String> getTypeNames() {
        return this.device.getTypeNames();
    }

    @Override
    public @NotNull List<RPCMethodGroup> getMethodGroups() {
        return this.device.getMethodGroups();
    }*/
}
