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
            .add("block", Location::getBlock)
            .add("blockX", Location::getBlockX)
            .add("blockY", Location::getBlockY)
            .add("blockZ", Location::getBlockZ)
            .add("chunk", Location::getChunk)
            .add("direction", Location::getDirection)
            .add("pitch", Location::getPitch)
            .add("world", Location::getWorld)
            .add("x", Location::getX)
            .add("y", Location::getY)
            .add("yaw", Location::getYaw)
            .add("z", Location::getZ)
            .add("length", Location::length)
            .add("lengthSquared", Location::lengthSquared)
            .self(loc -> "(world=" + loc.getWorld().getName() + ", x=" + loc.getX() + ", y=" + loc.getY() + ", z=" + loc.getZ() + ")");

        r.type(Block.class)
            .add("biome", Block::getBiome)
            .add("chunk", Block::getChunk)
            .add("data", Block::getData)
            .add("humidity", Block::getHumidity)
            .add("lightFromBlocks", Block::getLightFromBlocks)
            .add("lightFromSky", Block::getLightFromSky)
            .add("lightLevel", Block::getLightLevel)
            .add("location", b -> b.getLocation())
            .add("state", Block::getState)
            .add("temperature", Block::getTemperature)
            .add("type", Block::getType)
            .add("world", Block::getWorld)
            .add("x", Block::getX)
            .add("y", Block::getY)
            .add("z", Block::getZ)
            .add("blockIndirectlyPowered", Block::isBlockIndirectlyPowered)
            .add("blockPowered", Block::isBlockPowered)
            .add("empty", Block::isEmpty)
            .add("liquid", Block::isLiquid)
            .self(b -> b.getType().name() + "(x=" + b.getX() + ", y=" + b.getY() + ", z=" + b.getZ() + ")");

        r.type(Chunk.class)
            .add("world", Chunk::getWorld)
            .add("x", Chunk::getX)
            .add("z", Chunk::getZ)
            .add("loaded", Chunk::isLoaded)
            .self(c -> "(world=" + c.getWorld().getName() + ", x=" + c.getX() + ", z=" + c.getZ() + ")");

        r.type(Vector.class)
            .add("x", Vector::getX)
            .add("y", Vector::getY)
            .add("z", Vector::getZ)
            .self(v -> "(x=" + v.getX() + ", y=" + v.getY() + ", z=" + v.getZ() + ")");
    }
}
