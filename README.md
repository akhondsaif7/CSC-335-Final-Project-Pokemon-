# Pokémon UA — Turn-Based RPG

## Overview
A Pokémon-inspired turn-based RPG featuring strategic creature battles,
team building, and both single-player and multiplayer modes. Built in
Java as a team project for CSC 335 with Isa Abdulrazak, Matt Lagier, and
Bradley Adams.

## Features

### Core Gameplay
- **12+ unique creatures** with individual stats (HP, Attack, Defense, Speed, etc.) and movesets
- **Turn-based combat system** — select moves, swap creatures mid-battle, and outplay opponents
- **Team building** — assemble a team of 3+ creatures from the full roster before battle
- **Creature swapping** — tag creatures in and out of battle strategically

### Type System
- **Type effectiveness** — certain types deal bonus or reduced damage
  against others (e.g., Fire > Grass > Water > Fire)
- Adds a layer of strategy to team composition and move selection

### Game Modes
- **Single-player progressive mode** — battle through a series of
  increasingly difficult AI opponents on a progression map
- **Multiplayer mode** — challenge other players via:
  - Networked (online) play
  - Local play

### AI Difficulty
- Multiple difficulty levels that scale with progression
- Early opponents choose suboptimal moves; later opponents play optimally

### GUI
- **Main Menu / Team Select** — choose your game mode and build your team
- **Battle Screen** — animated battle interface with creature sprites,
  HP bars, and move selection
- **Progression Map** — visual overview of your journey and upcoming opponents

## Tech Stack
- **Language:** Java
- **GUI:** JavaFX
- **Networking:** Java Sockets
- **Architecture:** MVC (Model-View-Controller)

## How to Run

### Requirements
- Java 17+
- JavaFX SDK

### Build & Run
```bash
git clone https://github.com/YOUR_USERNAME/pokemon-ua.git
cd pokemon-ua
javac -d bin src/**/*.java
java -cp bin Main
