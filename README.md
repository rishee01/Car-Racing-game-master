<p align="center">
  <img src="https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white">
  <img src="https://img.shields.io/badge/Java%20Swing-Game%20Engine-blue?style=for-the-badge">
  <img src="https://img.shields.io/badge/Java2D-Graphics-green?style=for-the-badge">
  <img src="https://img.shields.io/badge/60%20FPS-Real%20Time-red?style=for-the-badge">
  <img src="https://img.shields.io/badge/MIT-License-black?style=for-the-badge">
</p>

# 🏎️ Arcade Racer 2D

> A modern **2D Arcade Racing Game** built entirely with **Java Swing** and **Java2D**, featuring intelligent traffic AI, procedural gameplay, power-ups, garage upgrades, particle effects, retro audio, and an endless arcade racing experience.

---

# 🎮 Overview

Arcade Racer 2D is a desktop arcade racing game inspired by classic endless highway racers.

Players must survive increasingly difficult traffic, collect coins, unlock upgrades, activate power-ups, and achieve the highest score while avoiding collisions.

https://rishee01.itch.io/arcade-racer-2d

The project demonstrates real-time rendering, game-loop architecture, object-oriented programming, collision detection, AI, resource management, and Java desktop development.

---

# ✨ Features

| Feature | Description |
|----------|-------------|
| 🚗 Endless Racing | Infinite highway gameplay |
| 🚦 Smart Traffic AI | Lane-based enemy vehicles |
| ⚡ Nitro Boost | Temporary speed increase |
| 🛡 Shield | Crash protection |
| 🪙 Coin Collection | Progression and upgrades |
| 🔧 Garage | Engine, Handling & Armor upgrades |
| 🌧 Dynamic Weather | Rain, fog and day/night |
| 💥 Particle Effects | Smoke, sparks, explosions |
| 🎵 Retro Audio | Engine sounds & chiptune music |
| 🏆 Leaderboard | Persistent top 10 scores |
| 📸 Screenshot | F12 saves gameplay |

---

# 🏗 Architecture

```text
Player Input
      │
      ▼
Input Manager
      │
      ▼
Game Loop (60 FPS)
      │
 ┌─────────────┐
 │             │
 ▼             ▼
Update      Render
 │             │
 ▼             ▼
Traffic AI  Java2D
 │             │
 ▼             ▼
Collision  Particles
 │             │
 └──────┬──────┘
        ▼
 HUD & Score
        │
        ▼
 Game State
```

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

# 🎮 Controls

| Key | Action |
|-----|--------|
| W / ↑ | Accelerate |
| S / ↓ | Brake |
| A / ← | Move Left |
| D / → | Move Right |
| SPACE | Nitro |
| ESC | Pause |
| M | Mute |
| F12 | Screenshot |

---

# ⚙️ Technology Stack

- Java 17+
- Java Swing
- Java2D
- Graphics2D
- OOP
- Event Handling
- Collision Detection
- Multithreading
- File Handling

---

# 🚀 Installation

## Clone

```bash
git clone https://github.com/rishee01/arcade-racer-2d.git
```

## Enter Project

```bash
cd arcade-racer-2d
```

## Compile

```bash
javac *.java
```

## Run

```bash
java Racing
```

---

# 📈 Gameplay Progression

- Collect Coins
- Upgrade Garage
- Unlock Better Performance
- Beat High Scores
- Survive Increasing Traffic
- Master Power-Ups

---

# 🔮 Roadmap

- [ ] Online Leaderboards
- [ ] Multiplayer
- [ ] Controller Support
- [ ] New Maps
- [ ] More Cars
- [ ] Achievements
- [ ] Cloud Save
- [ ] Android Version

---

# 📊 Performance

- Stable 60 FPS
- Resource Preloading
- Double Buffering
- Object-Oriented Design
- Efficient Collision Detection

---

# 🤝 Contributing

Contributions, feature requests, and bug reports are welcome.

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Open a Pull Request

---

# 📜 License

Licensed under the MIT License.

---

# 👨‍💻 Author

**Maharishee Ambati**

- Java Development
- Game Development
- Artificial Intelligence
- Software Engineering

---

<p align="center">

⭐ If you like this project, consider giving it a **Star**.

Built with ❤️ using Java.

</p>
