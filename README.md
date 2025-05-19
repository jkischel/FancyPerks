# Fancy Perks

**-------------------------------------------**

This is an extended version of FancyPerks by Oliver Schlueter.
As the original version of this plugin is not developed anymore and even archived in Github, I hope this version can help out :)

**-------------------------------------------**

FancyPerks is a simple plugin that adds some fancy perks to your server.

**Only supported for 1.20** _(might work in other version too tho)_<br>
_Using [paper](https://papermc.io/downloads) is highly recommended_

## Get the plugin

You can download the latest versions at the following places:

- https://github.com/jkischel/FancyPerks/releases
- Build from source

## Commands

/perks - Opens the perks gui<br>
/perks activate (perk | *) - _Activates the perk_<br>
/perks deactivate (perk | *) - _Deactivates the perk_<br>
/fancyperks version - _Shows the current version_<br>
/fancyperks reload - _Reloads the config_<br>

## Permissions

To use a perk - ``fancyperks.perk.(perk name)``<br>

## Perks

- Fast Digging

  Gives Haste II effect.

- Instant Break

  Gives Haste 50 effect.

- Night Vision

  Gives the night vision effect.

- Water Breathing

  Gives the Water Breathing effect.

- Speed

  Gives Speed II effect.

- Slowness

  Gives Slowness I effect.

- Dolphins Grace

  Gives Dolphins Grace effect.

- Glowing

  Gives Glowing effect.

- Strength

  Gives Strength II effect.

- Jump Boost

  Gives Jump Boost II effect.

- Health Boost

  Gives Health Boost I effect (2 extra hearts).

- Regeneration

  Gives Regeneration effect.

- Fire Resistance

  Gives Fire Resistance effect.

- Resistance

  Gives Restance III effect.

- Invisibility

  Gives Invisibility effect (not to be confused with vanish perk!).

- Levitation

  Gives Levitation effect

- Luck

  Gives Luck effect (only applies to fishing).

- Slow Falling

  Gives Slow Falling effect.

- Bad Omen

  Gives Bad Omen effect.

- Hero of the Village

  Gives Hero of the Village effect.

- Keep Exp

  No XP loss when dying.

- Keep Inventory

  No item loss when dying.

- Fly

  Enable flight.

- No Hunger

  Saturation does not decrease and is always maximum.

- No Fire Damage

  Take no damage from fire.

- No Fall Damage

  Take no damage from falling.

- No Explosion Damage

  Take no damage from explosions like TNT or Creepers.

- No Poison Damage

  Take no damage when poisoned (this will not prevent to get poisoned).

- No Projectile Damage

  Take no damage from projectiles like arrows.

- No Frost Damage

  Take no damage when standing in powdered snow (this will not prevent to get frozen).

- No Block Damage

  Take no damage from blocks like falling anvils or suffocation.

- Half Damage

  Take just the half of the damage that you would normally.

- God

  Take no damage at all.

- Mobs ignore

  Mobs don't attack you anymore.

- Double XP

  Get twice as much XP from killing mobs (not from mining blocks!).

- Triple XP

  Get three times as much XP from killing mobs (dito).
  
  Does not aggregate with Double XP; if both perks are activated, 3xXP is max.

- Double Drops

  Get twice as much drops from killing mobs. 

  Be sure to blacklist mobs that can pick up items to prevent possible duping!

- Vanish

  Hide from other players.

- Telekinesis

  Pick up mined blocks directly.

- Instant Smelt

  Directly smelts ores when mining them:
  copper -> copper ingot, gold -> gold ingot, iron -> iron ingot, ancient debris -> netherite scrap and cobble-> stone

  Does NOT smelt nether gold as usually this would drop a gold nugget instead of raw gold.

- Auto repair

  Player tools in inventory will not lose durability anymore (but will not be fixed on damage but keep their current durability!).

- Auto planting

  Will re-plant crops directly when harvesting them.

- Lava runner

  Walk on lava, will convert lava below into obsidian.
   
  **Attention, this perk could/will be buggy under some conditions:**
  when the perk is turned on and off, the transformed blocks could remain, but not be able to destroy. Also this perk could cause issues with some skyblock/oneblock, claim and/or plot plugins. I kept this perk from the original plugin to keep compatibility, but I'd rather recommand not to use it.

- Drop More Blocks

  Drops some blocks that usually do not drop when mined:

  budding amethyst, reinforced deepslate, dirt path, farmland and frogspawn.

- Drop Spawners

  Will drop mob spawners and trial spawners as item when mined. Currently, the current mob in the spawner will be lost, so a mined and again placed spawner will be empty.

**Some general thoughts about the perks:**

As server owner, you might not want to activate all of these perks for your players at the same time, but as there are e.g. different types of anti-damage perks, that gives you as owner the option to more have a more fine-grained control of what you allow and what not. And maybe you want to have a more fine-grained control when thinking of different worlds as you can individually decide in which world you want to allow this perk and where not.

Also, you can configure the strength of the effects in the conf file if a perk looks too OP for you.

## Build from source

1. Clone this repo and run `gradlew build`
2. The jar file will be in `build/libs/FancyPerks-<version>.jar`
