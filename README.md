# Fancy Perks

**-------------------------------------------**

This is an extended version of FancyPerks by Oliver Schlueter.
As the original version of this plugin is not developed anymore by Oliver and the repo is even archived in Github, I hope this version can help out :)

**-------------------------------------------**

FancyPerks is a simple plugin that adds some fancy perks to your server.

## Prerequisites
- Using a [paper](https://papermc.io/downloads) based server is highly recommended.<br>

- **Only supported for Minecraft version 1.21+**<br>

- LuckPerms (otherwise big parts of the perks won't work as intended or not at all)<br>

- Vault (otherwise, players cannot purchase perks)

## Get the plugin

You can download the latest versions at the following places:

- https://github.com/jkischel/FancyPerks/releases

- Build from source (see section at the end of this readme)

## Commands

### Player commands:
/perks - Opens the perks gui<br>
/perks activate (perk | *) - _Activates the perk_<br>
/perks deactivate (perk | *) - _Deactivates the perk_<br>

### Admin commands:
/fancyperks version - _Shows the current version_<br>
/fancyperks reload - _Reloads the config_<br>
/fancyperks getperksof (player) - get a list of active and inactive perks of the specified player<br> 
/fancyperks activateperk (player) (perk) - enable perk for the specified player<br> 
/fancyperks deactivateperk (player) (perk) - disable perk for the specified player<br> 
/fancyperks grantperk (player) (perk) - grant player a perk for free so he does not have to buy it<br>
/fancyperks revokeperk (player) (perk) - revoke player's perk with no refund<br>
/fancyperks massgrantperk (perk) - grants a perk to all players
/fancyperks massrevokeperk (perk) - revokes a perk from all players

It's strongly recommended to perform bulk actions like 

``/fancyperks massgrantperk *``

not in the times when the server is under heavy traffic (e.g. prime time with hundreds of live players). 
Truly, the mass tasks are desgned to run asynchronous to reduce lags, but it's not guaranteed that 
everything runs smoothly, especially on big servers with hundreds of players (keep in mind that also
offline players are counting into that number!).

#### General thoughts about admin commands:
In short words: This plugin has no brain, use your own :) 

That means, the plugin assumes that as an admin you know what you are doing. So **the plugin does not**
**check**, for example, **whether a perk is allowed in the player's world or whether the player has**
**bought that perk at all** if you're using the admin commands (that's _your_ job as admin!).
So if you fire an admin command, the plugin does exactly what you tell it to do (if it's possible).

It is also important to keep in mind that if you activate, deactivate, enable or disable a perk for an 
online player, the player affected will receive a message about this and who did it. 

Also it is possible to disable a fly/levitation perk of players that are literally flying at that moment,
keep in mind that such players should land first or they might hit the ground very hard.

Remember to play fair, also as admin. :)

#### About offline players
Yes, you can change perk statuses for players that aren't online at the moment as long as they were 
online some time ago. But that has a little catch: if a player has activated an effect perk like strength,
and you deactivate that perk, the effect will still remain active if the player comes back. This is
due to the technical nature of offline players: you cannot add or clear any effect until that player 
is online. This might lead to ... unexpected states. 

## Permissions

To use a perk - ``fancyperks.perk.(perk name)``<br>

If perks are buyable, the permissions are bought by the player and then set to his player account. 
If a player already has the perk permission above, he does not need to buy the perk.

## Configuration

In the config.yml there are several well-structured parameters you can configure to your needs:

### General parameters
``mute_version_notification: false``
deprecated, will be removed soon.

``activate_perk_on_permission_set: false``
If a player gets the permission, shall the corresponding perk also be activated automatically?

### GUI configuration
```
perk_disabled_item: RED_DYE
perk_enabled_item: GREEN_DYE
perk_not_owned_item: YELLOW_DYE
```
The items that are displayed below the perk in the /perks GUI, depending on the status.

```
next_page_item: PLAYER_HEAD:owner=MHF_arrowright
previous_page_item: PLAYER_HEAD:owner=MHF_arrowleft
```
The pagination items.

In general, all the items also can be player heads, in the syntax example above.
Keep in mind that only real player heads are supported, no just-texture heads.

### Perk configuration
A perk configuration entry will usually have entries like this example:
```
  hero_of_the_village:
    name: Hero of the village
    description: This perk will give a discount at villager trades. <newline>
      <red>(overworld only!)</red>
    enabled: true
    disabled_worlds:
    - world_nether
    - world_the_end
    buyable: true
    price: 100000.0
    effectStrength: 2
    display_item: EMERALD
```
**The parameters explained in detail**

```
perk_name:
```
This is an internal perk name, you usually see it in commands like /perks activate perk_name

```
  name: Display name here
```
Clear text perk name used in GUI and status messages.

