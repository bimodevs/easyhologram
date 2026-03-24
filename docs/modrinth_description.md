# EasyHologram API

Lightweight server-side hologram library for Minecraft 1.21 (Fabric). Create floating text, blocks, and items using vanilla Display Entities — one line of code.

---

## Features

🔤 **Text Holograms** — floating text with color codes, shadows, backgrounds, line width control

🧱 **Block Holograms** — display any block floating in mid-air

⚔️ **Item Holograms** — floating items with billboard mode (always face the player)

🎬 **Animations** — built-in presets: gentle floating, rotation, pulsing scale

💾 **Auto-Save** — holograms persist across server restarts via PersistentState

📁 **JSON Export/Import** — manually edit hologram layouts with `/hologram save` and `/hologram load`

🔌 **Developer Events** — Fabric events for CREATE, REMOVE, TICK — other mods can react to hologram changes

🛠️ **Debug Commands** — full command suite for testing without writing code

---

## Commands

All commands require operator permissions (level 2).

| Command | What it does |
|---------|-------------|
| `/hologram create text <x> <y> <z> <text>` | Spawn a text hologram |
| `/hologram create block <x> <y> <z> <block>` | Spawn a block hologram |
| `/hologram create item <x> <y> <z> <item>` | Spawn an item hologram |
| `/hologram list` | Show all active holograms |
| `/hologram remove <id>` | Remove one hologram |
| `/hologram clear` | Remove all holograms |
| `/hologram save <name>` | Export to JSON |
| `/hologram load <name>` | Import from JSON |

---

## For Developers

Add EasyHologram as a dependency to programmatically create/manage holograms in your own mod.

### Gradle Setup

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation 'com.github.tabplus:easyhologram:1.0.0'
}
```

### Example: Create a Text Hologram

```java
TextHologram hologram = TextHologram.builder(new Vec3d(100, 65, 200))
    .text("§6Welcome!")
    .billboard(true)
    .shadow(true)
    .build(world);

EasyHologram.getManager().register(hologram);
```

### Example: Listen to Events

```java
HologramEvents.AFTER_CREATE.register(hologram -> {
    LOGGER.info("Hologram created: {}", hologram.getId());
});
```

---

## Requirements

- Minecraft **1.21**
- Fabric Loader **≥ 0.16.0**
- Fabric API

---

## License

[MIT](https://opensource.org/licenses/MIT)
