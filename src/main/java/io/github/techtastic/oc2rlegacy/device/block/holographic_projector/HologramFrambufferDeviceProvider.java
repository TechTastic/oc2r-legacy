package io.github.techtastic.oc2rlegacy.device.block.holographic_projector;

import li.cil.sedna.api.device.Device;
import li.cil.sedna.api.device.MemoryMappedDevice;
import li.cil.sedna.api.devicetree.DevicePropertyNames;
import li.cil.sedna.api.devicetree.DeviceTree;
import li.cil.sedna.api.devicetree.DeviceTreeProvider;
import li.cil.sedna.api.memory.MappedMemoryRange;
import li.cil.sedna.api.memory.MemoryMap;

import java.util.Optional;

public class HologramFrambufferDeviceProvider implements DeviceTreeProvider {
    @Override
    public Optional<String> getName(final Device device) {
        return Optional.of("framebuffer");
    }

    @Override
    public Optional<DeviceTree> createNode(final DeviceTree root, final MemoryMap memoryMap, final Device device, final String deviceName) {
        final Optional<MappedMemoryRange> range = memoryMap.getMemoryRange((MemoryMappedDevice) device);
        return range.map(r -> {
            final DeviceTree chosen = root.find("/chosen");
            chosen.addProp(DevicePropertyNames.RANGES);

            return chosen.getChild(deviceName, r.address());
        });
    }

    @Override
    public void visit(final DeviceTree node, final MemoryMap memoryMap, final Device device) {
        node
                .addProp(DevicePropertyNames.COMPATIBLE, "simple-framebuffer")
                .addProp("width", HologramFramebufferDevice.WIDTH)
                .addProp("height", HologramFramebufferDevice.HEIGHT)
                .addProp("length", HologramFramebufferDevice.LENGTH)
                .addProp("stride", HologramFramebufferDevice.BYTES_PER_VOXEL)
                .addProp("format", "r8g8b8a8")
                .addProp("no-map")
                .addProp(DevicePropertyNames.STATUS, "okay");
    }
}
