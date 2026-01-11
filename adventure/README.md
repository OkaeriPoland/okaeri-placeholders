# Okaeri Placeholders Adventure

Adventure integration for okaeri-placeholders with full MiniMessage support.

## Installation

### Maven

```xml
<dependency>
    <groupId>eu.okaeri</groupId>
    <artifactId>okaeri-placeholders-adventure</artifactId>
    <version>5.1.2</version>
</dependency>
```

### Gradle

```groovy
implementation 'eu.okaeri:okaeri-placeholders-adventure:5.1.2'
```

## Features

- Full MiniMessage tag support (`<red>`, `<bold>`, `<gradient:red:blue>`, etc.)
- Legacy color code support (`§c`, `&6`)
- Style inheritance for string placeholders
- Component preservation for Component placeholders
- Click/hover event placeholder replacement

## Usage

### Basic Usage

```java
Placeholders placeholders = Placeholders.create();
AdventureMessageRenderer renderer = new AdventureMessageRenderer();

CompiledMessage message = CompiledMessage.of("<gold>Hello {name}!</gold>");
Component result = placeholders.context(message)
    .with("name", "World")
    .apply(renderer);
// Result: gold-colored "Hello World!"
```

### Style Inheritance

String placeholder values inherit surrounding styles:

```java
// Gradient applies to the entire text including placeholder value
CompiledMessage message = CompiledMessage.of("<gradient:red:blue>Welcome {player}!</gradient>");
Component result = placeholders.context(message)
    .with("player", "Steve")
    .apply(renderer);
// Result: "Welcome Steve!" with gradient from red to blue across all text
```

### Component Preservation

Component placeholder values preserve their own styling:

```java
Component styledName = Component.text("Admin")
    .color(NamedTextColor.GOLD)
    .decorate(TextDecoration.BOLD);

CompiledMessage message = CompiledMessage.of("<red>Error reported by {reporter}</red>");
Component result = placeholders.context(message)
    .with("reporter", styledName)
    .apply(renderer);
// Result: red "Error reported by " + gold bold "Admin"
```

### Click Events

Placeholders inside click events are replaced automatically:

```java
CompiledMessage message = CompiledMessage.of(
    "<click:run_command:'/give {player} diamond'>Click for diamond!</click>"
);
Component result = placeholders.context(message)
    .with("player", "Steve")
    .apply(renderer);
// Result: clickable text that runs "/give Steve diamond"
```

### Legacy Color Codes

Both `§` and `&` color codes are supported alongside MiniMessage:

```java
// Section codes
CompiledMessage message = CompiledMessage.of("§cRed §aGreen {name}");

// Ampersand codes
CompiledMessage message = CompiledMessage.of("&6Gold &lBold {name}");

// Mixed with MiniMessage
CompiledMessage message = CompiledMessage.of("&6Gold <bold>{name}</bold>");
```

### Using Resolvers

All okaeri-placeholders resolvers work with AdventureMessageRenderer:

```java
CompiledMessage message = CompiledMessage.of("<gold>{name.toUpperCase}</gold>");
Component result = placeholders.context(message)
    .with("name", "steve")
    .apply(renderer);
// Result: gold "STEVE"

// With .or() fallback
CompiledMessage message = CompiledMessage.of("Hello {name.or(\"Guest\")}!");
Component result = placeholders.context(message)
    .apply(renderer);
// Result: "Hello Guest!"
```

### Custom MiniMessage Instance

You can provide a custom MiniMessage instance:

```java
MiniMessage customMiniMessage = MiniMessage.builder()
    .strict(true)
    .build();

AdventureMessageRenderer renderer = new AdventureMessageRenderer(customMiniMessage);
```

The default MiniMessage instance supports both legacy codes and MiniMessage tags via pre/post processors.

## How It Works

1. **Text placeholders** use MiniMessage's TagResolver for native integration - string values inherit surrounding styles (gradients, colors, decorations)

2. **Component placeholders** are inserted with their own styling preserved via `Tag.selfClosingInserting()`

3. **Event placeholders** (click/hover) are replaced via post-processing since MiniMessage doesn't resolve tags inside event attribute values
