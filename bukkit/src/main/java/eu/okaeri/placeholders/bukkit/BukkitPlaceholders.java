package eu.okaeri.placeholders.bukkit;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.ServerOperator;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.stream.Collectors;

public final class BukkitPlaceholders implements PlaceholderPack {

    public static Placeholders create() {
        return Placeholders.create().registerPlaceholders(new BukkitPlaceholders());
    }

    @Override
    public void register(Placeholders placeholders) {

        // HumanEntity
        placeholders.registerPlaceholder(HumanEntity.class, "enderChest", HumanEntity::getEnderChest); // Inventory
        placeholders.registerPlaceholder(HumanEntity.class, "expToLevel", HumanEntity::getExpToLevel);
        placeholders.registerPlaceholder(HumanEntity.class, "gameMode", HumanEntity::getGameMode); // GameMode
        placeholders.registerPlaceholder(HumanEntity.class, "inventory", HumanEntity::getInventory); // PlayerInventory
        placeholders.registerPlaceholder(HumanEntity.class, "itemInHand", HumanEntity::getItemInHand); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "itemOnCursor", HumanEntity::getItemOnCursor); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "name", HumanEntity::getName);
        placeholders.registerPlaceholder(HumanEntity.class, "openInventory", HumanEntity::getOpenInventory); // InventoryView
        placeholders.registerPlaceholder(HumanEntity.class, "sleepTicks", HumanEntity::getSleepTicks);
        placeholders.registerPlaceholder(HumanEntity.class, "blocking", HumanEntity::isBlocking);
        placeholders.registerPlaceholder(HumanEntity.class, "sleeping", HumanEntity::isSleeping);
        placeholders.registerPlaceholder(HumanEntity.class, HumanEntity::getName);

        // Inventory
        placeholders.registerPlaceholder(Inventory.class, "name", Inventory::getName);
        placeholders.registerPlaceholder(Inventory.class, "size", Inventory::getSize);
        placeholders.registerPlaceholder(Inventory.class, "title", Inventory::getTitle);
        placeholders.registerPlaceholder(Inventory.class, "type", Inventory::getType); // InventoryType (enum)
        placeholders.registerPlaceholder(Inventory.class, Inventory::getName);

        // InventoryView
        placeholders.registerPlaceholder(InventoryView.class, "bottomInventory", InventoryView::getBottomInventory); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "cursor", InventoryView::getCursor); // ItemStack
        placeholders.registerPlaceholder(InventoryView.class, "player", InventoryView::getPlayer); // HumanEntity
        placeholders.registerPlaceholder(InventoryView.class, "title", InventoryView::getTitle);
        placeholders.registerPlaceholder(InventoryView.class, "topInventory", InventoryView::getTopInventory); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "type", InventoryView::getType); // InventoryType (enum)

        // PlayerInventory
        placeholders.registerPlaceholder(PlayerInventory.class, "boots", PlayerInventory::getBoots); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "chestplate", PlayerInventory::getChestplate); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "heldItemSlot", PlayerInventory::getHeldItemSlot);
        placeholders.registerPlaceholder(PlayerInventory.class, "helmet", PlayerInventory::getHelmet); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "holder", PlayerInventory::getHolder); // HumanEntity
        placeholders.registerPlaceholder(PlayerInventory.class, "itemInHand", PlayerInventory::getItemInHand); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "leggings", PlayerInventory::getLeggings); // ItemStack

        // OfflinePlayer
        placeholders.registerPlaceholder(OfflinePlayer.class, "bedSpawnLocation", OfflinePlayer::getBedSpawnLocation); // Location
        placeholders.registerPlaceholder(OfflinePlayer.class, "firstPlayed", OfflinePlayer::getFirstPlayed);
        placeholders.registerPlaceholder(OfflinePlayer.class, "lastPlayed", OfflinePlayer::getFirstPlayed);
        placeholders.registerPlaceholder(OfflinePlayer.class, "name", OfflinePlayer::getName);
        placeholders.registerPlaceholder(OfflinePlayer.class, "uniqueId", OfflinePlayer::getUniqueId);
        placeholders.registerPlaceholder(OfflinePlayer.class, "playedBefore", OfflinePlayer::hasPlayedBefore);
        placeholders.registerPlaceholder(OfflinePlayer.class, "banned", OfflinePlayer::isBanned);
        placeholders.registerPlaceholder(OfflinePlayer.class, "online", OfflinePlayer::isOnline);
        placeholders.registerPlaceholder(OfflinePlayer.class, "whitelisted", OfflinePlayer::isWhitelisted);
        placeholders.registerPlaceholder(OfflinePlayer.class, OfflinePlayer::getName);

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

        // Entity
        placeholders.registerPlaceholder(Entity.class, "entityId", Entity::getEntityId);
        placeholders.registerPlaceholder(Entity.class, "fallDistance", Entity::getFallDistance);
        placeholders.registerPlaceholder(Entity.class, "fireTicks", Entity::getFireTicks);
        placeholders.registerPlaceholder(Entity.class, "lastDamageCause", Entity::getFireTicks); // EntityDamageEvent
        placeholders.registerPlaceholder(Entity.class, "location", Entity::getLocation); // Location
        placeholders.registerPlaceholder(Entity.class, "maxFireTicks", Entity::getMaxFireTicks);
        placeholders.registerPlaceholder(Entity.class, "passenger", Entity::getPassenger); // Entity
        placeholders.registerPlaceholder(Entity.class, "ticksLived", Entity::getTicksLived);
        placeholders.registerPlaceholder(Entity.class, "type", Entity::getType); // EntityType (enum)
        placeholders.registerPlaceholder(Entity.class, "uniqueId", Entity::getUniqueId);
        placeholders.registerPlaceholder(Entity.class, "vehicle", Entity::getVehicle); // Entity
        placeholders.registerPlaceholder(Entity.class, "velocity", Entity::getVelocity); // Vector
        placeholders.registerPlaceholder(Entity.class, "world", Entity::getWorld); // World
        placeholders.registerPlaceholder(Entity.class, "customNameVisible", Entity::isCustomNameVisible);
        placeholders.registerPlaceholder(Entity.class, "dead", Entity::isDead);
        placeholders.registerPlaceholder(Entity.class, "empty", Entity::isEmpty);
        placeholders.registerPlaceholder(Entity.class, "insideVehicle", Entity::isInsideVehicle);
        placeholders.registerPlaceholder(Entity.class, "onGround", Entity::isOnGround);
        placeholders.registerPlaceholder(Entity.class, "valid", Entity::isValid);
        placeholders.registerPlaceholder(Entity.class, entity -> entity.getType().name());

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
        placeholders.registerPlaceholder(Location.class, location -> "(world=" + location.getWorld().getName() + ", x=" + location.getX() + ", y=" + location.getY() + ", z=" + location.getZ() + ")");

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

        // Nameable
        placeholders.registerPlaceholder(Nameable.class, "customName", Nameable::getCustomName);

        // ServerOperator
        placeholders.registerPlaceholder(ServerOperator.class, "op", ServerOperator::isOp);

        // Damageable
        placeholders.registerPlaceholder(Damageable.class, "health", Damageable::getHealth);
        placeholders.registerPlaceholder(Damageable.class, "healthHearts", damageable -> (int) (damageable.getHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "maxHealth", Damageable::getMaxHealth);
        placeholders.registerPlaceholder(Damageable.class, "maxHealthHearts", damageable -> (int) (damageable.getMaxHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "healthBarHearts", damageable -> renderHealthBar(damageable, (int) (damageable.getMaxHealth() / 2), '❤'));
        placeholders.registerPlaceholder(Damageable.class, "healthBarHeartsNum", damageable -> ((int) (damageable.getHealth() / 2)) + "/" + ((int) (damageable.getMaxHealth() / 2)));
        placeholders.registerPlaceholder(Damageable.class, "healthBar10", damageable -> renderHealthBar(damageable, 10, '|'));
        placeholders.registerPlaceholder(Damageable.class, "healthBar20", damageable -> renderHealthBar(damageable, 20, '|'));
        placeholders.registerPlaceholder(Damageable.class, "healthBar30", damageable -> renderHealthBar(damageable, 30, '|'));
        placeholders.registerPlaceholder(Damageable.class, "healthBar40", damageable -> renderHealthBar(damageable, 40, '|'));
        placeholders.registerPlaceholder(Damageable.class, "healthBar50", damageable -> renderHealthBar(damageable, 50, '|'));
    }

    public static String enumList(Collection<? extends Enum> enums) {
        return enums.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public static String renderHealthBar(Damageable damageable, int limit, char pointChar) {
        double result = (damageable.getHealth() / damageable.getMaxHealth()) * limit;
        if ((result < 1) && (result > 0)) result = 1;
        return renderHealthBarWith((int) result, limit, pointChar);
    }

    public static String renderHealthBarWith(int value, int max, char pointChar) {

        StringBuilder buf = new StringBuilder();

        // empty
        if (value == 0) {
            buf.append(ChatColor.COLOR_CHAR).append("7");
            for (int i = 0; i < max; i++) buf.append(pointChar);
            return buf.toString();
        }

        // full
        if (value == max) {
            buf.append(ChatColor.COLOR_CHAR).append("c");
            for (int i = 0; i < max; i++) buf.append(pointChar);
            return buf.toString();
        }

        // partial
        buf.append(ChatColor.COLOR_CHAR).append("c");
        for (int i = 0; i < max; i++) {
            if (i == value) {
                buf.append(ChatColor.COLOR_CHAR).append("7");
            }
            buf.append(pointChar);
        }

        return buf.toString();
    }
}
