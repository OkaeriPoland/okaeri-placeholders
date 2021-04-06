# Okaeri Placeholders (okaeri-placeholders)

![License](https://img.shields.io/github/license/OkaeriPoland/okaeri-placeholders)
![Total lines](https://img.shields.io/tokei/lines/github/OkaeriPoland/okaeri-placeholders)
![Repo size](https://img.shields.io/github/repo-size/OkaeriPoland/okaeri-placeholders)
![Contributors](https://img.shields.io/github/contributors/OkaeriPoland/okaeri-placeholders)
[![Discord](https://img.shields.io/discord/589089838200913930)](https://discord.gg/hASN5eX)

Simple blazing-fast placeholders library with yet unlimited possibilities. Part of the [okaeri-platform](https://github.com/OkaeriPoland/okaeri-platform).

## Installation
### Maven
Add repository to the `repositories` section:
```xml
<repository>
    <id>okaeri-repo</id>
    <url>https://storehouse.okaeri.eu/repository/maven-public/</url>
</repository>
```
Add dependency to the `dependencies` section:
```xml
<dependency>
  <groupId>eu.okaeri</groupId>
  <artifactId>okaeri-placeholders</artifactId>
  <version>1.0.0</version>
</dependency>
```
### Gradle
Add repository to the `repositories` section:
```groovy
maven { url "https://storehouse.okaeri.eu/repository/maven-public/" }
```
Add dependency to the `maven` section:
```groovy
implementation 'eu.okaeri:okaeri-placeholders:1.0.0'
```

## Example
```java
// this is intended to be loaded from the configuration on the startup/cached and stored compiled
CompiledMessage message = CompiledMessage.of("Hola {who}! ¿Cómo estás {when}? Estoy {how}.");

// context can be cached or created on demand
PlaceholderContext context = PlaceholderContext.create()
    .with("who", "Mundo") // in real life scenario these would be your variables
    .with("when", "hoy")
    .with("how", "bien");

// process message and get output: Hola Mundo! ¿Cómo estás hoy? Estoy bien.
String test = context.apply(message);
```
