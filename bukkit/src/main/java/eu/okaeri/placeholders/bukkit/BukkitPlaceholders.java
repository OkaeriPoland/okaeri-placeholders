package eu.okaeri.placeholders.bukkit;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.Placeholders;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
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

    @Override
    public void register(Placeholders placeholders) {

        // ChatColor
        placeholders.registerPlaceholder(ChatColor.class, (e, p, o) -> e.toString());

        // Inventory
        placeholders.registerPlaceholder(Inventory.class, "name", (e, p, o) -> e.getName());
        placeholders.registerPlaceholder(Inventory.class, "size", (e, p, o) -> e.getSize());
        placeholders.registerPlaceholder(Inventory.class, "title", (e, p, o) -> e.getTitle());
        placeholders.registerPlaceholder(Inventory.class, "type", (e, p, o) -> e.getType()); // InventoryType (enum)
        placeholders.registerPlaceholder(Inventory.class, (e, p, o) -> e.getName());

        // InventoryView
        placeholders.registerPlaceholder(InventoryView.class, "bottomInventory", (e, p, o) -> e.getBottomInventory()); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "cursor", (e, p, o) -> e.getCursor()); // ItemStack
        placeholders.registerPlaceholder(InventoryView.class, "player", (e, p, o) -> e.getPlayer()); // HumanEntity
        placeholders.registerPlaceholder(InventoryView.class, "title", (e, p, o) -> e.getTitle());
        placeholders.registerPlaceholder(InventoryView.class, "topInventory", (e, p, o) -> e.getTopInventory()); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "type", (e, p, o) -> e.getType()); // InventoryType (enum)

        // PlayerInventory
        placeholders.registerPlaceholder(PlayerInventory.class, "boots", (e, p, o) -> e.getBoots()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "chestplate", (e, p, o) -> e.getChestplate()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "heldItemSlot", (e, p, o) -> e.getHeldItemSlot());
        placeholders.registerPlaceholder(PlayerInventory.class, "helmet", (e, p, o) -> e.getHelmet()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "holder", (e, p, o) -> e.getHolder()); // HumanEntity
        placeholders.registerPlaceholder(PlayerInventory.class, "itemInHand", (e, p, o) -> e.getItemInHand()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "leggings", (e, p, o) -> e.getLeggings()); // ItemStack

        // Entity
        placeholders.registerPlaceholder(Entity.class, "customName", (e, p, o) -> e.getCustomName());
        placeholders.registerPlaceholder(Entity.class, "entityId", (e, p, o) -> e.getEntityId());
        placeholders.registerPlaceholder(Entity.class, "fallDistance", (e, p, o) -> e.getFallDistance());
        placeholders.registerPlaceholder(Entity.class, "fireTicks", (e, p, o) -> e.getFireTicks());
        placeholders.registerPlaceholder(Entity.class, "lastDamageCause", (e, p, o) -> e.getFireTicks()); // EntityDamageEvent
        placeholders.registerPlaceholder(Entity.class, "location", (e, p, o) -> e.getLocation()); // Location
        placeholders.registerPlaceholder(Entity.class, "maxFireTicks", (e, p, o) -> e.getMaxFireTicks());
        placeholders.registerPlaceholder(Entity.class, "passenger", (e, p, o) -> e.getPassenger()); // Entity
        placeholders.registerPlaceholder(Entity.class, "ticksLived", (e, p, o) -> e.getTicksLived());
        placeholders.registerPlaceholder(Entity.class, "type", (e, p, o) -> e.getType()); // EntityType (enum)
        placeholders.registerPlaceholder(Entity.class, "uniqueId", (e, p, o) -> e.getUniqueId());
        placeholders.registerPlaceholder(Entity.class, "vehicle", (e, p, o) -> e.getVehicle()); // Entity
        placeholders.registerPlaceholder(Entity.class, "velocity", (e, p, o) -> e.getVelocity()); // Vector
        placeholders.registerPlaceholder(Entity.class, "world", (e, p, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Entity.class, "customNameVisible", (e, p, o) -> e.isCustomNameVisible());
        placeholders.registerPlaceholder(Entity.class, "dead", (e, p, o) -> e.isDead());
        placeholders.registerPlaceholder(Entity.class, "empty", (e, p, o) -> e.isEmpty());
        placeholders.registerPlaceholder(Entity.class, "insideVehicle", (e, p, o) -> e.isInsideVehicle());
        placeholders.registerPlaceholder(Entity.class, "onGround", (e, p, o) -> e.isOnGround());
        placeholders.registerPlaceholder(Entity.class, "valid", (e, p, o) -> e.isValid());
        placeholders.registerPlaceholder(Entity.class, (e, p, o) -> e.getType().name());

        // CommandSender
        placeholders.registerPlaceholder(CommandSender.class, "name", (e, p, o) -> e.getName());
        placeholders.registerPlaceholder(CommandSender.class, (e, p, o) -> e.getName());

        // HumanEntity
        placeholders.registerPlaceholder(HumanEntity.class, "enderChest", (e, p, o) -> e.getEnderChest()); // Inventory
        placeholders.registerPlaceholder(HumanEntity.class, "expToLevel", (e, p, o) -> e.getExpToLevel());
        placeholders.registerPlaceholder(HumanEntity.class, "gameMode", (e, p, o) -> e.getGameMode()); // GameMode
        placeholders.registerPlaceholder(HumanEntity.class, "inventory", (e, p, o) -> e.getInventory()); // PlayerInventory
        placeholders.registerPlaceholder(HumanEntity.class, "itemInHand", (e, p, o) -> e.getItemInHand()); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "itemOnCursor", (e, p, o) -> e.getItemOnCursor()); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "name", (e, p, o) -> e.getName());
        placeholders.registerPlaceholder(HumanEntity.class, "openInventory", (e, p, o) -> e.getOpenInventory()); // InventoryView
        placeholders.registerPlaceholder(HumanEntity.class, "sleepTicks", (e, p, o) -> e.getSleepTicks());
        placeholders.registerPlaceholder(HumanEntity.class, "blocking", (e, p, o) -> e.isBlocking());
        placeholders.registerPlaceholder(HumanEntity.class, "sleeping", (e, p, o) -> e.isSleeping());
        placeholders.registerPlaceholder(HumanEntity.class, (e, p, o) -> e.getName());

        // OfflinePlayer
        placeholders.registerPlaceholder(OfflinePlayer.class, "bedSpawnLocation", (e, p, o) -> e.getBedSpawnLocation()); // Location
        placeholders.registerPlaceholder(OfflinePlayer.class, "firstPlayed", (e, p, o) -> e.getFirstPlayed());
        placeholders.registerPlaceholder(OfflinePlayer.class, "lastPlayed", (e, p, o) -> e.getFirstPlayed());
        placeholders.registerPlaceholder(OfflinePlayer.class, "name", (e, p, o) -> e.getName());
        placeholders.registerPlaceholder(OfflinePlayer.class, "uniqueId", (e, p, o) -> e.getUniqueId());
        placeholders.registerPlaceholder(OfflinePlayer.class, "playedBefore", (e, p, o) -> e.hasPlayedBefore());
        placeholders.registerPlaceholder(OfflinePlayer.class, "banned", (e, p, o) -> e.isBanned());
        placeholders.registerPlaceholder(OfflinePlayer.class, "online", (e, p, o) -> e.isOnline());
        placeholders.registerPlaceholder(OfflinePlayer.class, "whitelisted", (e, p, o) -> e.isWhitelisted());
        placeholders.registerPlaceholder(OfflinePlayer.class, (e, p, o) -> e.getName());

        // Player
        placeholders.registerPlaceholder(Player.class, "address", (e, p, o) -> e.getAddress().getAddress().getHostAddress());
        placeholders.registerPlaceholder(Player.class, "addressFull", (e, p, o) -> e.getAddress().toString());
        placeholders.registerPlaceholder(Player.class, "addressPort", (e, p, o) -> e.getAddress().getPort());
        placeholders.registerPlaceholder(Player.class, "allowFlight", (e, p, o) -> e.getAllowFlight());
        placeholders.registerPlaceholder(Player.class, "bedSpawnLocation", (e, p, o) -> e.getBedSpawnLocation()); // Location
        placeholders.registerPlaceholder(Player.class, "compassTarget", (e, p, o) -> e.getCompassTarget()); // Location
        placeholders.registerPlaceholder(Player.class, "displayName", (e, p, o) -> e.getDisplayName());
        placeholders.registerPlaceholder(Player.class, "exhaustion", (e, p, o) -> e.getExhaustion());
        placeholders.registerPlaceholder(Player.class, "exp", (e, p, o) -> e.getExp());
        placeholders.registerPlaceholder(Player.class, "flySpeed", (e, p, o) -> e.getFlySpeed());
        placeholders.registerPlaceholder(Player.class, "foodLevel", (e, p, o) -> e.getFoodLevel());
        placeholders.registerPlaceholder(Player.class, "healthScale", (e, p, o) -> e.getHealthScale());
        placeholders.registerPlaceholder(Player.class, "level", (e, p, o) -> e.getLevel());
        placeholders.registerPlaceholder(Player.class, "playerListName", (e, p, o) -> e.getPlayerListName());
        placeholders.registerPlaceholder(Player.class, "playerTime", (e, p, o) -> e.getPlayerTime());
        placeholders.registerPlaceholder(Player.class, "playerTimeOffset", (e, p, o) -> e.getPlayerTimeOffset());
        placeholders.registerPlaceholder(Player.class, "weatherType", (e, p, o) -> e.getPlayerWeather()); // WeatherType (enum)
        placeholders.registerPlaceholder(Player.class, "saturation", (e, p, o) -> e.getSaturation());
        placeholders.registerPlaceholder(Player.class, "spectatorTarget", (e, p, o) -> e.getSpectatorTarget());
        placeholders.registerPlaceholder(Player.class, "totalExperience", (e, p, o) -> e.getTotalExperience());
        placeholders.registerPlaceholder(Player.class, "walkSpeed", (e, p, o) -> e.getWalkSpeed());
        placeholders.registerPlaceholder(Player.class, "flying", (e, p, o) -> e.isFlying());
        placeholders.registerPlaceholder(Player.class, "healthScaled", (e, p, o) -> e.isHealthScaled());
        placeholders.registerPlaceholder(Player.class, "playerTimeRelative", (e, p, o) -> e.isPlayerTimeRelative());
        placeholders.registerPlaceholder(Player.class, "sleepingIgnored", (e, p, o) -> e.isSleepingIgnored());
        placeholders.registerPlaceholder(Player.class, "sneaking", (e, p, o) -> e.isSneaking());
        placeholders.registerPlaceholder(Player.class, "sprinting", (e, p, o) -> e.isSprinting());
        placeholders.registerPlaceholder(Player.class, (e, p, o) -> e.getName());

        // Location
        placeholders.registerPlaceholder(Location.class, "block", (e, p, o) -> e.getBlock()); // Block
        placeholders.registerPlaceholder(Location.class, "blockX", (e, p, o) -> e.getBlockX());
        placeholders.registerPlaceholder(Location.class, "blockY", (e, p, o) -> e.getBlockY());
        placeholders.registerPlaceholder(Location.class, "blockZ", (e, p, o) -> e.getBlockZ());
        placeholders.registerPlaceholder(Location.class, "chunk", (e, p, o) -> e.getChunk()); // Chunk
        placeholders.registerPlaceholder(Location.class, "direction", (e, p, o) -> e.getDirection()); // Vector
        placeholders.registerPlaceholder(Location.class, "pitch", (e, p, o) -> e.getPitch());
        placeholders.registerPlaceholder(Location.class, "world", (e, p, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Location.class, "x", (e, p, o) -> e.getX());
        placeholders.registerPlaceholder(Location.class, "y", (e, p, o) -> e.getY());
        placeholders.registerPlaceholder(Location.class, "yaw", (e, p, o) -> e.getYaw());
        placeholders.registerPlaceholder(Location.class, "z", (e, p, o) -> e.getZ());
        placeholders.registerPlaceholder(Location.class, "length", (e, p, o) -> e.length());
        placeholders.registerPlaceholder(Location.class, "lengthSquared", (e, p, o) -> e.lengthSquared());
        placeholders.registerPlaceholder(Location.class, (e, p, o) -> "(world=" + e.getWorld().getName() + ", x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // Block
        placeholders.registerPlaceholder(Block.class, "biome", (e, p, o) -> e.getBiome()); // Biome (enum)
        placeholders.registerPlaceholder(Block.class, "chunk", (e, p, o) -> e.getChunk());
        placeholders.registerPlaceholder(Block.class, "data", (e, p, o) -> e.getData());
        placeholders.registerPlaceholder(Block.class, "humidity", (e, p, o) -> e.getHumidity());
        placeholders.registerPlaceholder(Block.class, "lightFromBlocks", (e, p, o) -> e.getLightFromBlocks());
        placeholders.registerPlaceholder(Block.class, "lightFromSky", (e, p, o) -> e.getLightFromSky());
        placeholders.registerPlaceholder(Block.class, "lightLevel", (e, p, o) -> e.getLightLevel());
        placeholders.registerPlaceholder(Block.class, "location", (e, p, o) -> e.getLocation());
        placeholders.registerPlaceholder(Block.class, "state", (e, p, o) -> e.getState()); // BlockState
        placeholders.registerPlaceholder(Block.class, "temperature", (e, p, o) -> e.getTemperature());
        placeholders.registerPlaceholder(Block.class, "type", (e, p, o) -> e.getType()); // Material (enum)
        placeholders.registerPlaceholder(Block.class, "world", (e, p, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Block.class, "x", (e, p, o) -> e.getX());
        placeholders.registerPlaceholder(Block.class, "y", (e, p, o) -> e.getY());
        placeholders.registerPlaceholder(Block.class, "z", (e, p, o) -> e.getZ());
        placeholders.registerPlaceholder(Block.class, "blockIndirectlyPowered", (e, p, o) -> e.isBlockIndirectlyPowered());
        placeholders.registerPlaceholder(Block.class, "blockPowered", (e, p, o) -> e.isBlockPowered());
        placeholders.registerPlaceholder(Block.class, "empty", (e, p, o) -> e.isEmpty());
        placeholders.registerPlaceholder(Block.class, "liquid", (e, p, o) -> e.isLiquid());
        placeholders.registerPlaceholder(Block.class, (e, p, o) -> e.getType().name() + "(x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // Chunk
        placeholders.registerPlaceholder(Chunk.class, "world", (e, p, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Chunk.class, "x", (e, p, o) -> e.getX());
        placeholders.registerPlaceholder(Chunk.class, "z", (e, p, o) -> e.getZ());
        placeholders.registerPlaceholder(Chunk.class, "loaded", (e, p, o) -> e.isLoaded());
        placeholders.registerPlaceholder(Chunk.class, (e, p, o) -> "(world=" + e.getWorld().getName() + ", x=" + e.getX() + ", z=" + e.getZ() + ")");

        // Vector
        placeholders.registerPlaceholder(Vector.class, "x", (e, p, o) -> e.getX());
        placeholders.registerPlaceholder(Vector.class, "y", (e, p, o) -> e.getY());
        placeholders.registerPlaceholder(Vector.class, "z", (e, p, o) -> e.getZ());
        placeholders.registerPlaceholder(Vector.class, (e, p, o) -> "(x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // World
        placeholders.registerPlaceholder(World.class, "allowAnimals", (e, p, o) -> e.getAllowAnimals());
        placeholders.registerPlaceholder(World.class, "allowMonsters", (e, p, o) -> e.getAllowMonsters());
        placeholders.registerPlaceholder(World.class, "ambientSpawnLimit", (e, p, o) -> e.getAmbientSpawnLimit());
        placeholders.registerPlaceholder(World.class, "animalSpawnLimit", (e, p, o) -> e.getAnimalSpawnLimit());
        placeholders.registerPlaceholder(World.class, "difficulty", (e, p, o) -> e.getDifficulty()); // Difficulty (enum)
        placeholders.registerPlaceholder(World.class, "environment", (e, p, o) -> e.getEnvironment()); // Environment (enum)
        placeholders.registerPlaceholder(World.class, "fullTime", (e, p, o) -> e.getFullTime());
        placeholders.registerPlaceholder(World.class, "maxHeight", (e, p, o) -> e.getMaxHeight());
        placeholders.registerPlaceholder(World.class, "name", (e, p, o) -> e.getName());
        placeholders.registerPlaceholder(World.class, "pvp", (e, p, o) -> e.getPVP());
        placeholders.registerPlaceholder(World.class, "seaLevel", (e, p, o) -> e.getSeaLevel());
        placeholders.registerPlaceholder(World.class, "seed", (e, p, o) -> e.getSeaLevel());
        placeholders.registerPlaceholder(World.class, "spawnLocation", (e, p, o) -> e.getSpawnLocation());
        placeholders.registerPlaceholder(World.class, "ticksPerAnimalSpawns", (e, p, o) -> e.getTicksPerAnimalSpawns());
        placeholders.registerPlaceholder(World.class, "ticksPerMonsterSpawns", (e, p, o) -> e.getTicksPerMonsterSpawns());
        placeholders.registerPlaceholder(World.class, "time", (e, p, o) -> e.getTime());
        placeholders.registerPlaceholder(World.class, "uid", (e, p, o) -> e.getUID());
        placeholders.registerPlaceholder(World.class, "waterAnimalSpawnLimit", (e, p, o) -> e.getWaterAnimalSpawnLimit());
        placeholders.registerPlaceholder(World.class, "weatherDuration", (e, p, o) -> e.getWeatherDuration());
        placeholders.registerPlaceholder(World.class, "worldBorder", (e, p, o) -> e.getWorldBorder()); // WorldBorder
        placeholders.registerPlaceholder(World.class, "worldFolder", (e, p, o) -> e.getWorldFolder());
        placeholders.registerPlaceholder(World.class, "worldType", (e, p, o) -> e.getWorldType()); // WorldType (enum)
        placeholders.registerPlaceholder(World.class, "storm", (e, p, o) -> e.hasStorm());
        placeholders.registerPlaceholder(World.class, "autoSave", (e, p, o) -> e.isAutoSave());
        placeholders.registerPlaceholder(World.class, "thundering", (e, p, o) -> e.isThundering());
        placeholders.registerPlaceholder(World.class, (e, p, o) -> e.getName());

        // WorldBorder
        placeholders.registerPlaceholder(WorldBorder.class, "center", (e, p, o) -> e.getCenter()); // Location
        placeholders.registerPlaceholder(WorldBorder.class, "damageAmount", (e, p, o) -> e.getDamageAmount());
        placeholders.registerPlaceholder(WorldBorder.class, "damageBuffer", (e, p, o) -> e.getDamageBuffer());
        placeholders.registerPlaceholder(WorldBorder.class, "size", (e, p, o) -> e.getSize());
        placeholders.registerPlaceholder(WorldBorder.class, "warningDistance", (e, p, o) -> e.getWarningDistance());
        placeholders.registerPlaceholder(WorldBorder.class, "warningTime", (e, p, o) -> e.getWarningTime());
        placeholders.registerPlaceholder(WorldBorder.class, (e, p, o) -> ((int) e.getSize() / 2) + "x" + ((int) e.getSize() / 2));

        // ItemStack
        placeholders.registerPlaceholder(ItemStack.class, "amount", (e, p, o) -> e.getAmount());
        placeholders.registerPlaceholder(ItemStack.class, "durability", (e, p, o) -> e.getDurability());
        placeholders.registerPlaceholder(ItemStack.class, "itemMeta", (e, p, o) -> e.getItemMeta());
        placeholders.registerPlaceholder(ItemStack.class, "maxStackSize", (e, p, o) -> e.getMaxStackSize());
        placeholders.registerPlaceholder(ItemStack.class, "type", (e, p, o) -> e.getType()); // Material (enum)
        placeholders.registerPlaceholder(ItemStack.class, "hasIteMeta", (e, p, o) -> e.hasItemMeta());
        placeholders.registerPlaceholder(ItemStack.class, (e, p, o) -> (e.getAmount() == 1) ? e.getType().name() : (e.getType().name() + " x " + e.getAmount()));

        // ItemMeta
        placeholders.registerPlaceholder(ItemMeta.class, "displayName", (e, p, o) -> e.getDisplayName());
        placeholders.registerPlaceholder(ItemMeta.class, "itemFlags", (e, p, o) -> enumList(e.getItemFlags()));
        placeholders.registerPlaceholder(ItemMeta.class, "lore", (e, p, o) -> String.join("\n", e.getLore()));
        placeholders.registerPlaceholder(ItemMeta.class, "hasDisplayName", (e, p, o) -> e.hasDisplayName());
        placeholders.registerPlaceholder(ItemMeta.class, "hasEnchants", (e, p, o) -> e.hasEnchants());
        placeholders.registerPlaceholder(ItemMeta.class, "hasLore", (e, p, o) -> e.hasLore());
        placeholders.registerPlaceholder(ItemMeta.class, (e, p, o) -> "(name=" + e.getDisplayName() + ", lore=" + String.join(", ", e.getLore()) + ")");

        // Nameable
//        placeholders.registerPlaceholder(Nameable.class, "customName", (e, p, o) -> e.getCustomName()); // nameable available in 1.8.8

        // ServerOperator
        placeholders.registerPlaceholder(ServerOperator.class, "op", (e, p, o) -> e.isOp());

        // Damageable
        placeholders.registerPlaceholder(Damageable.class, "health", (e, p, o) -> e.getHealth());
        placeholders.registerPlaceholder(Damageable.class, "healthHearts", (e, p, o) -> (int) (e.getHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "healthHeartsWithMax", (e, p, o) -> {
            int current = (int) (e.getHealth() / 2);
            int max = (int) (e.getMaxHealth() / 2);
            return current + "/" + max;
        });
        placeholders.registerPlaceholder(Damageable.class, "maxHealth", (e, p, o) -> e.getMaxHealth());
        placeholders.registerPlaceholder(Damageable.class, "maxHealthHearts", (e, p, o) -> (int) (e.getMaxHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "healthBarHearts", (e, p, o) -> {
            int maxHearts = (int) (e.getMaxHealth() / 2);
            String okColor = p.strAt(0, "c");
            String emptyColor = p.strAt(1, "7");
            String symbol = p.strAt(3, "❤");
            return renderHealthBar(e, maxHearts, symbol, okColor, emptyColor);
        });
        placeholders.registerPlaceholder(Damageable.class, "healthBar", (e, p, o) -> {
            int barLength = p.intAt(0, 40);
            String okColor = p.strAt(1, "c");
            String emptyColor = p.strAt(2, "7");
            String symbol = p.strAt(3, "|");
            return renderHealthBar(e, barLength, symbol, okColor, emptyColor);
        });
    }
}
