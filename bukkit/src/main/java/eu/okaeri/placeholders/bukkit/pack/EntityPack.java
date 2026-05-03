package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

public class EntityPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(Entity.class)
            .add("customName", e -> e.getCustomName())
            .add("entityId", e -> e.getEntityId())
            .add("fallDistance", e -> e.getFallDistance())
            .add("fireTicks", e -> e.getFireTicks())
            .add("lastDamageCause", e -> e.getLastDamageCause())
            .add("location", e -> e.getLocation())
            .add("maxFireTicks", e -> e.getMaxFireTicks())
            .add("passenger", e -> e.getPassenger())
            .add("ticksLived", e -> e.getTicksLived())
            .add("type", e -> e.getType())
            .add("uniqueId", e -> e.getUniqueId())
            .add("vehicle", e -> e.getVehicle())
            .add("velocity", e -> e.getVelocity())
            .add("world", e -> e.getWorld())
            .add("customNameVisible", e -> e.isCustomNameVisible())
            .add("dead", e -> e.isDead())
            .add("empty", e -> e.isEmpty())
            .add("insideVehicle", e -> e.isInsideVehicle())
            .add("onGround", e -> e.isOnGround())
            .add("valid", e -> e.isValid())
            .self(e -> e.getType().name());

        r.type(CommandSender.class)
            .add("name", s -> s.getName())
            .self(s -> s.getName());

        r.type(HumanEntity.class)
            .add("enderChest", e -> e.getEnderChest())
            .add("expToLevel", e -> e.getExpToLevel())
            .add("gameMode", e -> e.getGameMode())
            .add("inventory", e -> e.getInventory())
            .add("itemInHand", e -> e.getItemInHand())
            .add("itemOnCursor", e -> e.getItemOnCursor())
            .add("name", e -> e.getName())
            .add("openInventory", e -> e.getOpenInventory())
            .add("sleepTicks", e -> e.getSleepTicks())
            .add("blocking", e -> e.isBlocking())
            .add("sleeping", e -> e.isSleeping())
            .self(e -> e.getName());

        r.type(OfflinePlayer.class)
            .add("bedSpawnLocation", p -> p.getBedSpawnLocation())
            .add("firstPlayed", p -> p.getFirstPlayed())
            .add("lastPlayed", p -> p.getLastPlayed())
            .add("name", p -> p.getName())
            .add("uniqueId", p -> p.getUniqueId())
            .add("playedBefore", p -> p.hasPlayedBefore())
            .add("banned", p -> p.isBanned())
            .add("online", p -> p.isOnline())
            .add("whitelisted", p -> p.isWhitelisted())
            .self(p -> p.getName());

        r.type(Player.class)
            .add("address", p -> p.getAddress().getAddress().getHostAddress())
            .add("addressFull", p -> p.getAddress().toString())
            .add("addressPort", p -> p.getAddress().getPort())
            .add("allowFlight", p -> p.getAllowFlight())
            .add("bedSpawnLocation", p -> p.getBedSpawnLocation())
            .add("compassTarget", p -> p.getCompassTarget())
            .add("displayName", p -> p.getDisplayName())
            .add("exhaustion", p -> p.getExhaustion())
            .add("exp", p -> p.getExp())
            .add("flySpeed", p -> p.getFlySpeed())
            .add("foodLevel", p -> p.getFoodLevel())
            .add("healthScale", p -> p.getHealthScale())
            .add("level", p -> p.getLevel())
            .add("playerListName", p -> p.getPlayerListName())
            .add("playerTime", p -> p.getPlayerTime())
            .add("playerTimeOffset", p -> p.getPlayerTimeOffset())
            .add("weatherType", p -> p.getPlayerWeather())
            .add("saturation", p -> p.getSaturation())
            .add("spectatorTarget", p -> p.getSpectatorTarget())
            .add("totalExperience", p -> p.getTotalExperience())
            .add("walkSpeed", p -> p.getWalkSpeed())
            .add("flying", p -> p.isFlying())
            .add("healthScaled", p -> p.isHealthScaled())
            .add("playerTimeRelative", p -> p.isPlayerTimeRelative())
            .add("sleepingIgnored", p -> p.isSleepingIgnored())
            .add("sneaking", p -> p.isSneaking())
            .add("sprinting", p -> p.isSprinting())
            .self(p -> p.getName());
    }
}
