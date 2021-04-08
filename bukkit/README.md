# Okaeri Placeholders | Bukkit

This module includes bukkit specific mappings.

## Installation
### Maven
Add dependency to the `dependencies` section:
```xml
<dependency>
  <groupId>eu.okaeri</groupId>
  <artifactId>okaeri-placeholders-bukkit</artifactId>
  <version>1.2.0</version>
</dependency>
```
### Gradle
Add dependency to the `maven` section:
```groovy
implementation 'eu.okaeri:okaeri-placeholders-bukkit:1.2.0'
```

## Supported types

Note: It is most of the time (especially when description states that variable can be null)
recommended to add fallback value e.g. `{player.bedSpawnLocation|-}` to allow messages to be safely rendered.

### Player (`org.bukkit.entity.Player`)
| Field | Type | Description | Example |
|-|-|-|-|
| `address` | String | The IP address of the player. | `127.0.0.1` |
| `addressFull` | String | The socket address of the player. | `/127.0.0.1:35288` |
| `addressPort` | int | The socket port of the player. | `35288` |
| `allowFlight` | boolean | Determines if the Player is allowed to fly via jump key double-tap like in creative mode. | `true`/`false` |
| `bedSpawnLocation` | [Location](#Location) | Gets the Location where the player will spawn at their bed, null if they have not slept in one or their current bed spawn is invalid. | `null` / `*Location*` |
| `compassTarget` | [Location](#Location) | Get the previously set compass target. | - |
| `displayName` | String | Gets the "friendly" name to display of this player. | `SomePlayer` |
| `exhaustion` | float | Gets the players current exhaustion level. | `2.6910067` |
| `exp` | float | Gets the players current experience points towards the next level. | `0.2857143` |
| `flySpeed` | float | Gets the current allowed speed that a client can fly. | `0.1` |
| `foodLevel` | int | Gets the players current food level. | `14` |
| `healthScale` | double | Gets the number that health is scaled to for the client. | `20.00` |
| `level` | int | Gets the players current experience level | `0` |
| `playerListName` | String | Gets the name that is shown on the player list. | `SomePlayer` |
| `playerTime` | long | Returns the player's current timestamp. | `6148941` |
| `playerTimeOffset` | long | Returns the player's current time offset relative to server time, or the current player's fixed time if the player's time is absolute. | `0` |
| `weatherType` | WeatherType | Returns the type of weather the player is currently experiencing. | `CLEAR`, `DOWNFALL` |
| `saturation` | float | Gets the players current saturation level. | `0.0` |
| `spectatorTarget` | [Entity](#Entity) | Gets the entity which is followed by the camera when in GameMode.SPECTATOR. | `null` / `*Entity*`  |
| `totalExperience` | int | Gets the players total experience points | - |
| `walkSpeed` | float | Gets the current allowed speed that a client can walk. | - |
| `flying` | boolean | Checks to see if this player is currently flying or not. | `true`/`false` |
| `healthScaled` | boolean | Gets if the client is displayed a 'scaled' health, that is, health on a scale from 0-getHealthScale(). | `true`/`false` |
| `playerTimeRelative` | boolean | Returns true if the player's time is relative to the server time, otherwise the player's time is absolute and will not change its current time unless done so with setPlayerTime(). | `true`/`false` |
| `sleepingIgnored` | boolean | Returns whether the player is sleeping ignored. | `true`/`false` |
| `sneaking` | boolean | Returns if the player is in sneak mode | `true`/`false` |
| `sprinting` | boolean | Gets whether the player is sprinting or not. | `true`/`false` |
| *self* | String | The name of the player. | `SomePlayer` |
