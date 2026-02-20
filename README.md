# Monumenta Lootrun Utils
Monumenta mod that helps lootrunners optimize routes in areas with lootrun protection and track respawning POIs. Made for Fabric 1.20.4.

#### Terminology
*Mob Points*: Players' score that increases after killing mobs, and decreases after looting a chest with at least 1 nearby mob

*Free chest*: chests that do not have any nearby mobs, allowing the chest be looted without costing *mob points*

*Nearby mobs*: mobs close enough to the chest to make it cost mob points when looting

<details>
<summary>Important: How does Monumenta's lootrun protection work?</summary>
Monumenta uses a point-based system to prevent looting chests without engaging mobs or spawners.

#### Point System
* Killing a regular/elite/boss mob grants 1/3/5 *mob points*
* Mining a spawner grants 1 spawner point
* Players can hold up to `4 * <points required to loot a chest>` points at once

#### Looting a Chest
* If there are **no mobs** within `12` blocks **of the chest** (box-shaped search area):
  * The chest **is lootable regardless of players' points**, and **no mob points will be deducted from any player**
  * This mod refers to these chests as *free*
* If there is at least **1** mob within `12` blocks:
  * If players within `20` blocks (sphere-shaped search area) of the chest have at least `?` *mob points combined:*
    * The chest **is lootable** and **removes `?` total *mob points* from the group**, prioritizing the looter, then by distance to the chest
  * Otherwise, the chest **is not lootable** (blocked)
  * This mod refers to these mobs as *nearby mobs*

Spawner points follow the same mechanic, but likely with a different amount of points required to loot the chest.

This mod focuses on the *mob points* mechanic, since it is very rare to be unable to loot a chest due to *spawner points* deficiency when conquering POIs.

* Players can quickly identify **free chests** (no mobs within `12` blocks of the chest), and no *mob points* will be deducted when looting.
* Players can optimize lootrun routes and pathing to encounter as many *free chests* as possible
* Decide whether to kill nearby mobs before looting a chest if only a few mobs prevent the chest from being *free.*
</details>


### Main Features
* Change the color of free chests
* When looking at a chest, list nearby mobs' distance from it in the player's action bar
* Render the number of nearby mobs on the chest
* Append the shard name to POI conquer messages
* Show respawning POIs' time until respawn in tooltips of the shard/"Overworld Instance" selector NPC
* Full feature list can be found at `src/main/resources/assets/anti-lootrun-tracker/lang/en_us.json` or `src/main/java/com/aquamancer/antilootruntracker/config/ModConfig.java`

## Building
        git clone https://github.com/aquamancer/anti-lootrun-tracker
        cd anti-lootrun-tracker
        ./gradlew build

Or `gradlew.bat build` on Windows

Output .jar is located at `build/libs/anti-lootrun-tracker-1.0.0.jar`

## Screenshots

<img src="https://github.com/aquamancer/anti-lootrun-tracker/blob/main/src/main/resources/screenshots/demo1.png">

*Chest with 2 mobs close enough to cost points*

<img src="https://github.com/aquamancer/anti-lootrun-tracker/blob/main/src/main/resources/screenshots/demofree.png">

*Chest with no mobs close enough to cost points*

<img src="https://github.com/aquamancer/anti-lootrun-tracker/blob/main/src/main/resources/screenshots/configdemo.png">

*Config screen*