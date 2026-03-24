# EasyHologram API

**Lightweight server-side hologram library for Minecraft 1.21 (Fabric)**

Create floating text, blocks, and items in the world with a single line of code. Built on vanilla Display Entities — no packets, no hacks, just clean API.

---

## ✨ Features

- **Text Holograms** — floating text with colors, shadows, custom backgrounds
- **Block Holograms** — any block displayed in mid-air
- **Item Holograms** — floating items with billboard mode
- **Animations** — built-in presets: float, rotate, pulse
- **Persistence** — holograms survive server restarts automatically
- **JSON Export/Import** — edit hologram layouts by hand
- **Events** — other mods can react to hologram create/remove/tick
- **Debug Commands** — test everything in-game without writing code

## 📦 For Developers

### Dependency Setup

Add EasyHologram to your project via [JitPack](https://jitpack.io):

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation 'com.github.tabplus:easyhologram:1.0.0'
}
```

### Quick Start

```java
// Text hologram
TextHologram hologram = TextHologram.builder(new Vec3d(100, 65, 200))
    .text("§6Welcome to the server!")
    .scale(1.5f)
    .billboard(true)
    .shadow(true)
    .build(world);

EasyHologram.getManager().register(hologram);

// Block hologram
BlockHologram block = BlockHologram.builder(pos)
    .block(Blocks.DIAMOND_BLOCK)
    .build(world);

EasyHologram.getManager().register(block);

// Item hologram with animation
ItemHologram item = ItemHologram.builder(pos)
    .item(Items.NETHERITE_SWORD)
    .billboard(true)
    .build(world);

item.setAnimation(HologramAnimation.ROTATE);
EasyHologram.getManager().register(item);
```

### Events

```java
HologramEvents.AFTER_CREATE.register(hologram -> {
    System.out.println("New hologram: " + hologram.getId());
});
```

### Updating Text

```java
Hologram h = EasyHologram.getManager().get("my_hologram");
if (h instanceof TextHologram text) {
    text.setText("§aOnline: " + server.getCurrentPlayerCount());
}
```

## 🔧 Commands

All commands require OP level 2.

| Command | Description |
|---------|-------------|
| `/hologram create text <x> <y> <z> <text>` | Create a text hologram |
| `/hologram create block <x> <y> <z> <block>` | Create a block hologram |
| `/hologram create item <x> <y> <z> <item>` | Create an item hologram |
| `/hologram list` | List all holograms |
| `/hologram remove <id>` | Remove a hologram |
| `/hologram clear` | Remove all holograms |
| `/hologram save <name>` | Export to JSON file |
| `/hologram load <name>` | Import from JSON file |

## 📋 Requirements

- Minecraft 1.21
- Fabric Loader ≥ 0.16.0
- Fabric API

## 📄 License

MIT — use it however you want.
