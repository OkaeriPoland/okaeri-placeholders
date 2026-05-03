package eu.okaeri.placeholders.bukkit.pack;

import eu.okaeri.placeholders.PlaceholderPack;
import eu.okaeri.placeholders.registry.Registry;
import org.bukkit.World;
import org.bukkit.WorldBorder;

public class WorldPack implements PlaceholderPack {

    @Override
    public void register(Registry r) {
        r.type(World.class)
            .add("allowAnimals", w -> w.getAllowAnimals())
            .add("allowMonsters", w -> w.getAllowMonsters())
            .add("ambientSpawnLimit", w -> w.getAmbientSpawnLimit())
            .add("animalSpawnLimit", w -> w.getAnimalSpawnLimit())
            .add("difficulty", w -> w.getDifficulty())
            .add("environment", w -> w.getEnvironment())
            .add("fullTime", w -> w.getFullTime())
            .add("maxHeight", w -> w.getMaxHeight())
            .add("name", w -> w.getName())
            .add("pvp", w -> w.getPVP())
            .add("seaLevel", w -> w.getSeaLevel())
            .add("seed", w -> w.getSeed())
            .add("spawnLocation", w -> w.getSpawnLocation())
            .add("ticksPerAnimalSpawns", w -> w.getTicksPerAnimalSpawns())
            .add("ticksPerMonsterSpawns", w -> w.getTicksPerMonsterSpawns())
            .add("time", w -> w.getTime())
            .add("uid", w -> w.getUID())
            .add("waterAnimalSpawnLimit", w -> w.getWaterAnimalSpawnLimit())
            .add("weatherDuration", w -> w.getWeatherDuration())
            .add("worldBorder", w -> w.getWorldBorder())
            .add("worldFolder", w -> w.getWorldFolder())
            .add("worldType", w -> w.getWorldType())
            .add("storm", w -> w.hasStorm())
            .add("autoSave", w -> w.isAutoSave())
            .add("thundering", w -> w.isThundering())
            .self(w -> w.getName());

        r.type(WorldBorder.class)
            .add("center", wb -> wb.getCenter())
            .add("damageAmount", wb -> wb.getDamageAmount())
            .add("damageBuffer", wb -> wb.getDamageBuffer())
            .add("size", wb -> wb.getSize())
            .add("warningDistance", wb -> wb.getWarningDistance())
            .add("warningTime", wb -> wb.getWarningTime())
            .self(wb -> ((int) wb.getSize() / 2) + "x" + ((int) wb.getSize() / 2));
    }
}
