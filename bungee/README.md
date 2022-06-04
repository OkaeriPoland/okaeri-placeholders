# Okaeri Placeholders | Bungee

This module includes bungee specific mappings.

## Installation

### Maven

Add dependency to the `dependencies` section:
```xml
<dependency>
  <groupId>eu.okaeri</groupId>
  <artifactId>okaeri-placeholders-bungee</artifactId>
  <version>3.0.4</version>
</dependency>
```

### Gradle

Add dependency to the `maven` section:
```groovy
implementation 'eu.okaeri:okaeri-placeholders-bungee:3.0.4'
```

Note: It is most of the time (especially when description states that variable can be null)
recommended to add fallback value e.g. `{player.uniqueId|-}` to allow messages to be safely rendered.

## Supported types

- [Chatcolor](#chatcolor-netmd_5bungeeapichatcolor)
- [CommandSender](#commandsender-netmd_5bungeeapicommandsender)
- [ProxyServer](#proxyserver-netmd_5bungeeapiproxyserver)
- [ProxiedPlayer](#proxiedplayer-netmd_5bungeeapiconnectionproxiedplayer)
- [Server](#server-netmd_5bungeeapiconnectionserver)

### ChatColor (`net.md_5.bungee.api.ChatColor`)

| Field | Type | Description | Example |
|-|-|-|-|
| *self* | String | The text embeddable color. | `§f` |

### CommandSender (`net.md_5.bungee.api.CommandSender`)

| Field | Type | Description | Example |
|-|-|-|-|
| `name` | String | Get the unique name of this command sender. | `SomePlayer` |
| *self* | String | The name of the sender. | `SomePlayer` |

### ProxyServer (`net.md_5.bungee.api.ProxyServer`)

| Field | Type | Description | Example |
|-|-|-|-|
| `name` | String | Gets the name of the currently running proxy software. | ? |
| `onlineCount` | int | Get the current number of connected users. | `21` |
| *self* | String | The name of the server. | `SomePlayer` |

### ProxiedPlayer (`net.md_5.bungee.api.connection.ProxiedPlayer`)

| Field | Type | Description | Example |
|-|-|-|-|
| `displayName` | String | Gets this player's display name. | `SomePlayer` |
| `ping` | int | Gets the ping time between the proxy and this connection. | `33` |
| `server` | [Server](#server-netmd_5bungeeapiconnectionserver) | Gets the server this player is connected to. | `*ProxyServer*` |
| `uniqueId` | UUID | Get this connection's UUID, if set. | `null`/`UUID` |
| `locale` | Locale | Gets this player's locale. | `en_GB` |
| `viewDistance` | Locale | Gets this player's view distance. | `8` |
| `chatMode` | Enum | Gets this player's chat mode. | `SHOWN` |

### Server (`net.md_5.bungee.api.connection.Server`)

| Field | Type | Description | Example |
|-|-|-|-|
| `name` | String | The name of the server. | `skyblock` |
| `motd` | String | Returns the MOTD which should be used when this server is a forced host. | `Just another BungeeCord - Forced Host` |
| `address` | SocketAddress | Gets the connectable address for this server. Implementations expect this to be used as the unique identifier per each instance of this class. | `127.0.0.1:25564` |
| `permission` | String | Get the permission required to access this server. Only enforced when the server is restricted. | `some.permission` |
| `playersCount` | int | Get the count of players on the server. | `12` |
| *self* | String | The name of the server. | `skyblock` |
