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
        return create(false);
    }

    public static Placeholders create(boolean registerDefaults) {
        return Placeholders.create(registerDefaults)
                .registerPlaceholders(new BukkitPlaceholders());
    }

    @Override
    public void register(Placeholders placeholders) {

        // ChatColor
        placeholders.registerPlaceholder(ChatColor.class, (e, p) -> e.toString());

        // HumanEntity
        placeholders.registerPlaceholder(HumanEntity.class, "enderChest", (e, p) -> e.getEnderChest()); // Inventory
        placeholders.registerPlaceholder(HumanEntity.class, "expToLevel", (e, p) -> e.getExpToLevel());
        placeholders.registerPlaceholder(HumanEntity.class, "gameMode", (e, p) -> e.getGameMode()); // GameMode
        placeholders.registerPlaceholder(HumanEntity.class, "inventory", (e, p) -> e.getInventory()); // PlayerInventory
        placeholders.registerPlaceholder(HumanEntity.class, "itemInHand", (e, p) -> e.getItemInHand()); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "itemOnCursor", (e, p) -> e.getItemOnCursor()); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "name", (e, p) -> e.getName());
        placeholders.registerPlaceholder(HumanEntity.class, "openInventory", (e, p) -> e.getOpenInventory()); // InventoryView
        placeholders.registerPlaceholder(HumanEntity.class, "sleepTicks", (e, p) -> e.getSleepTicks());
        placeholders.registerPlaceholder(HumanEntity.class, "blocking", (e, p) -> e.isBlocking());
        placeholders.registerPlaceholder(HumanEntity.class, "sleeping", (e, p) -> e.isSleeping());
        placeholders.registerPlaceholder(HumanEntity.class, (e, p) -> e.getName());

        // Inventory
        placeholders.registerPlaceholder(Inventory.class, "name", (e, p) -> e.getName());
        placeholders.registerPlaceholder(Inventory.class, "size", (e, p) -> e.getSize());
        placeholders.registerPlaceholder(Inventory.class, "title", (e, p) -> e.getTitle());
        placeholders.registerPlaceholder(Inventory.class, "type", (e, p) -> e.getType()); // InventoryType (enum)
        placeholders.registerPlaceholder(Inventory.class, (e, p) -> e.getName());

        // InventoryView
        placeholders.registerPlaceholder(InventoryView.class, "bottomInventory", (e, p) -> e.getBottomInventory()); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "cursor", (e, p) -> e.getCursor()); // ItemStack
        placeholders.registerPlaceholder(InventoryView.class, "player", (e, p) -> e.getPlayer()); // HumanEntity
        placeholders.registerPlaceholder(InventoryView.class, "title", (e, p) -> e.getTitle());
        placeholders.registerPlaceholder(InventoryView.class, "topInventory", (e, p) -> e.getTopInventory()); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "type", (e, p) -> e.getType()); // InventoryType (enum)

        // PlayerInventory
        placeholders.registerPlaceholder(PlayerInventory.class, "boots", (e, p) -> e.getBoots()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "chestplate", (e, p) -> e.getChestplate()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "heldItemSlot", (e, p) -> e.getHeldItemSlot());
        placeholders.registerPlaceholder(PlayerInventory.class, "helmet", (e, p) -> e.getHelmet()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "holder", (e, p) -> e.getHolder()); // HumanEntity
        placeholders.registerPlaceholder(PlayerInventory.class, "itemInHand", (e, p) -> e.getItemInHand()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "leggings", (e, p) -> e.getLeggings()); // ItemStack

        // OfflinePlayer
        placeholders.registerPlaceholder(OfflinePlayer.class, "bedSpawnLocation", (e, p) -> e.getBedSpawnLocation()); // Location
        placeholders.registerPlaceholder(OfflinePlayer.class, "firstPlayed", (e, p) -> e.getFirstPlayed());
        placeholders.registerPlaceholder(OfflinePlayer.class, "lastPlayed", (e, p) -> e.getFirstPlayed());
        placeholders.registerPlaceholder(OfflinePlayer.class, "name", (e, p) -> e.getName());
        placeholders.registerPlaceholder(OfflinePlayer.class, "uniqueId", (e, p) -> e.getUniqueId());
        placeholders.registerPlaceholder(OfflinePlayer.class, "playedBefore", (e, p) -> e.hasPlayedBefore());
        placeholders.registerPlaceholder(OfflinePlayer.class, "banned", (e, p) -> e.isBanned());
        placeholders.registerPlaceholder(OfflinePlayer.class, "online", (e, p) -> e.isOnline());
        placeholders.registerPlaceholder(OfflinePlayer.class, "whitelisted", (e, p) -> e.isWhitelisted());
        placeholders.registerPlaceholder(OfflinePlayer.class, (e, p) -> e.getName());

        // Player
        placeholders.registerPlaceholder(Player.class, "address", (e, p) -> e.getAddress().getAddress().getHostAddress());
        placeholders.registerPlaceholder(Player.class, "addressFull", (e, p) -> e.getAddress().toString());
        placeholders.registerPlaceholder(Player.class, "addressPort", (e, p) -> e.getAddress().getPort());
        placeholders.registerPlaceholder(Player.class, "allowFlight", (e, p) -> e.getAllowFlight());
        placeholders.registerPlaceholder(Player.class, "bedSpawnLocation", (e, p) -> e.getBedSpawnLocation()); // Location
        placeholders.registerPlaceholder(Player.class, "compassTarget", (e, p) -> e.getCompassTarget()); // Location
        placeholders.registerPlaceholder(Player.class, "displayName", (e, p) -> e.getDisplayName());
        placeholders.registerPlaceholder(Player.class, "exhaustion", (e, p) -> e.getExhaustion());
        placeholders.registerPlaceholder(Player.class, "exp", (e, p) -> e.getExp());
        placeholders.registerPlaceholder(Player.class, "flySpeed", (e, p) -> e.getFlySpeed());
        placeholders.registerPlaceholder(Player.class, "foodLevel", (e, p) -> e.getFoodLevel());
        placeholders.registerPlaceholder(Player.class, "healthScale", (e, p) -> e.getHealthScale());
        placeholders.registerPlaceholder(Player.class, "level", (e, p) -> e.getLevel());
        placeholders.registerPlaceholder(Player.class, "playerListName", (e, p) -> e.getPlayerListName());
        placeholders.registerPlaceholder(Player.class, "playerTime", (e, p) -> e.getPlayerTime());
        placeholders.registerPlaceholder(Player.class, "playerTimeOffset", (e, p) -> e.getPlayerTimeOffset());
        placeholders.registerPlaceholder(Player.class, "weatherType", (e, p) -> e.getPlayerWeather()); // WeatherType (enum)
        placeholders.registerPlaceholder(Player.class, "saturation", (e, p) -> e.getSaturation());
        placeholders.registerPlaceholder(Player.class, "spectatorTarget", (e, p) -> e.getSpectatorTarget());
        placeholders.registerPlaceholder(Player.class, "totalExperience", (e, p) -> e.getTotalExperience());
        placeholders.registerPlaceholder(Player.class, "walkSpeed", (e, p) -> e.getWalkSpeed());
        placeholders.registerPlaceholder(Player.class, "flying", (e, p) -> e.isFlying());
        placeholders.registerPlaceholder(Player.class, "healthScaled", (e, p) -> e.isHealthScaled());
        placeholders.registerPlaceholder(Player.class, "playerTimeRelative", (e, p) -> e.isPlayerTimeRelative());
        placeholders.registerPlaceholder(Player.class, "sleepingIgnored", (e, p) -> e.isSleepingIgnored());
        placeholders.registerPlaceholder(Player.class, "sneaking", (e, p) -> e.isSneaking());
        placeholders.registerPlaceholder(Player.class, "sprinting", (e, p) -> e.isSprinting());
        placeholders.registerPlaceholder(Player.class, (e, p) -> e.getName());

        // Entity
        placeholders.registerPlaceholder(Entity.class, "customName", (e, p) -> e.getCustomName());
        placeholders.registerPlaceholder(Entity.class, "entityId", (e, p) -> e.getEntityId());
        placeholders.registerPlaceholder(Entity.class, "fallDistance", (e, p) -> e.getFallDistance());
        placeholders.registerPlaceholder(Entity.class, "fireTicks", (e, p) -> e.getFireTicks());
        placeholders.registerPlaceholder(Entity.class, "lastDamageCause", (e, p) -> e.getFireTicks()); // EntityDamageEvent
        placeholders.registerPlaceholder(Entity.class, "location", (e, p) -> e.getLocation()); // Location
        placeholders.registerPlaceholder(Entity.class, "maxFireTicks", (e, p) -> e.getMaxFireTicks());
        placeholders.registerPlaceholder(Entity.class, "passenger", (e, p) -> e.getPassenger()); // Entity
        placeholders.registerPlaceholder(Entity.class, "ticksLived", (e, p) -> e.getTicksLived());
        placeholders.registerPlaceholder(Entity.class, "type", (e, p) -> e.getType()); // EntityType (enum)
        placeholders.registerPlaceholder(Entity.class, "uniqueId", (e, p) -> e.getUniqueId());
        placeholders.registerPlaceholder(Entity.class, "vehicle", (e, p) -> e.getVehicle()); // Entity
        placeholders.registerPlaceholder(Entity.class, "velocity", (e, p) -> e.getVelocity()); // Vector
        placeholders.registerPlaceholder(Entity.class, "world", (e, p) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Entity.class, "customNameVisible", (e, p) -> e.isCustomNameVisible());
        placeholders.registerPlaceholder(Entity.class, "dead", (e, p) -> e.isDead());
        placeholders.registerPlaceholder(Entity.class, "empty", (e, p) -> e.isEmpty());
        placeholders.registerPlaceholder(Entity.class, "insideVehicle", (e, p) -> e.isInsideVehicle());
        placeholders.registerPlaceholder(Entity.class, "onGround", (e, p) -> e.isOnGround());
        placeholders.registerPlaceholder(Entity.class, "valid", (e, p) -> e.isValid());
        placeholders.registerPlaceholder(Entity.class, (e, p) -> e.getType().name());

        // Location
        placeholders.registerPlaceholder(Location.class, "block", (e, p) -> e.getBlock()); // Block
        placeholders.registerPlaceholder(Location.class, "blockX", (e, p) -> e.getBlockX());
        placeholders.registerPlaceholder(Location.class, "blockY", (e, p) -> e.getBlockY());
        placeholders.registerPlaceholder(Location.class, "blockZ", (e, p) -> e.getBlockZ());
        placeholders.registerPlaceholder(Location.class, "chunk", (e, p) -> e.getChunk()); // Chunk
        placeholders.registerPlaceholder(Location.class, "direction", (e, p) -> e.getDirection()); // Vector
        placeholders.registerPlaceholder(Location.class, "pitch", (e, p) -> e.getPitch());
        placeholders.registerPlaceholder(Location.class, "world", (e, p) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Location.class, "x", (e, p) -> e.getX());
        placeholders.registerPlaceholder(Location.class, "y", (e, p) -> e.getY());
        placeholders.registerPlaceholder(Location.class, "yaw", (e, p) -> e.getYaw());
        placeholders.registerPlaceholder(Location.class, "z", (e, p) -> e.getZ());
        placeholders.registerPlaceholder(Location.class, "length", (e, p) -> e.length());
        placeholders.registerPlaceholder(Location.class, "lengthSquared", (e, p) -> e.lengthSquared());
        placeholders.registerPlaceholder(Location.class, (e, p) -> "(world=" + e.getWorld().getName() + ", x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // Block
        placeholders.registerPlaceholder(Block.class, "biome", (e, p) -> e.getBiome()); // Biome (enum)
        placeholders.registerPlaceholder(Block.class, "chunk", (e, p) -> e.getChunk());
        placeholders.registerPlaceholder(Block.class, "data", (e, p) -> e.getData());
        placeholders.registerPlaceholder(Block.class, "humidity", (e, p) -> e.getHumidity());
        placeholders.registerPlaceholder(Block.class, "lightFromBlocks", (e, p) -> e.getLightFromBlocks());
        placeholders.registerPlaceholder(Block.class, "lightFromSky", (e, p) -> e.getLightFromSky());
        placeholders.registerPlaceholder(Block.class, "lightLevel", (e, p) -> e.getLightLevel());
        placeholders.registerPlaceholder(Block.class, "location", (e, p) -> e.getLocation());
        placeholders.registerPlaceholder(Block.class, "state", (e, p) -> e.getState()); // BlockState
        placeholders.registerPlaceholder(Block.class, "temperature", (e, p) -> e.getTemperature());
        placeholders.registerPlaceholder(Block.class, "type", (e, p) -> e.getType()); // Material (enum)
        placeholders.registerPlaceholder(Block.class, "world", (e, p) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Block.class, "x", (e, p) -> e.getX());
        placeholders.registerPlaceholder(Block.class, "y", (e, p) -> e.getY());
        placeholders.registerPlaceholder(Block.class, "z", (e, p) -> e.getZ());
        placeholders.registerPlaceholder(Block.class, "blockIndirectlyPowered", (e, p) -> e.isBlockIndirectlyPowered());
        placeholders.registerPlaceholder(Block.class, "blockPowered", (e, p) -> e.isBlockPowered());
        placeholders.registerPlaceholder(Block.class, "empty", (e, p) -> e.isEmpty());
        placeholders.registerPlaceholder(Block.class, "liquid", (e, p) -> e.isLiquid());
        placeholders.registerPlaceholder(Block.class, (e, p) -> e.getType().name() + "(x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // Chunk
        placeholders.registerPlaceholder(Chunk.class, "world", (e, p) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Chunk.class, "x", (e, p) -> e.getX());
        placeholders.registerPlaceholder(Chunk.class, "z", (e, p) -> e.getZ());
        placeholders.registerPlaceholder(Chunk.class, "loaded", (e, p) -> e.isLoaded());
        placeholders.registerPlaceholder(Chunk.class, (e, p) -> "(world=" + e.getWorld().getName() + ", x=" + e.getX() + ", z=" + e.getZ() + ")");

        // Vector
        placeholders.registerPlaceholder(Vector.class, "x", (e, p) -> e.getX());
        placeholders.registerPlaceholder(Vector.class, "y", (e, p) -> e.getY());
        placeholders.registerPlaceholder(Vector.class, "z", (e, p) -> e.getZ());
        placeholders.registerPlaceholder(Vector.class, (e, p) -> "(x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // World
        placeholders.registerPlaceholder(World.class, "allowAnimals", (e, p) -> e.getAllowAnimals());
        placeholders.registerPlaceholder(World.class, "allowMonsters", (e, p) -> e.getAllowMonsters());
        placeholders.registerPlaceholder(World.class, "ambientSpawnLimit", (e, p) -> e.getAmbientSpawnLimit());
        placeholders.registerPlaceholder(World.class, "animalSpawnLimit", (e, p) -> e.getAnimalSpawnLimit());
        placeholders.registerPlaceholder(World.class, "difficulty", (e, p) -> e.getDifficulty()); // Difficulty (enum)
        placeholders.registerPlaceholder(World.class, "environment", (e, p) -> e.getEnvironment()); // Environment (enum)
        placeholders.registerPlaceholder(World.class, "fullTime", (e, p) -> e.getFullTime());
        placeholders.registerPlaceholder(World.class, "maxHeight", (e, p) -> e.getMaxHeight());
        placeholders.registerPlaceholder(World.class, "name", (e, p) -> e.getName());
        placeholders.registerPlaceholder(World.class, "pvp", (e, p) -> e.getPVP());
        placeholders.registerPlaceholder(World.class, "seaLevel", (e, p) -> e.getSeaLevel());
        placeholders.registerPlaceholder(World.class, "seed", (e, p) -> e.getSeaLevel());
        placeholders.registerPlaceholder(World.class, "spawnLocation", (e, p) -> e.getSpawnLocation());
        placeholders.registerPlaceholder(World.class, "ticksPerAnimalSpawns", (e, p) -> e.getTicksPerAnimalSpawns());
        placeholders.registerPlaceholder(World.class, "ticksPerMonsterSpawns", (e, p) -> e.getTicksPerMonsterSpawns());
        placeholders.registerPlaceholder(World.class, "time", (e, p) -> e.getTime());
        placeholders.registerPlaceholder(World.class, "uid", (e, p) -> e.getUID());
        placeholders.registerPlaceholder(World.class, "waterAnimalSpawnLimit", (e, p) -> e.getWaterAnimalSpawnLimit());
        placeholders.registerPlaceholder(World.class, "weatherDuration", (e, p) -> e.getWeatherDuration());
        placeholders.registerPlaceholder(World.class, "worldBorder", (e, p) -> e.getWorldBorder()); // WorldBorder
        placeholders.registerPlaceholder(World.class, "worldFolder", (e, p) -> e.getWorldFolder());
        placeholders.registerPlaceholder(World.class, "worldType", (e, p) -> e.getWorldType()); // WorldType (enum)
        placeholders.registerPlaceholder(World.class, "storm", (e, p) -> e.hasStorm());
        placeholders.registerPlaceholder(World.class, "autoSave", (e, p) -> e.isAutoSave());
        placeholders.registerPlaceholder(World.class, "thundering", (e, p) -> e.isThundering());
        placeholders.registerPlaceholder(World.class, (e, p) -> e.getName());

        // WorldBorder
        placeholders.registerPlaceholder(WorldBorder.class, "center", (e, p) -> e.getCenter()); // Location
        placeholders.registerPlaceholder(WorldBorder.class, "damageAmount", (e, p) -> e.getDamageAmount());
        placeholders.registerPlaceholder(WorldBorder.class, "damageBuffer", (e, p) -> e.getDamageBuffer());
        placeholders.registerPlaceholder(WorldBorder.class, "size", (e, p) -> e.getSize());
        placeholders.registerPlaceholder(WorldBorder.class, "warningDistance", (e, p) -> e.getWarningDistance());
        placeholders.registerPlaceholder(WorldBorder.class, "warningTime", (e, p) -> e.getWarningTime());
        placeholders.registerPlaceholder(WorldBorder.class, (e, p) -> ((int) e.getSize() / 2) + "x" + ((int) e.getSize() / 2));

        // ItemStack
        placeholders.registerPlaceholder(ItemStack.class, "amount", (e, p) -> e.getAmount());
        placeholders.registerPlaceholder(ItemStack.class, "durability", (e, p) -> e.getDurability());
        placeholders.registerPlaceholder(ItemStack.class, "itemMeta", (e, p) -> e.getItemMeta());
        placeholders.registerPlaceholder(ItemStack.class, "maxStackSize", (e, p) -> e.getMaxStackSize());
        placeholders.registerPlaceholder(ItemStack.class, "type", (e, p) -> e.getType()); // Material (enum)
        placeholders.registerPlaceholder(ItemStack.class, "hasIteMeta", (e, p) -> e.hasItemMeta());
        placeholders.registerPlaceholder(ItemStack.class, (e, p) -> (e.getAmount() == 1) ? e.getType().name() : (e.getType().name() + " x " + e.getAmount()));

        // ItemMeta
        placeholders.registerPlaceholder(ItemMeta.class, "displayName", (e, p) -> e.getDisplayName());
        placeholders.registerPlaceholder(ItemMeta.class, "itemFlags", (e, p) -> enumList(e.getItemFlags()));
        placeholders.registerPlaceholder(ItemMeta.class, "lore", (e, p) -> String.join("\n", e.getLore()));
        placeholders.registerPlaceholder(ItemMeta.class, "hasDisplayName", (e, p) -> e.hasDisplayName());
        placeholders.registerPlaceholder(ItemMeta.class, "hasEnchants", (e, p) -> e.hasEnchants());
        placeholders.registerPlaceholder(ItemMeta.class, "hasLore", (e, p) -> e.hasLore());
        placeholders.registerPlaceholder(ItemMeta.class, (e, p) -> "(name=" + e.getDisplayName() + ", lore=" + String.join(", ", e.getLore()) + ")");

        // Nameable
//        placeholders.registerPlaceholder(Nameable.class, "customName", (e, p) -> e.getCustomName()); // nameable available in 1.8.8

        // ServerOperator
        placeholders.registerPlaceholder(ServerOperator.class, "op", (e, p) -> e.isOp());

        // Damageable
        placeholders.registerPlaceholder(Damageable.class, "health", (e, p) -> e.getHealth());
        placeholders.registerPlaceholder(Damageable.class, "healthHearts", (e, p) -> (int) (e.getHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "healthHeartsWithMax", (e, p) -> {
            int current = (int) (e.getHealth() / 2);
            int max = (int) (e.getMaxHealth() / 2);
            return current + "/" + max;
        });
        placeholders.registerPlaceholder(Damageable.class, "maxHealth", (e, p) -> e.getMaxHealth());
        placeholders.registerPlaceholder(Damageable.class, "maxHealthHearts", (e, p) -> (int) (e.getMaxHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "healthBarHearts", (e, p) -> {
            int maxHearts = (int) (e.getMaxHealth() / 2);
            String okColor = p.strAt(0, "c");
            String emptyColor = p.strAt(1, "7");
            String symbol = p.strAt(3, "❤");
            return renderHealthBar(e, maxHearts, symbol, okColor, emptyColor);
        });
        placeholders.registerPlaceholder(Damageable.class, "healthBar", (e, p) -> {
            int barLength = p.intAt(0, 40);
            String okColor = p.strAt(1, "c");
            String emptyColor = p.strAt(2, "7");
            String symbol = p.strAt(3, "|");
            return renderHealthBar(e, barLength, symbol, okColor, emptyColor);
        });
    }

    public static String enumList(Collection<? extends Enum> enums) {
        return enums.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    public static String renderHealthBar(Damageable damageable, int limit, String symbol, String okColor, String emptyColor) {
        double result = (damageable.getHealth() / damageable.getMaxHealth()) * limit;
        if ((result < 1) && (result > 0)) result = 1;
        return renderHealthBarWith((int) result, limit, symbol, okColor, emptyColor);
    }

    public static String renderHealthBarWith(int value, int max, String symbol, String okColor, String emptyColor) {

        StringBuilder buf = new StringBuilder();

        // empty
        if (value == 0) {
            buf.append(ChatColor.COLOR_CHAR).append(emptyColor);
            for (int i = 0; i < max; i++) buf.append(symbol);
            return buf.toString();
        }

        // full
        if (value == max) {
            buf.append(ChatColor.COLOR_CHAR).append(okColor);
            for (int i = 0; i < max; i++) buf.append(symbol);
            return buf.toString();
        }

        // partial
        buf.append(ChatColor.COLOR_CHAR).append(okColor);
        for (int i = 0; i < max; i++) {
            if (i == value) {
                buf.append(ChatColor.COLOR_CHAR).append(emptyColor);
            }
            buf.append(symbol);
        }

        return buf.toString();
    }
}