```
  description: longer description goes here
```
Formattable description text like described [here](https://docs.advntr.dev/minimessage/format). 
Line break is ``<newline>``, but not ``<br>``.

```
  enabled: true
```
Will this perk appear in the /perks menu? Can it be enabled by players?

An admin *can* also activate disabled perks.

```
  disabled_worlds: []
```
The plugin will prevent players from activating this perks in this worlds and if it's 
already activated, the perk will get deactivated when the player enters one of the specified worlds.

Admins *can* activate perks anywhere thru commands (but not thru their GUI).

Example:
```
  disabled_worlds:
  - world_nether
  - world_the_end
```

```
  buyable: true
```
If the user doesn't already have the permission for this perk, can he purchase it if he has enough coins?

```
  price: 150000.0
```
If buyable=true, for how much ingame currency?
 
```
  effectStrength: 0
```
In effect perks, you can define the strength, beginning from 0 (which equals Level I).

```
  display_item: WATER_BUCKET
```
Which item shall be used for displaying it in the GUI?

```
  blacklist: []
```
The *double_drop* perk does support blacklisting mobs that will not generate double drops (as a dupe protection).
Example:
```
  blacklist:
  - PLAYER
  - ZOMBIE
  - FOX
  - ALLAY
```

## Perks

- **Fast Digging**

  Gives Haste II effect.

- **Instant Break**

  Gives Haste 50 effect.

- **Night Vision**

  Gives the night vision effect.

- **Water Breathing**

  Gives the Water Breathing effect.

- **Speed**

  Gives Speed II effect.

- **Slowness**

  Gives Slowness I effect.

- **Dolphins Grace**

  Gives Dolphins Grace effect.

- **Glowing**

  Gives Glowing effect.

- **Strength**

  Gives Strength II effect.

- **Jump Boost**

  Gives Jump Boost II effect.

- **Health Boost**

  Gives Health Boost I effect (2 extra hearts).

- **Regeneration**

  Gives Regeneration effect.

- **Fire Resistance**

  Gives Fire Resistance effect.

- **Resistance**

  Gives Restance III effect.

- **Invisibility**

  Gives Invisibility effect (not to be confused with vanish perk!).

- **Levitation**

  Gives Levitation effect

- **Luck**

  Gives Luck effect (only applies to fishing).

- **Slow Falling**

  Gives Slow Falling effect.

- **Bad Omen**

  Gives Bad Omen effect.

- **Hero of the Village**

  Gives Hero of the Village effect.

- **Keep Exp**

  No XP loss when dying.

- **Keep Inventory**

  No item loss when dying.

- **Fly**

  Enable flight.

- **No Hunger**

  Saturation does not decrease and is always maximum.

- **No Fire Damage**

  Take no damage from fire.

- **No Fall Damage**

  Take no damage from falling.

- **No Explosion Damage**

  Take no damage from explosions like TNT or Creepers.

- **No Poison Damage**

  Take no damage when poisoned (this will not prevent to get poisoned).

- **No Projectile Damage**

  Take no damage from projectiles like arrows.

- **No Frost Damage**

  Take no damage when standing in powdered snow (this will not prevent to get frozen).

- **No Block Damage**

  Take no damage from blocks like falling anvils or suffocation.

- **Half Damage**

  Take just the half of the damage that you would normally.

- **God**

  Take no damage at all.

- **Mobs ignore**

  Mobs don't attack you anymore.

- **Double XP**

  Get twice as much XP from killing mobs (not from mining blocks!).

- **Triple XP**

  Get three times as much XP from killing mobs (dito).
  
  Does not aggregate with Double XP; if both perks are activated, 3xXP is max.

- **Double Drops**

  Get twice as much drops from killing mobs. 

  Be sure to blacklist mobs that can pick up items to prevent possible duping!

- **Vanish**

  Hide from other players.

- **Telekinesis**

  Pick up mined blocks directly.

- **Instant Smelt**

  Directly smelts ores when mining them:
  copper -> copper ingot, gold -> gold ingot, iron -> iron ingot, ancient debris -> netherite scrap and cobble-> stone

  Does NOT smelt nether gold as usually this would drop a gold nugget instead of raw gold.

- **Auto repair**

  Player tools in inventory will not lose durability anymore (but will not be fixed on damage but keep their current durability!).

- **Auto planting**

  Will re-plant crops directly when harvesting them.

- **Lava runner**

  Walk on lava, will convert lava below into obsidian.
   
  *Attention, this perk could/will be buggy under some conditions:*
  When the perk is turned on and off, the transformed blocks could remain, but are unintendedly indestructible. 
  Also this perk could cause issues with some skyblock/oneblock, claim and/or plot plugins. 
  I kept this perk from the original plugin to keep compatibility, but I'd strongly recommand not to use it.

- **Drop More Blocks**

  Drops some blocks that usually do not drop when mined:

  budding amethyst, reinforced deepslate, dirt path, farmland and frogspawn.

- **Drop Spawners**

  Will drop mob spawners and trial spawners as item when mined. Currently, the current mob in the spawner will be lost, so a mined and again placed spawner will be empty.

**Some general thoughts about the perks:**

As server owner, you might not want to activate all of these perks for your players at the same time, 
but as there are e.g. different types of anti-damage perks, that gives you as owner the option rather to 
have a more fine-grained control of what you allow and what not. And maybe you want to have a 
more fine-grained control when thinking of different worlds as you can individually decide in which 
world you want to allow this perk and where not.

Also, you can configure the strength of the effects in the conf file if a perk looks too OP for you.

## Build from source

Prerequisite:
You will need Java 17 and Maven.

1. Clone this repo and run `build.cmd`
2. The jar file will be in `build/target/FancyPerks-<version>.jar`
