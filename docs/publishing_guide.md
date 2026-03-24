# Полная инструкция по публикации библиотеки EasyHologram

Эта инструкция описывает, как опубликовать библиотечный мод на **GitHub**, **Modrinth**, **CurseForge** и настроить **JitPack** (чтобы другие моддеры могли подключить твою библиотеку через Gradle).

---

## 1. GitHub

### 1.1 Создание репозитория

1. Зайди на [github.com/new](https://github.com/new)
2. Заполни:
   - **Repository name:** `easyhologram`
   - **Description:** `Lightweight server-side hologram library for Minecraft 1.21 (Fabric)`
   - **Visibility:** Public (обязательно для JitPack!)
   - **License:** MIT
   - **Add .gitignore:** выбери `Gradle`
3. Нажми **Create repository**

### 1.2 Загрузка кода

```bash
cd c:\Users\gamin\Downloads\tabplus
git init
git remote add origin https://github.com/ТВОЙ_НИК/easyhologram.git
git add .
git commit -m "Initial release v1.0.0"
git push -u origin main
```

### 1.3 README

Скопируй содержимое файла `docs/github_readme.md` в файл `README.md` в корне репозитория.

### 1.4 Создание релиза (обязательно для JitPack!)

1. На странице репозитория → **Releases** → **Create a new release**
2. **Tag version:** `1.0.0` (нажми «Create new tag»)
3. **Release title:** `EasyHologram 1.0.0`
4. **Description:** скопируй из `docs/github_readme.md` секцию Features
5. **Attach binary:** загрузи файл `build/libs/easyhologram-1.0.0.jar`
6. Нажми **Publish release**

---

## 2. JitPack (Maven-репозиторий для разработчиков)

JitPack автоматически собирает твой мод из GitHub и делает его доступным как Maven-зависимость. **Тебе не нужно ничего настраивать** — просто:

1. Зайди на [jitpack.io](https://jitpack.io)
2. Вставь URL репозитория: `https://github.com/ТВОЙ_НИК/easyhologram`
3. Нажми **Look up**
4. Увидишь список версий (тегов) — нажми **Get it** напротив `1.0.0`
5. JitPack покажет инструкцию для Gradle — это и есть то, что ты даешь другим разработчикам:

```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation 'com.github.ТВОЙ_НИК:easyhologram:1.0.0'
}
```

> **Важно:** Первая сборка на JitPack может занять 2-5 минут. Если она провалилась — проверь логи на jitpack.io.

---

## 3. Modrinth

### 3.1 Создание проекта

1. Зайди на [modrinth.com/dashboard/projects](https://modrinth.com/dashboard/projects)
2. Нажми **Create a project**
3. Заполни форму:

| Поле | Что писать |
|------|-----------|
| **Project type** | `Mod` |
| **Name** | `EasyHologram API` |
| **Slug** | `easyhologram` (URL будет modrinth.com/mod/easyhologram) |
| **Summary** | `Lightweight server-side hologram library using vanilla Display Entities` |
| **Categories** | `Library`, `Utility` |
| **Client/Server** | Server side: **Required**, Client side: **Unsupported** (это серверная библиотека) |
| **License** | `MIT` |
| **Source code** | `https://github.com/ТВОЙ_НИК/easyhologram` |
| **Issue tracker** | `https://github.com/ТВОЙ_НИК/easyhologram/issues` |
| **Discord** | (твой Discord-сервер, если есть) |

4. **Description:** скопируй содержимое `docs/modrinth_description.md`
5. Нажми **Create**

### 3.2 Загрузка версии

1. В проекте → **Versions** → **Create a version**
2. Заполни:

| Поле | Что писать |
|------|-----------|
| **Version number** | `1.0.0` |
| **Version title** | `EasyHologram 1.0.0` |
| **Release channel** | `Release` |
| **Loaders** | `Fabric` |
| **Game versions** | `1.21` |
| **Dependencies** | Добавь: `Fabric API` → тип `Required` |

3. **Upload files:** загрузи `build/libs/easyhologram-1.0.0.jar`
4. **Changelog:**
```
Initial release!
- Text, Block, and Item holograms
- Builder pattern API
- Built-in animations (float, rotate, pulse)
- Auto-save / persistence
- JSON export/import
- Debug commands (/hologram)
- Fabric Events for mod interop
```
5. Нажми **Publish**

### 3.3 Что писать в полях Summary и Description

- **Summary** (короткая строка, видно в поиске):
  > `Lightweight server-side hologram library using vanilla Display Entities`

- **Description** (полная страница мода): используй файл `docs/modrinth_description.md`

---

## 4. CurseForge

### 4.1 Создание проекта

1. Зайди на [authors.curseforge.com](https://authors.curseforge.com)
2. Нажми **Create a Project**
3. Заполни:

| Поле | Что писать |
|------|-----------|
| **Project Type** | `Bukkit / Spigot → нет! Выбери: Mods` |
| **Game** | `Minecraft: Java Edition` |
| **Project Name** | `EasyHologram API` |
| **Project Slug** | `easyhologram` |
| **Summary** | `Lightweight server-side hologram library using vanilla Display Entities for Fabric 1.21` |
| **Description** | Скопируй содержимое `docs/curseforge_description.md` (это HTML) |
| **Categories** | `API and Library` |
| **License** | `MIT` |
| **Project URL** | `https://github.com/ТВОЙ_НИК/easyhologram` |

4. Нажми **Create Project**

### 4.2 Загрузка файла

1. В проекте → **Files** → **Upload File**
2. Загрузи `build/libs/easyhologram-1.0.0.jar`
3. Заполни:

| Поле | Что писать |
|------|-----------|
| **File Display Name** | `EasyHologram-1.0.0.jar` |
| **Game Version** | `1.21` |
| **Mod Loader** | `Fabric` |
| **Release Type** | `Release` |
| **Dependencies** | Добавь: `Fabric API` → тип `Required` |

4. **Changelog:** такой же, как для Modrinth (см. выше)
5. Нажми **Submit**

### 4.3 Важные отличия CurseForge от Modrinth

- CurseForge использует **HTML** в описании, а не Markdown
- Модерация CurseForge занимает **1-3 дня** (Modrinth обычно быстрее)
- На CurseForge нужно указать **Environment** (Client/Server) в настройках проекта

---

## 5. Как другие моддеры подключают твою библиотеку

Когда ты опубликуешь библиотеку, другие разработчики добавят в свои проекты:

### Через JitPack (рекомендуется):
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    modImplementation 'com.github.ТВОЙ_НИК:easyhologram:1.0.0'
}
```

### Через Modrinth Maven:
```gradle
repositories {
    maven { url 'https://api.modrinth.com/maven' }
}

dependencies {
    modImplementation 'maven.modrinth:easyhologram:1.0.0'
}
```

### В fabric.mod.json зависимого мода:
```json
{
  "depends": {
    "easyhologram": ">=1.0.0"
  }
}
```

---

## 6. Чеклист перед публикацией

- [ ] Код компилируется: `gradlew build`
- [ ] JAR появился в `build/libs/`
- [ ] Протестировал в игре: `/hologram create text ~ ~2 ~ Hello`
- [ ] GitHub репозиторий создан и код загружен
- [ ] Создан Release с тегом `1.0.0` на GitHub
- [ ] JitPack успешно собрал проект
- [ ] Modrinth проект создан и версия загружена
- [ ] CurseForge проект создан и файл загружен
