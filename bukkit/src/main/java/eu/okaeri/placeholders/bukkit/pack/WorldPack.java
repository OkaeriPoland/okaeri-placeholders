package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class WorldPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(World.class)
            .add("allowAnimals", World::getAllowAnimals)
            .add("allowMonsters", World::getAllowMonsters)
            .add("ambientSpawnLimit", World::getAmbientSpawnLimit)
            .add("animalSpawnLimit", World::getAnimalSpawnLimit)
            .add("difficulty", World::getDifficulty)
            .add("environment", World::getEnvironment)
            .add("fullTime", World::getFullTime)
            .add("maxHeight", World::getMaxHeight)
            .add("name", World::getName)
            .add("pvp", World::getPVP)
            .add("seaLevel", World::getSeaLevel)
            .add("seed", World::getSeed)
            .add("spawnLocation", World::getSpawnLocation)
            .add("ticksPerAnimalSpawns", World::getTicksPerAnimalSpawns)
            .add("ticksPerMonsterSpawns", World::getTicksPerMonsterSpawns)
            .add("time", World::getTime)
            .add("uid", World::getUID)
            .add("waterAnimalSpawnLimit", World::getWaterAnimalSpawnLimit)
            .add("weatherDuration", World::getWeatherDuration)
            .add("worldBorder", World::getWorldBorder)
            .add("worldFolder", World::getWorldFolder)
            .add("worldType", World::getWorldType)
            .add("storm", World::hasStorm)
            .add("autoSave", World::isAutoSave)
            .add("thundering", World::isThundering)
            .self(World::getName);

        r.type(WorldBorder.class)
            .add("center", WorldBorder::getCenter)
            .add("damageAmount", WorldBorder::getDamageAmount)
            .add("damageBuffer", WorldBorder::getDamageBuffer)
            .add("size", WorldBorder::getSize)
            .add("warningDistance", WorldBorder::getWarningDistance)
            .add("warningTime", WorldBorder::getWarningTime)
            .self(wb -> ((int) wb.getSize() / 2) + "x" + ((int) wb.getSize() / 2));
    }
}
