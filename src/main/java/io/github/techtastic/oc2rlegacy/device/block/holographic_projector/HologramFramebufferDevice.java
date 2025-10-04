package io.github.techtastic.oc2rlegacy.device.block.holographic_projector;

import li.cil.sedna.api.Sizes;
import li.cil.sedna.api.device.MemoryMappedDevice;
import li.cil.sedna.api.memory.MemoryAccessException;
import li.cil.sedna.utils.DirectByteBufferUtils;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class HologramFramebufferDevice implements MemoryMappedDevice {
    private static final int WIDTH = 48;
    private static final int LENGTH = 48;
    private static final int HEIGHT = 32;
    private static final int BYTES_PER_VOXEL = 4;
    private static final int TOTAL_BYTES = WIDTH * LENGTH * HEIGHT * BYTES_PER_VOXEL;
    private final MappedByteBuffer buffer;

    private boolean dirty = false;

    public HologramFramebufferDevice(FileChannel channel) throws IOException {
        this.buffer = channel.map(FileChannel.MapMode.READ_WRITE, 0, WIDTH * HEIGHT * LENGTH * BYTES_PER_VOXEL);
    }

    public void close() {
        synchronized(this.buffer) {
            DirectByteBufferUtils.release(this.buffer);
        }
    }

    public boolean hasChanges() {
        return this.dirty;
    }

    @Override
    public int getLength() {
        return TOTAL_BYTES;
    }

    @Override
    public long load(int offset, int sizeLog2) throws MemoryAccessException {
        int size = 1 << sizeLog2;

        if (offset < 0 || offset + size > TOTAL_BYTES)
            throw new MemoryAccessException();

        return switch (sizeLog2) {
            case Sizes.SIZE_8_LOG2 -> this.buffer.get(offset) & 0xFFL;
            case Sizes.SIZE_16_LOG2 -> this.buffer.getShort(offset) & 0xFFFFL;
            case Sizes.SIZE_32_LOG2 -> this.buffer.getInt(offset) & 0xFFFFFFFFL;
            case Sizes.SIZE_64_LOG2 -> this.buffer.getLong(offset);
            default -> throw new MemoryAccessException();
        };
    }

    /**
     * Store (write) data to the device memory.
     *
     * @param offset The offset within this device's memory space
     * @param value The value to write
     * @param sizeLog2 The size of the write (Sizes.SIZE_8_LOG2, SIZE_16_LOG2, SIZE_32_LOG2, SIZE_64_LOG2)
     * @throws MemoryAccessException if the access is out of bounds
     */
    @Override
    public void store(int offset, long value, int sizeLog2) throws MemoryAccessException {
        int size = 1 << sizeLog2;

        if (offset < 0 || offset + size > TOTAL_BYTES)
            throw new MemoryAccessException();

        switch (sizeLog2) {
            case Sizes.SIZE_8_LOG2:
                this.buffer.put(offset, (byte) value);
                break;
            case Sizes.SIZE_16_LOG2:
                this.buffer.putShort(offset, (short) value);
                break;
            case Sizes.SIZE_32_LOG2:
                this.buffer.putInt(offset, (int) value);
                break;
            case Sizes.SIZE_64_LOG2:
                this.buffer.putLong(offset, value);
                break;
            default:
                throw new MemoryAccessException();
        }

        this.markDirty();
    }

    public int getVoxel(int x, int y, int z) {
        if (!isValidCoordinate(x, y, z))
            return 0;

        int index = getVoxelIndex(x, y, z);
        int r = this.buffer.get(index) & 0xFF;
        int g = this.buffer.get(index + 1) & 0xFF;
        int b = this.buffer.get(index + 2) & 0xFF;
        int a = this.buffer.get(index + 3) & 0xFF;

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public void setVoxel(int x, int y, int z, int rgba) {
        if (!this.isValidCoordinate(x, y, z)) {
            return;
        }

        int index = this.getVoxelIndex(x, y, z);
        this.buffer.put(index, (byte) ((rgba >> 16) & 0xFF));
        this.buffer.put(index + 1, (byte) ((rgba >> 8) & 0xFF));
        this.buffer.put(index + 2, (byte) (rgba & 0xFF));
        this.buffer.put(index + 3, (byte) ((rgba >> 24) & 0xFF));

        this.markDirty();
    }

    public void clear() {
        for (int i = 0; i < TOTAL_BYTES; i++) {
            this.buffer.put(i, (byte) 0);
        }
        this.markDirty();
    }

    public void clear(int rgba) {
        byte r = (byte) ((rgba >> 16) & 0xFF);
        byte g = (byte) ((rgba >> 8) & 0xFF);
        byte b = (byte) (rgba & 0xFF);
        byte a = (byte) ((rgba >> 24) & 0xFF);

        for (int i = 0; i < TOTAL_BYTES; i += 4) {
            this.buffer.put(i, r);
            this.buffer.put(i + 1, g);
            this.buffer.put(i + 2, b);
            this.buffer.put(i + 3, a);
        }
        this.markDirty();
    }

    private int getVoxelIndex(int x, int y, int z) {
        return (x * LENGTH * HEIGHT + y * HEIGHT + z) * BYTES_PER_VOXEL;
    }

    private boolean isValidCoordinate(int x, int y, int z) {
        return x >= 0 && x < WIDTH &&
                y >= 0 && y < HEIGHT &&
                z >= 0 && z < LENGTH;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void clearDirty() {
        this.dirty = false;
    }

    public ByteBuffer getMemoryReadOnly() {
        return this.buffer.asReadOnlyBuffer();
    }

    public ByteBuffer getMemoryBuffer() {
        return this.buffer;
    }
}
