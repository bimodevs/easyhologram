<h1 align="center">EasyHologram API</h1>
<p align="center"><strong>The Definitive Server-Side Hologram Framework for Minecraft 1.21 (Fabric)</strong></p>

EasyHologram leverages vanilla **Display Entities** to deliver a robust, strictly packet-free, and highly performant API for rendering holograms. Built on pure modern Minecraft architecture.

---

## ✨ Core Architecture

- **Rich Typography & Objects** — Native support for scalable text, customized RGB backgrounds, dynamic drop shadows, and 3D floating blocks/items.
- **Interactive & Dynamic** — Attach functional left/right click listeners. Full integration with **PlaceholderAPI** for live-updating server statistics.
- **Multi-Line Topologies** — Built-in logical wrapper to configure perfectly aligned, vertically stacked text arrays with absolute precision.
- **Granular Visibility** — Programmatic per-player visibility matrices. Selectively render, intercept, or hide holograms dynamically.
- **Fluid Interpolation** — Unleash 1.21 matrix interpolation limits for buttery-smooth position, scaling, and rotational transitions.
- **Persistence Engine** — Fault-tolerant auto-saving mechanism via Minecraft's `PersistentState`. Endures server execution cycles and hard restarts safely.

---

## 🔧 Operational Commands
Execute layout management strictly within the server environment. *(Requires OP Level 2 configuration)*

| Syntax Parameter | Invocation Target |
| :--- | :--- |
| `/hologram create text <p> <txt>` | Instantiates a textual hologram object. |
| `/hologram create block <p> <b>` | Instantiates a 3D block geometry. |
| `/hologram create item <p> <i>` | Instantiates a billboard-attached item. |
| `/hologram edit <id> settext <txt>` | Mutates string layout configurations live. |
| `/hologram list` | Audits tracked holograms loaded in heap memory. |
| `/hologram remove <id>` | Safely triggers a synchronized deallocation. |
| `/hologram save <n>` / `load <n>` | Serializes/Deserializes JSON layout schemas. |

---

## 📦 Developer Integration

Integrate the engine into your project environments strictly via **JitPack**.

### Grade Configuration
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation 'com.github.tabplus:easyhologram:1.0.0'
}
```

### Technical Implementation

```java
// 1. Initializing & Registering
TextHologram target = TextHologram.builder(new Vec3d(100, 65, 200))
    .id("sys_welcome")
    .text("Main Application Server")
    .scale(1.5f)
    .shadow(true)
    .build(world);

EasyHologram.getManager().register(target);

// 2. Click Interception Hook
target.setClickListener((player, type) -> {
    player.sendMessage(Text.literal("Hologram interaction packet received."), false);
});

// 3. Modifying Visibility Matrix
target.addViewer(player.getUuid()); // Entity becomes absolutely invisible to all other generic sessions
```

### Lifecycle Event Listeners
Hook into the framework internals securely to track entity creation and manipulation logic:
```java
HologramEvents.AFTER_CREATE.register(hologram -> {
    System.out.println("Heap Allocation Completed for ID: " + hologram.getId());
});
```

---

## 📋 System Requirements
- **Runtime Environment:** Minecraft 1.21
- **Subsystem Dependency:** Fabric Loader ≥ 0.16.0
- **Library Module:** Fabric API

## 📄 Licensing Architecture
Released under the permissive **MIT License**. Unrestricted integration is permitted.
