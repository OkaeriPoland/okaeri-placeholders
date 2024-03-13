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
        placeholders.registerPlaceholder(ChatColor.class, (e, a, o) -> e.toString());

        // Inventory
        placeholders.registerPlaceholder(Inventory.class, "name", (e, a, o) -> e.getName());
        placeholders.registerPlaceholder(Inventory.class, "size", (e, a, o) -> e.getSize());
        placeholders.registerPlaceholder(Inventory.class, "title", (e, a, o) -> e.getTitle());
        placeholders.registerPlaceholder(Inventory.class, "type", (e, a, o) -> e.getType()); // InventoryType (enum)
        placeholders.registerPlaceholder(Inventory.class, (e, a, o) -> e.getName());

        // InventoryView
        placeholders.registerPlaceholder(InventoryView.class, "bottomInventory", (e, a, o) -> e.getBottomInventory()); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "cursor", (e, a, o) -> e.getCursor()); // ItemStack
        placeholders.registerPlaceholder(InventoryView.class, "player", (e, a, o) -> e.getPlayer()); // HumanEntity
        placeholders.registerPlaceholder(InventoryView.class, "title", (e, a, o) -> e.getTitle());
        placeholders.registerPlaceholder(InventoryView.class, "topInventory", (e, a, o) -> e.getTopInventory()); // Inventory
        placeholders.registerPlaceholder(InventoryView.class, "type", (e, a, o) -> e.getType()); // InventoryType (enum)

        // PlayerInventory
        placeholders.registerPlaceholder(PlayerInventory.class, "boots", (e, a, o) -> e.getBoots()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "chestplate", (e, a, o) -> e.getChestplate()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "heldItemSlot", (e, a, o) -> e.getHeldItemSlot());
        placeholders.registerPlaceholder(PlayerInventory.class, "helmet", (e, a, o) -> e.getHelmet()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "holder", (e, a, o) -> e.getHolder()); // HumanEntity
        placeholders.registerPlaceholder(PlayerInventory.class, "itemInHand", (e, a, o) -> e.getItemInHand()); // ItemStack
        placeholders.registerPlaceholder(PlayerInventory.class, "leggings", (e, a, o) -> e.getLeggings()); // ItemStack

        // Entity
        placeholders.registerPlaceholder(Entity.class, "customName", (e, a, o) -> e.getCustomName());
        placeholders.registerPlaceholder(Entity.class, "entityId", (e, a, o) -> e.getEntityId());
        placeholders.registerPlaceholder(Entity.class, "fallDistance", (e, a, o) -> e.getFallDistance());
        placeholders.registerPlaceholder(Entity.class, "fireTicks", (e, a, o) -> e.getFireTicks());
        placeholders.registerPlaceholder(Entity.class, "lastDamageCause", (e, a, o) -> e.getFireTicks()); // EntityDamageEvent
        placeholders.registerPlaceholder(Entity.class, "location", (e, a, o) -> e.getLocation()); // Location
        placeholders.registerPlaceholder(Entity.class, "maxFireTicks", (e, a, o) -> e.getMaxFireTicks());
        placeholders.registerPlaceholder(Entity.class, "passenger", (e, a, o) -> e.getPassenger()); // Entity
        placeholders.registerPlaceholder(Entity.class, "ticksLived", (e, a, o) -> e.getTicksLived());
        placeholders.registerPlaceholder(Entity.class, "type", (e, a, o) -> e.getType()); // EntityType (enum)
        placeholders.registerPlaceholder(Entity.class, "uniqueId", (e, a, o) -> e.getUniqueId());
        placeholders.registerPlaceholder(Entity.class, "vehicle", (e, a, o) -> e.getVehicle()); // Entity
        placeholders.registerPlaceholder(Entity.class, "velocity", (e, a, o) -> e.getVelocity()); // Vector
        placeholders.registerPlaceholder(Entity.class, "world", (e, a, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Entity.class, "customNameVisible", (e, a, o) -> e.isCustomNameVisible());
        placeholders.registerPlaceholder(Entity.class, "dead", (e, a, o) -> e.isDead());
        placeholders.registerPlaceholder(Entity.class, "empty", (e, a, o) -> e.isEmpty());
        placeholders.registerPlaceholder(Entity.class, "insideVehicle", (e, a, o) -> e.isInsideVehicle());
        placeholders.registerPlaceholder(Entity.class, "onGround", (e, a, o) -> e.isOnGround());
        placeholders.registerPlaceholder(Entity.class, "valid", (e, a, o) -> e.isValid());
        placeholders.registerPlaceholder(Entity.class, (e, a, o) -> e.getType().name());

        // CommandSender
        placeholders.registerPlaceholder(CommandSender.class, "name", (e, a, o) -> e.getName());
        placeholders.registerPlaceholder(CommandSender.class, (e, a, o) -> e.getName());

        // HumanEntity
        placeholders.registerPlaceholder(HumanEntity.class, "enderChest", (e, a, o) -> e.getEnderChest()); // Inventory
        placeholders.registerPlaceholder(HumanEntity.class, "expToLevel", (e, a, o) -> e.getExpToLevel());
        placeholders.registerPlaceholder(HumanEntity.class, "gameMode", (e, a, o) -> e.getGameMode()); // GameMode
        placeholders.registerPlaceholder(HumanEntity.class, "inventory", (e, a, o) -> e.getInventory()); // PlayerInventory
        placeholders.registerPlaceholder(HumanEntity.class, "itemInHand", (e, a, o) -> e.getItemInHand()); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "itemOnCursor", (e, a, o) -> e.getItemOnCursor()); // ItemStack
        placeholders.registerPlaceholder(HumanEntity.class, "name", (e, a, o) -> e.getName());
        placeholders.registerPlaceholder(HumanEntity.class, "openInventory", (e, a, o) -> e.getOpenInventory()); // InventoryView
        placeholders.registerPlaceholder(HumanEntity.class, "sleepTicks", (e, a, o) -> e.getSleepTicks());
        placeholders.registerPlaceholder(HumanEntity.class, "blocking", (e, a, o) -> e.isBlocking());
        placeholders.registerPlaceholder(HumanEntity.class, "sleeping", (e, a, o) -> e.isSleeping());
        placeholders.registerPlaceholder(HumanEntity.class, (e, a, o) -> e.getName());

        // OfflinePlayer
        placeholders.registerPlaceholder(OfflinePlayer.class, "bedSpawnLocation", (e, a, o) -> e.getBedSpawnLocation()); // Location
        placeholders.registerPlaceholder(OfflinePlayer.class, "firstPlayed", (e, a, o) -> e.getFirstPlayed());
        placeholders.registerPlaceholder(OfflinePlayer.class, "lastPlayed", (e, a, o) -> e.getFirstPlayed());
        placeholders.registerPlaceholder(OfflinePlayer.class, "name", (e, a, o) -> e.getName());
        placeholders.registerPlaceholder(OfflinePlayer.class, "uniqueId", (e, a, o) -> e.getUniqueId());
        placeholders.registerPlaceholder(OfflinePlayer.class, "playedBefore", (e, a, o) -> e.hasPlayedBefore());
        placeholders.registerPlaceholder(OfflinePlayer.class, "banned", (e, a, o) -> e.isBanned());
        placeholders.registerPlaceholder(OfflinePlayer.class, "online", (e, a, o) -> e.isOnline());
        placeholders.registerPlaceholder(OfflinePlayer.class, "whitelisted", (e, a, o) -> e.isWhitelisted());
        placeholders.registerPlaceholder(OfflinePlayer.class, (e, a, o) -> e.getName());

        // Player
        placeholders.registerPlaceholder(Player.class, "address", (e, a, o) -> e.getAddress().getAddress().getHostAddress());
        placeholders.registerPlaceholder(Player.class, "addressFull", (e, a, o) -> e.getAddress().toString());
        placeholders.registerPlaceholder(Player.class, "addressPort", (e, a, o) -> e.getAddress().getPort());
        placeholders.registerPlaceholder(Player.class, "allowFlight", (e, a, o) -> e.getAllowFlight());
        placeholders.registerPlaceholder(Player.class, "bedSpawnLocation", (e, a, o) -> e.getBedSpawnLocation()); // Location
        placeholders.registerPlaceholder(Player.class, "compassTarget", (e, a, o) -> e.getCompassTarget()); // Location
        placeholders.registerPlaceholder(Player.class, "displayName", (e, a, o) -> e.getDisplayName());
        placeholders.registerPlaceholder(Player.class, "exhaustion", (e, a, o) -> e.getExhaustion());
        placeholders.registerPlaceholder(Player.class, "exp", (e, a, o) -> e.getExp());
        placeholders.registerPlaceholder(Player.class, "flySpeed", (e, a, o) -> e.getFlySpeed());
        placeholders.registerPlaceholder(Player.class, "foodLevel", (e, a, o) -> e.getFoodLevel());
        placeholders.registerPlaceholder(Player.class, "healthScale", (e, a, o) -> e.getHealthScale());
        placeholders.registerPlaceholder(Player.class, "level", (e, a, o) -> e.getLevel());
        placeholders.registerPlaceholder(Player.class, "playerListName", (e, a, o) -> e.getPlayerListName());
        placeholders.registerPlaceholder(Player.class, "playerTime", (e, a, o) -> e.getPlayerTime());
        placeholders.registerPlaceholder(Player.class, "playerTimeOffset", (e, a, o) -> e.getPlayerTimeOffset());
        placeholders.registerPlaceholder(Player.class, "weatherType", (e, a, o) -> e.getPlayerWeather()); // WeatherType (enum)
        placeholders.registerPlaceholder(Player.class, "saturation", (e, a, o) -> e.getSaturation());
        placeholders.registerPlaceholder(Player.class, "spectatorTarget", (e, a, o) -> e.getSpectatorTarget());
        placeholders.registerPlaceholder(Player.class, "totalExperience", (e, a, o) -> e.getTotalExperience());
        placeholders.registerPlaceholder(Player.class, "walkSpeed", (e, a, o) -> e.getWalkSpeed());
        placeholders.registerPlaceholder(Player.class, "flying", (e, a, o) -> e.isFlying());
        placeholders.registerPlaceholder(Player.class, "healthScaled", (e, a, o) -> e.isHealthScaled());
        placeholders.registerPlaceholder(Player.class, "playerTimeRelative", (e, a, o) -> e.isPlayerTimeRelative());
        placeholders.registerPlaceholder(Player.class, "sleepingIgnored", (e, a, o) -> e.isSleepingIgnored());
        placeholders.registerPlaceholder(Player.class, "sneaking", (e, a, o) -> e.isSneaking());
        placeholders.registerPlaceholder(Player.class, "sprinting", (e, a, o) -> e.isSprinting());
        placeholders.registerPlaceholder(Player.class, (e, a, o) -> e.getName());

        // Location
        placeholders.registerPlaceholder(Location.class, "block", (e, a, o) -> e.getBlock()); // Block
        placeholders.registerPlaceholder(Location.class, "blockX", (e, a, o) -> e.getBlockX());
        placeholders.registerPlaceholder(Location.class, "blockY", (e, a, o) -> e.getBlockY());
        placeholders.registerPlaceholder(Location.class, "blockZ", (e, a, o) -> e.getBlockZ());
        placeholders.registerPlaceholder(Location.class, "chunk", (e, a, o) -> e.getChunk()); // Chunk
        placeholders.registerPlaceholder(Location.class, "direction", (e, a, o) -> e.getDirection()); // Vector
        placeholders.registerPlaceholder(Location.class, "pitch", (e, a, o) -> e.getPitch());
        placeholders.registerPlaceholder(Location.class, "world", (e, a, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Location.class, "x", (e, a, o) -> e.getX());
        placeholders.registerPlaceholder(Location.class, "y", (e, a, o) -> e.getY());
        placeholders.registerPlaceholder(Location.class, "yaw", (e, a, o) -> e.getYaw());
        placeholders.registerPlaceholder(Location.class, "z", (e, a, o) -> e.getZ());
        placeholders.registerPlaceholder(Location.class, "length", (e, a, o) -> e.length());
        placeholders.registerPlaceholder(Location.class, "lengthSquared", (e, a, o) -> e.lengthSquared());
        placeholders.registerPlaceholder(Location.class, (e, a, o) -> "(world=" + e.getWorld().getName() + ", x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // Block
        placeholders.registerPlaceholder(Block.class, "biome", (e, a, o) -> e.getBiome()); // Biome (enum)
        placeholders.registerPlaceholder(Block.class, "chunk", (e, a, o) -> e.getChunk());
        placeholders.registerPlaceholder(Block.class, "data", (e, a, o) -> e.getData());
        placeholders.registerPlaceholder(Block.class, "humidity", (e, a, o) -> e.getHumidity());
        placeholders.registerPlaceholder(Block.class, "lightFromBlocks", (e, a, o) -> e.getLightFromBlocks());
        placeholders.registerPlaceholder(Block.class, "lightFromSky", (e, a, o) -> e.getLightFromSky());
        placeholders.registerPlaceholder(Block.class, "lightLevel", (e, a, o) -> e.getLightLevel());
        placeholders.registerPlaceholder(Block.class, "location", (e, a, o) -> e.getLocation());
        placeholders.registerPlaceholder(Block.class, "state", (e, a, o) -> e.getState()); // BlockState
        placeholders.registerPlaceholder(Block.class, "temperature", (e, a, o) -> e.getTemperature());
        placeholders.registerPlaceholder(Block.class, "type", (e, a, o) -> e.getType()); // Material (enum)
        placeholders.registerPlaceholder(Block.class, "world", (e, a, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Block.class, "x", (e, a, o) -> e.getX());
        placeholders.registerPlaceholder(Block.class, "y", (e, a, o) -> e.getY());
        placeholders.registerPlaceholder(Block.class, "z", (e, a, o) -> e.getZ());
        placeholders.registerPlaceholder(Block.class, "blockIndirectlyPowered", (e, a, o) -> e.isBlockIndirectlyPowered());
        placeholders.registerPlaceholder(Block.class, "blockPowered", (e, a, o) -> e.isBlockPowered());
        placeholders.registerPlaceholder(Block.class, "empty", (e, a, o) -> e.isEmpty());
        placeholders.registerPlaceholder(Block.class, "liquid", (e, a, o) -> e.isLiquid());
        placeholders.registerPlaceholder(Block.class, (e, a, o) -> e.getType().name() + "(x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // Chunk
        placeholders.registerPlaceholder(Chunk.class, "world", (e, a, o) -> e.getWorld()); // World
        placeholders.registerPlaceholder(Chunk.class, "x", (e, a, o) -> e.getX());
        placeholders.registerPlaceholder(Chunk.class, "z", (e, a, o) -> e.getZ());
        placeholders.registerPlaceholder(Chunk.class, "loaded", (e, a, o) -> e.isLoaded());
        placeholders.registerPlaceholder(Chunk.class, (e, a, o) -> "(world=" + e.getWorld().getName() + ", x=" + e.getX() + ", z=" + e.getZ() + ")");

        // Vector
        placeholders.registerPlaceholder(Vector.class, "x", (e, a, o) -> e.getX());
        placeholders.registerPlaceholder(Vector.class, "y", (e, a, o) -> e.getY());
        placeholders.registerPlaceholder(Vector.class, "z", (e, a, o) -> e.getZ());
        placeholders.registerPlaceholder(Vector.class, (e, a, o) -> "(x=" + e.getX() + ", y=" + e.getY() + ", z=" + e.getZ() + ")");

        // World
        placeholders.registerPlaceholder(World.class, "allowAnimals", (e, a, o) -> e.getAllowAnimals());
        placeholders.registerPlaceholder(World.class, "allowMonsters", (e, a, o) -> e.getAllowMonsters());
        placeholders.registerPlaceholder(World.class, "ambientSpawnLimit", (e, a, o) -> e.getAmbientSpawnLimit());
        placeholders.registerPlaceholder(World.class, "animalSpawnLimit", (e, a, o) -> e.getAnimalSpawnLimit());
        placeholders.registerPlaceholder(World.class, "difficulty", (e, a, o) -> e.getDifficulty()); // Difficulty (enum)
        placeholders.registerPlaceholder(World.class, "environment", (e, a, o) -> e.getEnvironment()); // Environment (enum)
        placeholders.registerPlaceholder(World.class, "fullTime", (e, a, o) -> e.getFullTime());
        placeholders.registerPlaceholder(World.class, "maxHeight", (e, a, o) -> e.getMaxHeight());
        placeholders.registerPlaceholder(World.class, "name", (e, a, o) -> e.getName());
        placeholders.registerPlaceholder(World.class, "pvp", (e, a, o) -> e.getPVP());
        placeholders.registerPlaceholder(World.class, "seaLevel", (e, a, o) -> e.getSeaLevel());
        placeholders.registerPlaceholder(World.class, "seed", (e, a, o) -> e.getSeaLevel());
        placeholders.registerPlaceholder(World.class, "spawnLocation", (e, a, o) -> e.getSpawnLocation());
        placeholders.registerPlaceholder(World.class, "ticksPerAnimalSpawns", (e, a, o) -> e.getTicksPerAnimalSpawns());
        placeholders.registerPlaceholder(World.class, "ticksPerMonsterSpawns", (e, a, o) -> e.getTicksPerMonsterSpawns());
        placeholders.registerPlaceholder(World.class, "time", (e, a, o) -> e.getTime());
        placeholders.registerPlaceholder(World.class, "uid", (e, a, o) -> e.getUID());
        placeholders.registerPlaceholder(World.class, "waterAnimalSpawnLimit", (e, a, o) -> e.getWaterAnimalSpawnLimit());
        placeholders.registerPlaceholder(World.class, "weatherDuration", (e, a, o) -> e.getWeatherDuration());
        placeholders.registerPlaceholder(World.class, "worldBorder", (e, a, o) -> e.getWorldBorder()); // WorldBorder
        placeholders.registerPlaceholder(World.class, "worldFolder", (e, a, o) -> e.getWorldFolder());
        placeholders.registerPlaceholder(World.class, "worldType", (e, a, o) -> e.getWorldType()); // WorldType (enum)
        placeholders.registerPlaceholder(World.class, "storm", (e, a, o) -> e.hasStorm());
        placeholders.registerPlaceholder(World.class, "autoSave", (e, a, o) -> e.isAutoSave());
        placeholders.registerPlaceholder(World.class, "thundering", (e, a, o) -> e.isThundering());
        placeholders.registerPlaceholder(World.class, (e, a, o) -> e.getName());

        // WorldBorder
        placeholders.registerPlaceholder(WorldBorder.class, "center", (e, a, o) -> e.getCenter()); // Location
        placeholders.registerPlaceholder(WorldBorder.class, "damageAmount", (e, a, o) -> e.getDamageAmount());
        placeholders.registerPlaceholder(WorldBorder.class, "damageBuffer", (e, a, o) -> e.getDamageBuffer());
        placeholders.registerPlaceholder(WorldBorder.class, "size", (e, a, o) -> e.getSize());
        placeholders.registerPlaceholder(WorldBorder.class, "warningDistance", (e, a, o) -> e.getWarningDistance());
        placeholders.registerPlaceholder(WorldBorder.class, "warningTime", (e, a, o) -> e.getWarningTime());
        placeholders.registerPlaceholder(WorldBorder.class, (e, a, o) -> ((int) e.getSize() / 2) + "x" + ((int) e.getSize() / 2));

        // ItemStack
        placeholders.registerPlaceholder(ItemStack.class, "amount", (e, a, o) -> e.getAmount());
        placeholders.registerPlaceholder(ItemStack.class, "durability", (e, a, o) -> e.getDurability());
        placeholders.registerPlaceholder(ItemStack.class, "itemMeta", (e, a, o) -> e.getItemMeta());
        placeholders.registerPlaceholder(ItemStack.class, "maxStackSize", (e, a, o) -> e.getMaxStackSize());
        placeholders.registerPlaceholder(ItemStack.class, "type", (e, a, o) -> e.getType()); // Material (enum)
        placeholders.registerPlaceholder(ItemStack.class, "hasIteMeta", (e, a, o) -> e.hasItemMeta());
        placeholders.registerPlaceholder(ItemStack.class, (e, a, o) -> (e.getAmount() == 1) ? e.getType().name() : (e.getType().name() + " x " + e.getAmount()));

        // ItemMeta
        placeholders.registerPlaceholder(ItemMeta.class, "displayName", (e, a, o) -> e.getDisplayName());
        placeholders.registerPlaceholder(ItemMeta.class, "itemFlags", (e, a, o) -> enumList(e.getItemFlags()));
        placeholders.registerPlaceholder(ItemMeta.class, "lore", (e, a, o) -> String.join("\n", e.getLore()));
        placeholders.registerPlaceholder(ItemMeta.class, "hasDisplayName", (e, a, o) -> e.hasDisplayName());
        placeholders.registerPlaceholder(ItemMeta.class, "hasEnchants", (e, a, o) -> e.hasEnchants());
        placeholders.registerPlaceholder(ItemMeta.class, "hasLore", (e, a, o) -> e.hasLore());
        placeholders.registerPlaceholder(ItemMeta.class, (e, a, o) -> "(name=" + e.getDisplayName() + ", lore=" + String.join(", ", e.getLore()) + ")");

        // Nameable
//        placeholders.registerPlaceholder(Nameable.class, "customName", (e, a, o) -> e.getCustomName()); // nameable available in 1.8.8

        // ServerOperator
        placeholders.registerPlaceholder(ServerOperator.class, "op", (e, a, o) -> e.isOp());

        // Damageable
        placeholders.registerPlaceholder(Damageable.class, "health", (e, a, o) -> e.getHealth());
        placeholders.registerPlaceholder(Damageable.class, "healthHearts", (e, a, o) -> (int) (e.getHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "healthHeartsWithMax", (e, a, o) -> {
            int current = (int) (e.getHealth() / 2);
            int max = (int) (e.getMaxHealth() / 2);
            return current + "/" + max;
        });
        placeholders.registerPlaceholder(Damageable.class, "maxHealth", (e, a, o) -> e.getMaxHealth());
        placeholders.registerPlaceholder(Damageable.class, "maxHealthHearts", (e, a, o) -> (int) (e.getMaxHealth() / 2));
        placeholders.registerPlaceholder(Damageable.class, "healthBarHearts", (e, a, o) -> {
            int maxHearts = (int) (e.getMaxHealth() / 2);
            String okColor = a.params().strAt(0, "c");
            String emptyColor = a.params().strAt(1, "7");
            String symbol = a.params().strAt(3, "❤");
            return renderHealthBar(e, maxHearts, symbol, okColor, emptyColor);
        });
        placeholders.registerPlaceholder(Damageable.class, "healthBar", (e, a, o) -> {
            int barLength = a.params().intAt(0, 40);
            String okColor = a.params().strAt(1, "c");
            String emptyColor = a.params().strAt(2, "7");
            String symbol = a.params().strAt(3, "|");
            return renderHealthBar(e, barLength, symbol, okColor, emptyColor);
        });
    }
}
