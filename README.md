# Contagious Effects

A Minecraft Fabric mod that enables status effects to spread between nearby players, creating more dynamic and strategic gameplay scenarios.

## Overview

Contagious Effects adds a new layer of interaction to multiplayer Minecraft by allowing potion effects to propagate between players within a configurable range. When a player carries a status effect, nearby players have a chance to contract the same effect, simulating a form of proximity-based transmission.

## Features

- **Configurable Range**: Set the distance over which effects can spread (default: 2 blocks)
- **Spread Intervals**: Control how frequently the mod checks for effect transmission (default: every 10 ticks)
- **Cooldown System**: Prevent constant re-application with a configurable cooldown period
- **Smart Overwriting**: Option to only replace existing effects if the incoming effect is stronger or longer-lasting
- **Player State Filtering**: Automatically exclude spectators and creative mode players from both spreading and receiving effects
- **In-game Configuration**: Full integration with ModMenu and Cloth Config for easy adjustment of all settings

## Configuration Options

| Option | Default | Description |
|--------|---------|-------------|
| Range | 2.0 blocks | Maximum distance for effect transmission |
| Scan Interval | 10 ticks | Frequency of effect spread checks |
| Cooldown | 20 ticks | Minimum time between re-application of the same effect |
| Stronger Only | false | Only apply effects if they are stronger than existing ones |
| Ignore Spectators | true | Exclude spectators from the effect system |
| Ignore Creative | false | Exclude creative mode players from the effect system |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/) for Minecraft 1.21.11
2. Download and install [Fabric API](https://modrinth.com/mod/fabric-api)
3. Download this mod and place it in your `mods` folder
4. Optional but recommended: Install [ModMenu](https://modrinth.com/mod/modmenu) and [Cloth Config](https://modrinth.com/mod/cloth-config) for in-game configuration

## Compatibility

- Minecraft: 1.21.11
- Fabric Loader: >= 0.18.4
- Java: >= 21

## License

This project is released under the CC0 1.0 Universal license. See the LICENSE file for details.
