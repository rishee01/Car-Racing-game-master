````markdown
<p align="center">

<img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
<img src="https://img.shields.io/badge/Java%20Swing-Game%20Engine-blue?style=for-the-badge">
<img src="https://img.shields.io/badge/Java2D-Graphics-green?style=for-the-badge">
<img src="https://img.shields.io/badge/60%20FPS-Real%20Time-red?style=for-the-badge">
<img src="https://img.shields.io/badge/License-MIT-black?style=for-the-badge">

</p>

<h1 align="center">🏎️ Arcade Racer 2D</h1>

<p align="center">

A modern high-performance <b>2D Arcade Racing Game</b> built completely in <b>Java Swing & Java2D</b>, featuring intelligent traffic AI, dynamic weather, power-ups, garage upgrades, particle effects, retro sound synthesis, and an endless racing experience.

</p>

<p align="center">

🚗 Java • 🎮 Arcade • ⚡ 60 FPS • 🌧 Dynamic Weather • 🧠 Traffic AI

</p>

---

# 🎮 Gameplay

Survive as long as possible while navigating through heavy traffic.

Avoid collisions, collect coins, activate power-ups, upgrade your vehicle, and climb the global leaderboard.

Every second increases the challenge.

Can you become the ultimate highway racer?

---

# ✨ Features

| Feature | Description |
|----------|-------------|
| 🚗 Endless Racing | Infinite procedurally generated highway |
| 🚦 Smart Traffic AI | Dynamic vehicles with lane-changing behaviors |
| ⚡ Nitro Boost | Temporary speed boost with visual effects |
| 🛡 Shield System | Temporary collision protection |
| 🪙 Coin Collection | Unlock upgrades and progression |
| 🚗 Garage | Upgrade engine, handling and armor |
| 🌧 Dynamic Weather | Rain, fog and day/night transitions |
| 🎵 Retro Audio | Chiptune background music and engine sounds |
| 💥 Particle Engine | Smoke, sparks, explosions and nitro flames |
| 🏆 Leaderboard | Persistent Top 10 high scores |
| 🎯 Power-Ups | Nitro, Magnet, Shield, Slow Motion, Double Score, Repair |
| 🎮 Multiple Game States | Menu, Garage, Countdown, Pause and Game Over |

---

# 🏗 Game Architecture

```text
Player Input
      │
      ▼
Input Manager
      │
      ▼
Game Loop (60 FPS)
      │
 ┌───────────────┐
 │               │
 ▼               ▼
Update        Rendering
 │               │
 ▼               ▼
Traffic AI   Java2D Graphics
 │               │
 ▼               ▼
Collision    Particle Engine
 │               │
 └──────┬────────┘
        ▼
HUD & Score System
        │
        ▼
Game State Manager
````

---

# 🚗 Gameplay Mechanics

### 🧠 Intelligent Traffic

* Multiple traffic vehicle types
* Lane changing AI
* Random traffic density
* Progressive difficulty
* Dynamic spawning

---

### ⚡ Power-Ups

* 🔴 Nitro
* 🟢 Repair
* 🟣 Magnet
* 🔵 Slow Motion
* 🟠 Double Score
* 🛡 Shield

Each power-up features unique visual effects and timed abilities.

---

### 🏎 Garage Upgrades

Upgrade your vehicle permanently.

* Engine
* Handling
* Armor

Spend collected coins wisely to survive longer.

---

### 🌦 Dynamic Environment

Experience an immersive world with

* Rain
* Fog
* Day/Night Cycle
* Street Lights
* Road Effects
* Screen Shake
* Camera Zoom
* Speed Lines

---

# 🎮 Controls

| Key   | Action      |
| ----- | ----------- |
| W / ↑ | Accelerate  |
| S / ↓ | Brake       |
| A / ← | Steer Left  |
| D / → | Steer Right |
| SPACE | Nitro Boost |
| ESC   | Pause       |
| M     | Mute Audio  |
| F12   | Screenshot  |

---

# 📂 Project Structure

```text
ArcadeRacer2D
│
├── Racing.java
├── GamePanel.java
├── Player.java
├── EnemyCar.java
├── Road.java
├── HUD.java
├── Particle.java
├── PowerUp.java
├── ResourceManager.java
├── SoundManager.java
├── ScoreManager.java
├── InputManager.java
├── GameState.java
├── assets/
├── images/
├── sounds/
└── README.md
```

---

# ⚙ Technology Stack

* Java 17+
* Java Swing
* Java2D Graphics
* Graphics2D Rendering
* Object-Oriented Programming
* Multithreading
* File Handling
* Event Handling
* Collision Detection
* Procedural Game Logic

---

# 🚀 Installation

Clone the repository

```bash
git clone https://github.com/rishee01/arcade-racer-2d.git
```

Move into the project

```bash
cd arcade-racer-2d
```

Compile

```bash
javac *.java
```

Run

```bash
java Racing
```

---

# 📊 Game Features

✅ Endless Racing

✅ Smart Traffic AI

✅ Dynamic Difficulty

✅ Dynamic Weather

✅ Power-Up System

✅ Garage Upgrades

✅ Coin Collection

✅ High Score System

✅ Particle Engine

✅ Retro Audio

✅ Screen Shake

✅ HUD

✅ Pause Menu

✅ Screenshot Support

---

# 🔮 Future Roadmap

* 🌐 Online Leaderboards
* 👥 Multiplayer Racing
* 🎮 Controller Support
* 🚓 Police Chase Mode
* 🚑 Emergency Vehicle AI
* 🏆 Achievement System
* ☁ Cloud Save
* 📱 Android Version
* 🌍 New Maps
* 🚘 More Unlockable Cars

---

# 📈 GitHub Stats

<p align="center">

<img src="https://github-readme-stats.vercel.app/api?username=rishee01&show_icons=true&theme=tokyonight">

<img src="https://github-readme-streak-stats.herokuapp.com/?user=rishee01&theme=tokyonight">

</p>

---

# 📜 License

This project is licensed under the **MIT License**.

---

# 👨‍💻 Developer

**Maharishee Ambati**

Passionate about building high-performance software and immersive interactive applications.

### Interests

* 🎮 Game Development
* ☕ Java Development
* 🤖 Artificial Intelligence
* ☁ Cloud Computing
* 💻 Software Engineering

---

<p align="center">

⭐ If you enjoyed this project, consider giving it a Star!

Made with ❤️ using Java.

</p>
```
