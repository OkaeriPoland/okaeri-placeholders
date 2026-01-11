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
            .add("customName", Entity::getCustomName)
            .add("entityId", Entity::getEntityId)
            .add("fallDistance", Entity::getFallDistance)
            .add("fireTicks", Entity::getFireTicks)
            .add("lastDamageCause", Entity::getLastDamageCause)
            .add("location", e -> e.getLocation())
            .add("maxFireTicks", Entity::getMaxFireTicks)
            .add("passenger", Entity::getPassenger)
            .add("ticksLived", Entity::getTicksLived)
            .add("type", Entity::getType)
            .add("uniqueId", Entity::getUniqueId)
            .add("vehicle", Entity::getVehicle)
            .add("velocity", Entity::getVelocity)
            .add("world", Entity::getWorld)
            .add("customNameVisible", Entity::isCustomNameVisible)
            .add("dead", Entity::isDead)
            .add("empty", Entity::isEmpty)
            .add("insideVehicle", Entity::isInsideVehicle)
            .add("onGround", Entity::isOnGround)
            .add("valid", Entity::isValid)
            .self(e -> e.getType().name());

        r.type(CommandSender.class)
            .add("name", CommandSender::getName)
            .self(CommandSender::getName);

        r.type(HumanEntity.class)
            .add("enderChest", HumanEntity::getEnderChest)
            .add("expToLevel", HumanEntity::getExpToLevel)
            .add("gameMode", HumanEntity::getGameMode)
            .add("inventory", HumanEntity::getInventory)
            .add("itemInHand", HumanEntity::getItemInHand)
            .add("itemOnCursor", HumanEntity::getItemOnCursor)
            .add("name", HumanEntity::getName)
            .add("openInventory", HumanEntity::getOpenInventory)
            .add("sleepTicks", HumanEntity::getSleepTicks)
            .add("blocking", HumanEntity::isBlocking)
            .add("sleeping", HumanEntity::isSleeping)
            .self(HumanEntity::getName);

        r.type(OfflinePlayer.class)
            .add("bedSpawnLocation", OfflinePlayer::getBedSpawnLocation)
            .add("firstPlayed", OfflinePlayer::getFirstPlayed)
            .add("lastPlayed", OfflinePlayer::getLastPlayed)
            .add("name", OfflinePlayer::getName)
            .add("uniqueId", OfflinePlayer::getUniqueId)
            .add("playedBefore", OfflinePlayer::hasPlayedBefore)
            .add("banned", OfflinePlayer::isBanned)
            .add("online", OfflinePlayer::isOnline)
            .add("whitelisted", OfflinePlayer::isWhitelisted)
            .self(OfflinePlayer::getName);

        r.type(Player.class)
            .add("address", p -> p.getAddress().getAddress().getHostAddress())
            .add("addressFull", p -> p.getAddress().toString())
            .add("addressPort", p -> p.getAddress().getPort())
            .add("allowFlight", Player::getAllowFlight)
            .add("bedSpawnLocation", Player::getBedSpawnLocation)
            .add("compassTarget", Player::getCompassTarget)
            .add("displayName", Player::getDisplayName)
            .add("exhaustion", Player::getExhaustion)
            .add("exp", Player::getExp)
            .add("flySpeed", Player::getFlySpeed)
            .add("foodLevel", Player::getFoodLevel)
            .add("healthScale", Player::getHealthScale)
            .add("level", Player::getLevel)
            .add("playerListName", Player::getPlayerListName)
            .add("playerTime", Player::getPlayerTime)
            .add("playerTimeOffset", Player::getPlayerTimeOffset)
            .add("weatherType", Player::getPlayerWeather)
            .add("saturation", Player::getSaturation)
            .add("spectatorTarget", Player::getSpectatorTarget)
            .add("totalExperience", Player::getTotalExperience)
            .add("walkSpeed", Player::getWalkSpeed)
            .add("flying", Player::isFlying)
            .add("healthScaled", Player::isHealthScaled)
            .add("playerTimeRelative", Player::isPlayerTimeRelative)
            .add("sleepingIgnored", Player::isSleepingIgnored)
            .add("sneaking", Player::isSneaking)
            .add("sprinting", Player::isSprinting)
            .self(Player::getName);
    }
}
