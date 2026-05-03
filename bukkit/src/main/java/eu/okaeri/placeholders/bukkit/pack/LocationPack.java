package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

public class LocationPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Location.class)
            .add("block", l -> l.getBlock())
            .add("blockX", l -> l.getBlockX())
            .add("blockY", l -> l.getBlockY())
            .add("blockZ", l -> l.getBlockZ())
            .add("chunk", l -> l.getChunk())
            .add("direction", l -> l.getDirection())
            .add("pitch", l -> l.getPitch())
            .add("world", l -> l.getWorld())
            .add("x", l -> l.getX())
            .add("y", l -> l.getY())
            .add("yaw", l -> l.getYaw())
            .add("z", l -> l.getZ())
            .add("length", l -> l.length())
            .add("lengthSquared", l -> l.lengthSquared())
            .self(l -> "(world=" + l.getWorld().getName() + ", x=" + l.getX() + ", y=" + l.getY() + ", z=" + l.getZ() + ")");

        r.type(Block.class)
            .add("biome", b -> b.getBiome())
            .add("chunk", b -> b.getChunk())
            .add("data", b -> b.getData())
            .add("humidity", b -> b.getHumidity())
            .add("lightFromBlocks", b -> b.getLightFromBlocks())
            .add("lightFromSky", b -> b.getLightFromSky())
            .add("lightLevel", b -> b.getLightLevel())
            .add("location", b -> b.getLocation())
            .add("state", b -> b.getState())
            .add("temperature", b -> b.getTemperature())
            .add("type", b -> b.getType())
            .add("world", b -> b.getWorld())
            .add("x", b -> b.getX())
            .add("y", b -> b.getY())
            .add("z", b -> b.getZ())
            .add("blockIndirectlyPowered", b -> b.isBlockIndirectlyPowered())
            .add("blockPowered", b -> b.isBlockPowered())
            .add("empty", b -> b.isEmpty())
            .add("liquid", b -> b.isLiquid())
            .self(b -> b.getType().name() + "(x=" + b.getX() + ", y=" + b.getY() + ", z=" + b.getZ() + ")");

        r.type(Chunk.class)
            .add("world", c -> c.getWorld())
            .add("x", c -> c.getX())
            .add("z", c -> c.getZ())
            .add("loaded", c -> c.isLoaded())
            .self(c -> "(world=" + c.getWorld().getName() + ", x=" + c.getX() + ", z=" + c.getZ() + ")");

        r.type(Vector.class)
            .add("x", v -> v.getX())
            .add("y", v -> v.getY())
            .add("z", v -> v.getZ())
            .self(v -> "(x=" + v.getX() + ", y=" + v.getY() + ", z=" + v.getZ() + ")");
    }
}
