# Okaeri Placeholders | Bukkit

This module includes bukkit specific mappings.

## Installation

### Maven

Add dependency to the `dependencies` section:
```xml
<dependency>
  <groupId>eu.okaeri</groupId>
  <artifactId>okaeri-placeholders-bukkit</artifactId>
  <version>5.1.0</version>
</dependency>
```

### Gradle

Add dependency to the `maven` section:
```groovy
implementation 'eu.okaeri:okaeri-placeholders-bukkit:5.1.0'
```

## Supported types

- [ChatColor](#chatcolor-orgbukkitchatcolor)
- [CommandSender](#humanentity-orgbukkitcommandcommandsender)
- [HumanEntity](#humanentity-orgbukkitentityhumanentity)
- [Inventory](#inventory-orgbukkitinventoryinventory)
- [InventoryView](#inventoryview-orgbukkitinventoryinventoryview)
- [PlayerInventory](#playerinventory-orgbukkitinventoryplayerinventory)
- [OfflinePlayer](#offlineplayer-orgbukkitofflineplayer)
- [Player](#player-orgbukkitentityplayer)
- [Entity](#entity-orgbukkitentityentity)
- [Location](#location-orgbukkitlocation)

Note: It is most of the time (especially when description states that variable can be null)
recommended to add fallback value e.g. `{player.bedSpawnLocation|-}` to allow messages to be safely rendered.

### ChatColor (`org.bukkit.ChatColor`)

| Field | Type | Description | Example |
|-|-|-|-|
| *self* | String | The text embeddable color. | `§f` |

### CommandSender (`org.bukkit.command.CommandSender`)

| Field | Type | Description | Example |
|-|-|-|-|
| `name` | String | Gets the name of this command sender | `CONSOLE` |
| *self* | String | The name of the sender. | `CONSOLE` |

### HumanEntity (`org.bukkit.entity.HumanEntity`)

| Field | Type | Description | Example |
|-|-|-|-|
| `enderChest` | [Inventory](#inventory-orgbukkitinventoryinventory) | Get the player's EnderChest inventory. | `*Inventory*` |
| `expToLevel` | int | Get the total amount of experience required for the player to level. | `7` |
| `gameMode` | GameMode | Gets this human's current GameMode. | `CREATIVE`/`SURVIVAL`/.. |
| `inventory` | [Inventory](#inventory-orgbukkitinventoryinventory) | Get the player's inventory. | `*Inventory*` |
| `itemInHand` | [ItemStack](#ItemStack) | Returns the ItemStack currently in your hand, can be empty. | `null`/`*ItemStack*` |
| `itemOnCursor` | [ItemStack](#ItemStack) | Returns the ItemStack currently on your cursor, can be empty. | `null`/`*ItemStack*` |
| `name` | String | Returns the name of this player. | `SomePlayer` |
| `openInventory` | [InventoryView](#inventoryview-orgbukkitinventoryinventoryview) | Gets the inventory view the player is currently viewing. | `*InventoryView*` |
| `sleepTicks` | int | Get the sleep ticks of the player. | `0` |
| `blocking` | boolean | Check if the player is currently blocking (ie with a sword). | `true`/`false` |
| `sleeping` | boolean | Returns whether this player is slumbering. | `true`/`false` |
| *self* | String | The name of the entity. | `SomePlayer` |

### Inventory (`org.bukkit.inventory.Inventory`)

| Field | Type | Description | Example |
|-|-|-|-|
| `name` | String | Returns the name of the inventory. | `container.inventory` |
| `size` | int | Returns the size of the inventory. | `41` |
| `title` | String | Returns the title of this inventory. | `container.inventory` |
| `type` | InventoryType | Returns what type of inventory this is. | `PLAYER`/.. |
| *self*  | String | The name of the inventory. | `container.inventory` |

### InventoryView (`org.bukkit.inventory.InventoryView`)

| Field | Type | Description | Example |
|-|-|-|-|
| `bottomInventory` | [Inventory](#inventory-orgbukkitinventoryinventory) | Get the lower inventory involved in this transaction. | `*Inventory*` |
| `cursor` | [ItemStack](#ItemStack) | Get the item on the cursor of one of the viewing players. | `null`/`*ItemStack*` |
| `player` | [HumanEntity](#humanentity-orgbukkitentityhumanentity) | Get the player viewing. | `*Player*` |
| `title` | String | Get the title of this inventory window. | `container.crafting` |
| `topInventory` | [Inventory](#inventory-orgbukkitinventoryinventory) | Get the upper inventory involved in this transaction. | `*Inventory*` |
| `type` | InventoryType | Determine the type of inventory involved in the transaction. | `CREATIVE`/`CRAFTING`/.. |

### PlayerInventory (`org.bukkit.inventory.PlayerInventory`)

| Field | Type | Description | Example |
|-|-|-|-|
| `boots` | [ItemStack](#ItemStack) | Return the ItemStack from the boots slot. | `null`/`*ItemStack*` |
| `chestplate` | [ItemStack](#ItemStack) | Return the ItemStack from the chestplate slot. | `null`/`*ItemStack*` |
| `heldItemSlot` | int | Get the slot number of the currently held item. | `4` |
| `helmet` | [ItemStack](#ItemStack) | Return the ItemStack from the helmet slot. | `null`/`*ItemStack*` |
| `holder` | [HumanEntity](#humanentity-orgbukkitentityhumanentity) | Gets the block or entity belonging to the open inventory. | `*HumanEntity*` |
| `itemInHand` | [ItemStack](#ItemStack) | Returns the ItemStack currently hold. | `null`/`*ItemStack*` |
| `leggings` | [ItemStack](#ItemStack) | Return the ItemStack from the leg slot. | `null`/`*ItemStack*` |

### OfflinePlayer (`org.bukkit.OfflinePlayer`)

| Field | Type | Description | Example |
|-|-|-|-|
| `bedSpawnLocation` | [Location](#location-orgbukkitlocation) | Gets the Location where the player will spawn at their bed, null if they have not slept in one or their current bed spawn is invalid. | `null`/`*Location*` |
| `firstPlayed` | long | Gets the first date and time that this player was witnessed on this server. | `1616478979960` |
| `lastPlayed` | long | Gets the last date and time that this player was witnessed on this server. | `1617925100501` |
| `name` | String | Returns the name of this player. | `SomePlayer` |
| `uniqueId` | UUID | Returns the UUID of this player. | `fc286c60-8187-4725-b1e9-580f447cc391` |
| `playedBefore` | boolean | Checks if this player has played on this server before. | `true`/`false` |
| `banned` | boolean | Checks if this player is banned or not. | `true`/`false` |
| `online` | boolean | Checks if this player is currently online. | `true`/`false` |
| `whitelisted` | boolean | Checks if this player is whitelisted or not. | `true`/`false` |
| *self*  | String | The name of the player. | `SomePlayer` |

### Player (`org.bukkit.entity.Player`)

| Field | Type | Description | Example |
|-|-|-|-|
| `address` | String | The IP address of the player. | `127.0.0.1` |
| `addressFull` | String | The socket address of the player. | `/127.0.0.1:35288` |
| `addressPort` | int | The socket port of the player. | `35288` |
| `allowFlight` | boolean | Determines if the Player is allowed to fly via jump key double-tap like in creative mode. | `true`/`false` |
| `bedSpawnLocation` | [Location](#location-orgbukkitlocation) | Gets the Location where the player will spawn at their bed, null if they have not slept in one or their current bed spawn is invalid. | `null`/`*Location*` |
| `compassTarget` | [Location](#location-orgbukkitlocation) | Get the previously set compass target. | `*Location*` |
| `displayName` | String | Gets the "friendly" name to display of this player. | `SomePlayer` |
| `exhaustion` | float | Gets the players current exhaustion level. | `2.69` |
| `exp` | float | Gets the players current experience points towards the next level. | `0.28` |
| `flySpeed` | float | Gets the current allowed speed that a client can fly. | `0.10` |
| `foodLevel` | int | Gets the players current food level. | `14` |
| `healthScale` | double | Gets the number that health is scaled to for the client. | `20.00` |
| `level` | int | Gets the players current experience level | `0` |
| `playerListName` | String | Gets the name that is shown on the player list. | `SomePlayer` |
| `playerTime` | long | Returns the player's current timestamp. | `6148941` |
| `playerTimeOffset` | long | Returns the player's current time offset relative to server time, or the current player's fixed time if the player's time is absolute. | `0` |
| `weatherType` | WeatherType | Returns the type of weather the player is currently experiencing. | `CLEAR`/`DOWNFALL` |
| `saturation` | float | Gets the players current saturation level. | `0.00` |
| `spectatorTarget` | [Entity](#entity-orgbukkitentityentity) | Gets the entity which is followed by the camera when in GameMode.SPECTATOR. | `null`/`*Entity*`  |
| `totalExperience` | int | Gets the players total experience points | `2` |
| `walkSpeed` | float | Gets the current allowed speed that a client can walk. | `0.20` |
| `flying` | boolean | Checks to see if this player is currently flying or not. | `true`/`false` |
| `healthScaled` | boolean | Gets if the client is displayed a 'scaled' health, that is, health on a scale from 0-getHealthScale(). | `true`/`false` |
| `playerTimeRelative` | boolean | Returns true if the player's time is relative to the server time, otherwise the player's time is absolute and will not change its current time unless done so with setPlayerTime(). | `true`/`false` |
| `sleepingIgnored` | boolean | Returns whether the player is sleeping ignored. | `true`/`false` |
| `sneaking` | boolean | Returns if the player is in sneak mode | `true`/`false` |
| `sprinting` | boolean | Gets whether the player is sprinting or not. | `true`/`false` |
| *self* | String | The name of the player. | `SomePlayer` |

### Entity (`org.bukkit.entity.Entity`)

| Field | Type | Description | Example |
|-|-|-|-|
| `customName` | String | Gets the custom name on a mob. | `SomePlayers's dog` |
| `entityId` | int | Returns a unique id for this entity. | `47` |
| `fallDistance` | float | Returns the distance this entity has fallen. | `0.00` |
| `fireTicks` | int | Returns the entity's current fire ticks (ticks before the entity stops being on fire). | `-20` |
| `lastDamageCause` | [EntityDamageEvent](#EntityDamageEvent) | Retrieve the last EntityDamageEvent inflicted on this entity. | `null`/`*EntityDamageEvent*` |
| `location` | [Location](#location-orgbukkitlocation) | Gets the entity's current position. | `*Location*` |
| `maxFireTicks` | int | Returns the entity's maximum fire ticks. | `20` |
| `passenger` | [Entity](#entity-orgbukkitentityentity) | Gets the primary passenger of a vehicle. | `null`/`*Entity*` |
| `ticksLived` | int | Gets the amount of ticks this entity has lived for. | `2041389` |
| `type` | EntityType | Get the type of the entity. | `PLAYER`/`ZOMBIE`/.. |
| `uniqueId` | UUID | Returns a unique and persistent id for this entity. | `fc286c60-8187-4725-b1e9-580f447cc391` |
| `vehicle` | [Entity](#entity-orgbukkitentityentity) | Get the vehicle that this player is inside. | `null`/`*Entity*` |
| `velocity` | [Vector](#Vector) | Gets this entity's current velocity. | `*Vector*` |
| `world` | [World](#World) | Gets the current world this entity resides in. | `*World*` |
| `customNameVisible` | boolean | Gets whether or not the mob's custom name is displayed client side. | `true`/`false` |
| `dead` | boolean | Returns true if this entity has been marked for removal. | `true`/`false` |
| `empty` | boolean | Check if a vehicle has passengers. | `true`/`false` |
| `insideVehicle` | boolean | Returns whether this entity is inside a vehicle. | `true`/`false` |
| `onGround` | boolean | Returns true if the entity is supported by a block. | `true`/`false` |
| `valid` | boolean | Returns false if the entity has died or been despawned for some other reason. | `true`/`false` |
| *self* | String | The name of the type of the entity. | `PLAYER`/.. |

### Location (`org.bukkit.Location`)

| Field | Type | Description | Example |
|-|-|-|-|
| `block` | [Block](#Block) | Gets the block at the represented location. | `*Block*` |
| `blockX` | int | Gets the floored value of the X component, indicating the block that this location is contained with. | `4` |
| `blockY` | int | Gets the floored value of the Y component, indicating the block that this location is contained with. | `79` |
| `blockZ` | int | Gets the floored value of the Z component, indicating the block that this location is contained with. | `418` |
| `chunk` | [Chunk](#Chunk) | Gets the chunk at the represented location. | `*Chunk*` |
| `direction` | [Vector](#Vector) | Gets a unit-vector pointing in the direction that this Location is facing. | `*Vector*` |
| `pitch` | float | Gets the pitch of this location, measured in degrees. | `12.14` |
| `world` | [World](#World) | Gets the world that this location resides in. | `*World*` |
| `x` | double | Gets the x-coordinate of this location. | `4.10` |
| `y` | double | Gets the y-coordinate of this location. | `79.00` |
| `yaw` | float | Gets the yaw of this location, measured in degrees. | `116.09` |
| `z` | float | Gets the z-coordinate of this location. | `418.77` |
| `length` | float | Gets the magnitude of the location, defined as sqrt(x^2+y^2+z^2). | `426.18` |
| `lengthSquared` | float | Gets the magnitude of the location squared. | `181625.77` |
| *self* | String | Representation of the basic location parameters as string. | `(world=world_nether, x=4.1, y=79.0, z=418.7)` |
