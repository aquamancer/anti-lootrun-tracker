# Anti-Lootrun Tracker (specifically made for Monumenta)

Displays the number of mobs in a 12-block radius around a chest.

If there are no mobs nearby, the chest gets colored green.

This mod is compatible with Fabric 1.20.4

## Building
        git clone https://github.com/aquamancer/anti-lootrun-tracker
        cd anti-lootrun-tracker
        ./gradlew build

Output .jar is located at `build/libs/anti-lootrun-tracker-1.0.0.jar`

## Monumenta's Anti-Lootrun Mechanics

Monumenta uses a point-based system to prevent looting chests without engaging mobs or spawners.

#### Point System

* Killing a regular/elite/boss mob grants 1/3/5 *mob points*, respectively
* Mining a spawner grants 1 spawner point
* Players can hold up to `4 * <points required to loot a chest>` points at once

#### Looting a Chest
* If there is at least **1** mob within `12` blocks **of the chest** (box-shaped search area):
  * If players within `20` blocks (sphere-shaped search area) of the chest have at least `?` *mob points combined:*
    * The chest **is lootable** and **removes `?` total *mob points* from the group**, prioritizing the looter, then by distance to the chest
  * Otherwise, the chest **is not lootable**
* If there are **no mobs** within `12` blocks of the chest:
  * The chest **is lootable regardless of players' points**, and **no mob points will be deducted from any player**

Spawner points follow the same mechanic, but likely with a different amount of points required to loot the chest.

## Why this mod helps

This mod focuses on the *mob points* mechanic, since it's nearly impossible to be unable to loot a chest due to *spawner points* deficiency when conquering POI's.

* Players can quickly identify **free chests** (no mobs within `12` blocks of the chest), and no *mob points* will be deducted when looting!
* Players can optimize lootrun routes to encounter as many *free chests* as possible 
* Decide whether to fight nearby mobs before looting a chest if only a few mobs prevent the chest from being *free.*

## Screenshots

<img src="https://github.com/aquamancer/anti-lootrun-tracker/blob/main/src/main/resources/screenshots/demo1.png">

*Chest with 2 mobs close enough to cost points*

<img src="https://github.com/aquamancer/anti-lootrun-tracker/blob/main/src/main/resources/screenshots/demofree.png">

*Chest with no mobs close enough to cost points*

<img src="https://github.com/aquamancer/anti-lootrun-tracker/blob/main/src/main/resources/screenshots/configdemo.png">

*Config screen*