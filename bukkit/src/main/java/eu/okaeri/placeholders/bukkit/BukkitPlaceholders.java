package eu.okaeri.placeholders.bukkit;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldBorder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.stream.Collectors;

public final class BukkitPlaceholders implements PlaceholderPack {

    public static Placeholders create() {
        return Placeholders.create().registerPlaceholders(new BukkitPlaceholders());
    }

    @Override
    public void register(Placeholders placeholders) {

        // Player
        placeholders.registerPlaceholder(Player.class, "address", player -> player.getAddress().getAddress().getHostAddress());
        placeholders.registerPlaceholder(Player.class, "addressFull", player -> player.getAddress().toString());
        placeholders.registerPlaceholder(Player.class, "addressPort", player -> player.getAddress().getPort());
        placeholders.registerPlaceholder(Player.class, "allowFlight", Player::getAllowFlight);
        placeholders.registerPlaceholder(Player.class, "bedSpawnLocation", Player::getBedSpawnLocation); // Location
        placeholders.registerPlaceholder(Player.class, "compassTarget", Player::getCompassTarget); // Location
        placeholders.registerPlaceholder(Player.class, "displayName", Player::getDisplayName);
        placeholders.registerPlaceholder(Player.class, "exhaustion", Player::getExhaustion);
        placeholders.registerPlaceholder(Player.class, "exp", Player::getExp);
        placeholders.registerPlaceholder(Player.class, "flySpeed", Player::getFlySpeed);
        placeholders.registerPlaceholder(Player.class, "foodLevel", Player::getFoodLevel);
        placeholders.registerPlaceholder(Player.class, "healthScale", Player::getHealthScale);
        placeholders.registerPlaceholder(Player.class, "level", Player::getLevel);
        placeholders.registerPlaceholder(Player.class, "playerListName", Player::getPlayerListName);
        placeholders.registerPlaceholder(Player.class, "playerTime", Player::getPlayerTime);
        placeholders.registerPlaceholder(Player.class, "playerTimeOffset", Player::getPlayerTimeOffset);
        placeholders.registerPlaceholder(Player.class, "weatherType", Player::getPlayerWeather); // WeatherType (enum)
        placeholders.registerPlaceholder(Player.class, "saturation", Player::getSaturation);
        placeholders.registerPlaceholder(Player.class, "spectatorTarget", Player::getSpectatorTarget);
        placeholders.registerPlaceholder(Player.class, "totalExperience", Player::getTotalExperience);
        placeholders.registerPlaceholder(Player.class, "walkSpeed", Player::getWalkSpeed);
        placeholders.registerPlaceholder(Player.class, "flying", Player::isFlying);
        placeholders.registerPlaceholder(Player.class, "healthScaled", Player::isHealthScaled);
        placeholders.registerPlaceholder(Player.class, "playerTimeRelative", Player::isPlayerTimeRelative);
        placeholders.registerPlaceholder(Player.class, "sleepingIgnored", Player::isSleepingIgnored);
        placeholders.registerPlaceholder(Player.class, "sneaking", Player::isSneaking);
        placeholders.registerPlaceholder(Player.class, "sprinting", Player::isSprinting);
        placeholders.registerPlaceholder(Player.class, Player::getName);

        // Location
        placeholders.registerPlaceholder(Location.class, "block", Location::getBlock); // Block
        placeholders.registerPlaceholder(Location.class, "blockX", Location::getBlockX);
        placeholders.registerPlaceholder(Location.class, "blockY", Location::getBlockY);
        placeholders.registerPlaceholder(Location.class, "blockZ", Location::getBlockZ);
        placeholders.registerPlaceholder(Location.class, "chunk", Location::getChunk); // Chunk
        placeholders.registerPlaceholder(Location.class, "direction", Location::getDirection); // Vector
        placeholders.registerPlaceholder(Location.class, "pitch", Location::getPitch);
        placeholders.registerPlaceholder(Location.class, "world", Location::getWorld); // World
        placeholders.registerPlaceholder(Location.class, "x", Location::getX);
        placeholders.registerPlaceholder(Location.class, "y", Location::getY);
        placeholders.registerPlaceholder(Location.class, "yaw", Location::getYaw);
        placeholders.registerPlaceholder(Location.class, "z", Location::getZ);
        placeholders.registerPlaceholder(Location.class, "length", Location::length);
        placeholders.registerPlaceholder(Location.class, "lengthSquared", Location::lengthSquared);
        placeholders.registerPlaceholder(Location.class, location -> "(world=" + location.getWorld().getName() + "x=" + location.getX() + ", y=" + location.getY() + ", z=" + location.getZ() + ")");

        // Block
        placeholders.registerPlaceholder(Block.class, "biome", Block::getBiome); // Biome (enum)
        placeholders.registerPlaceholder(Block.class, "chunk", Block::getChunk);
        placeholders.registerPlaceholder(Block.class, "data", Block::getData);
        placeholders.registerPlaceholder(Block.class, "humidity", Block::getHumidity);
        placeholders.registerPlaceholder(Block.class, "lightFromBlocks", Block::getLightFromBlocks);
        placeholders.registerPlaceholder(Block.class, "lightFromSky", Block::getLightFromSky);
        placeholders.registerPlaceholder(Block.class, "lightLevel", Block::getLightLevel);
        placeholders.registerPlaceholder(Block.class, "location", Block::getLocation);
        placeholders.registerPlaceholder(Block.class, "state", Block::getState); // BlockState
        placeholders.registerPlaceholder(Block.class, "temperature", Block::getTemperature);
        placeholders.registerPlaceholder(Block.class, "type", Block::getType); // Material (enum)
        placeholders.registerPlaceholder(Block.class, "world", Block::getWorld); // World
        placeholders.registerPlaceholder(Block.class, "x", Block::getX);
        placeholders.registerPlaceholder(Block.class, "y", Block::getY);
        placeholders.registerPlaceholder(Block.class, "z", Block::getZ);
        placeholders.registerPlaceholder(Block.class, "blockIndirectlyPowered", Block::isBlockIndirectlyPowered);
        placeholders.registerPlaceholder(Block.class, "blockPowered", Block::isBlockPowered);
        placeholders.registerPlaceholder(Block.class, "empty", Block::isEmpty);
        placeholders.registerPlaceholder(Block.class, "liquid", Block::isLiquid);
        placeholders.registerPlaceholder(Block.class, block -> block.getType().name() + "(x=" + block.getX() + ", y=" + block.getY() + ", z=" + block.getZ() + ")");

        // Chunk
        placeholders.registerPlaceholder(Chunk.class, "world", Chunk::getWorld); // World
        placeholders.registerPlaceholder(Chunk.class, "x", Chunk::getX);
        placeholders.registerPlaceholder(Chunk.class, "z", Chunk::getZ);
        placeholders.registerPlaceholder(Chunk.class, "loaded", Chunk::isLoaded);
        placeholders.registerPlaceholder(Chunk.class, chunk -> "(world=" + chunk.getWorld().getName() + ", x=" + chunk.getX() + ", z=" + chunk.getZ() + ")");

        // Vector
        placeholders.registerPlaceholder(Vector.class, "x", Vector::getX);
        placeholders.registerPlaceholder(Vector.class, "y", Vector::getY);
        placeholders.registerPlaceholder(Vector.class, "z", Vector::getZ);
        placeholders.registerPlaceholder(Vector.class, vector -> "(x=" + vector.getX() + ", y=" + vector.getY() + ", z=" + vector.getZ() + ")");

        // World
        placeholders.registerPlaceholder(World.class, "allowAnimals", World::getAllowAnimals);
        placeholders.registerPlaceholder(World.class, "allowMonsters", World::getAllowMonsters);
        placeholders.registerPlaceholder(World.class, "ambientSpawnLimit", World::getAmbientSpawnLimit);
        placeholders.registerPlaceholder(World.class, "animalSpawnLimit", World::getAnimalSpawnLimit);
        placeholders.registerPlaceholder(World.class, "difficulty", World::getDifficulty); // Difficulty (enum)
        placeholders.registerPlaceholder(World.class, "environment", World::getEnvironment); // Environment (enum)
        placeholders.registerPlaceholder(World.class, "fullTime", World::getFullTime);
        placeholders.registerPlaceholder(World.class, "maxHeight", World::getMaxHeight);
        placeholders.registerPlaceholder(World.class, "name", World::getName);
        placeholders.registerPlaceholder(World.class, "pvp", World::getPVP);
        placeholders.registerPlaceholder(World.class, "seaLevel", World::getSeaLevel);
        placeholders.registerPlaceholder(World.class, "seed", World::getSeaLevel);
        placeholders.registerPlaceholder(World.class, "spawnLocation", World::getSpawnLocation);
        placeholders.registerPlaceholder(World.class, "ticksPerAnimalSpawns", World::getTicksPerAnimalSpawns);
        placeholders.registerPlaceholder(World.class, "ticksPerMonsterSpawns", World::getTicksPerMonsterSpawns);
        placeholders.registerPlaceholder(World.class, "time", World::getTime);
        placeholders.registerPlaceholder(World.class, "uid", World::getUID);
        placeholders.registerPlaceholder(World.class, "waterAnimalSpawnLimit", World::getWaterAnimalSpawnLimit);
        placeholders.registerPlaceholder(World.class, "weatherDuration", World::getWeatherDuration);
        placeholders.registerPlaceholder(World.class, "worldBorder", World::getWorldBorder); // WorldBorder
        placeholders.registerPlaceholder(World.class, "worldFolder", World::getWorldFolder);
        placeholders.registerPlaceholder(World.class, "worldType", World::getWorldType); // WorldType (enum)
        placeholders.registerPlaceholder(World.class, "storm", World::hasStorm);
        placeholders.registerPlaceholder(World.class, "autoSave", World::isAutoSave);
        placeholders.registerPlaceholder(World.class, "thundering", World::isThundering);
        placeholders.registerPlaceholder(World.class, World::getName);

        // WorldBorder
        placeholders.registerPlaceholder(WorldBorder.class, "center", WorldBorder::getCenter); // Location
        placeholders.registerPlaceholder(WorldBorder.class, "damageAmount", WorldBorder::getDamageAmount);
        placeholders.registerPlaceholder(WorldBorder.class, "damageBuffer", WorldBorder::getDamageBuffer);
        placeholders.registerPlaceholder(WorldBorder.class, "size", WorldBorder::getSize);
        placeholders.registerPlaceholder(WorldBorder.class, "warningDistance", WorldBorder::getWarningDistance);
        placeholders.registerPlaceholder(WorldBorder.class, "warningTime", WorldBorder::getWarningTime);
        placeholders.registerPlaceholder(WorldBorder.class, worldBorder -> ((int) worldBorder.getSize() / 2) + "x" + ((int) worldBorder.getSize() / 2));

        // ItemStack
        placeholders.registerPlaceholder(ItemStack.class, "amount", ItemStack::getAmount);
        placeholders.registerPlaceholder(ItemStack.class, "durability", ItemStack::getDurability);
        placeholders.registerPlaceholder(ItemStack.class, "itemMeta", ItemStack::getItemMeta);
        placeholders.registerPlaceholder(ItemStack.class, "maxStackSize", ItemStack::getMaxStackSize);
        placeholders.registerPlaceholder(ItemStack.class, "type", ItemStack::getType); // Material (enum)
        placeholders.registerPlaceholder(ItemStack.class, "hasIteMeta", ItemStack::hasItemMeta);
        placeholders.registerPlaceholder(ItemStack.class, itemStack -> (itemStack.getAmount() == 1) ? itemStack.getType().name() : (itemStack.getType().name() + " x " + itemStack.getAmount()));

        // ItemMeta
        placeholders.registerPlaceholder(ItemMeta.class, "displayName", ItemMeta::getDisplayName);
        placeholders.registerPlaceholder(ItemMeta.class, "itemFlags", meta -> enumList(meta.getItemFlags()));
        placeholders.registerPlaceholder(ItemMeta.class, "lore", meta -> String.join("\n", meta.getLore()));
        placeholders.registerPlaceholder(ItemMeta.class, "hasDisplayName", ItemMeta::hasDisplayName);
        placeholders.registerPlaceholder(ItemMeta.class, "hasEnchants", ItemMeta::hasEnchants);
        placeholders.registerPlaceholder(ItemMeta.class, "hasLore", ItemMeta::hasLore);
        placeholders.registerPlaceholder(ItemMeta.class, itemMeta -> "(name=" + itemMeta.getDisplayName() + ", lore=" + String.join(", ", itemMeta.getLore()) + ")");
    }

    private static String enumList(Collection<? extends Enum> enums) {
        return enums.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }
}
