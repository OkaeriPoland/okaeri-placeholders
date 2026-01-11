# Okaeri Placeholders Adventure

Adventure integration for okaeri-placeholders with full MiniMessage support.

## Installation

### Maven

```xml
<dependency>
    <groupId>eu.okaeri</groupId>
    <artifactId>okaeri-placeholders-adventure</artifactId>
    <version>6.0.0-beta.1</version>
</dependency>
```

### Gradle

```groovy
implementation 'eu.okaeri:okaeri-placeholders-adventure:6.0.0-beta.1'
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

### Dynamic Styling with color()

Static tags like `<red>{name}</red>` work fine - the tags are parsed from template text. But when the **tag itself** needs to be dynamic (e.g., color based on a condition), use `AdventurePack.color()`:

```java
Placeholders placeholders = Placeholders.create()
    .with(new AdventurePack());
AdventureMessageRenderer renderer = new AdventureMessageRenderer();

// Dynamic gradient - the colors change based on health value
CompiledMessage message = CompiledMessage.of(
    "{color(\"<gradient:\",cond(health.gt(66),\"green:dark_green\",health.gt(33),\"yellow:gold\",\"red:dark_red\"),\">\",health,\"% HP</gradient>\")}"
);

Component result = placeholders.context(message)
    .with("health", 75)
    .apply(renderer);
// Result: "75% HP" with green gradient (health > 66)
```

The `color()` function concatenates all arguments and parses through MiniMessage, allowing you to build tags dynamically.

## Security

**Context values are safe from injection.** Color codes (`&`, `§`) and MiniMessage tags in user-provided values are NOT parsed:

```java
// User input with attempted injection
context.with("name", "&cEvil<red>Hacker");

CompiledMessage message = CompiledMessage.of("<gold>Hello {name}!</gold>");
Component result = context.apply(renderer);
// Result: gold "Hello &cEvil<red>Hacker!" (injection attempt shown as literal text)
```

Only template text and string literals in expressions (like `cond(x,"&a","&c")`) are processed for color codes.

## How It Works

1. **Text placeholders** use MiniMessage's TagResolver for native integration - string values inherit surrounding styles (gradients, colors, decorations)

2. **Component placeholders** are inserted with their own styling preserved via `Tag.selfClosingInserting()`

3. **Event placeholders** (click/hover) are replaced via post-processing since MiniMessage doesn't resolve tags inside event attribute values
