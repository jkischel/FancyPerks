package de.oliver.fancyperks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import de.oliver.fancyperks.perks.impl.EffectPerk;

public class PerkManager {

    private final Map<UUID, List<Perk>> playerPerks;
    private final File playersConfig = new File(FancyPerks.getInstance().getDataFolder().getAbsolutePath() + "/players.yml");

    public PerkManager() {
        this.playerPerks = new HashMap<>();
    }

    public List<Perk> getEnabledPerks(Player player) {
        return playerPerks.getOrDefault(player.getUniqueId(), new ArrayList<>());
    }

    public List<Perk> getEnabledPerks(UUID uuid) {
        return playerPerks.getOrDefault(uuid, new ArrayList<>());
    }

    public boolean hasPerkEnabled(Player player, Perk perk) {
        return playerPerks.containsKey(player.getUniqueId()) && playerPerks.get(player.getUniqueId()).contains(perk);
    }

    public void enablePerk(Player player, Perk perk) {
        List<Perk> perks = getEnabledPerks(player);
        if (!perks.contains(perk)) {
            perks.add(perk);
        }

        playerPerks.put(player.getUniqueId(), perks);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
        config.set("perks." + player.getUniqueId() + "." + perk.getSystemName(), true);
        try {
            config.save(playersConfig);
        } catch (IOException e) {
            e.printStackTrace();
            MessageHelper.error(Bukkit.getConsoleSender(), "Could not save player config");
        }
    }

    public void disablePerk(Player player, Perk perk) {
        List<Perk> perks = getEnabledPerks(player);
        perks.remove(perk);

        playerPerks.put(player.getUniqueId(), perks);

        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
        config.set("perks." + player.getUniqueId() + "." + perk.getSystemName(), false);
        try {
            config.save(playersConfig);
        } catch (IOException e) {
            e.printStackTrace();
            MessageHelper.error(Bukkit.getConsoleSender(), "Could not save player config");
        }
    }

    public void loadFromConfig() {
        playerPerks.clear();
        YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
        if (!config.isConfigurationSection("perks")) return;

        for (String uuidStr : config.getConfigurationSection("perks").getKeys(false)) {
            UUID uuid = UUID.fromString(uuidStr);
            for (String perkStr : config.getConfigurationSection("perks." + uuidStr).getKeys(false)) {
                Perk perk = PerkRegistry.getPerkByName(perkStr);
                if (perk == null) {
                    continue;
                }

                boolean isActivated = config.getBoolean("perks." + uuidStr + "." + perkStr, false);
                if (isActivated) {
                    List<Perk> current = playerPerks.getOrDefault(uuid, new ArrayList<>());
                    current.add(perk);
                    playerPerks.put(uuid, current);
                }
            }
        }
    }

    public static String getPlayerGUID(String playerName) {
        Player player = Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);

        if (player != null && player.isOnline()) {
            return player.getUniqueId().toString();
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);

            if (offlinePlayer.hasPlayedBefore()) {
                return offlinePlayer.getUniqueId().toString();
            }
            return null;
        }
    }

    public boolean activatePerkFor(String playerName, String perkStr, boolean isAdminCommand, CommandSender actingSender) {
        Perk perk = PerkRegistry.getPerkByName(perkStr);
 
        // check if specified perk exists
        if (perk == null) {
            MessageHelper.error(actingSender, "There is no perk named '" + perkStr + "'.");
            return false;
        }

        Player player = Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);

        // forbid to use perk if no admin and no permissions
        if (!isAdminCommand && !perk.hasPermission(player)) {
            MessageHelper.error(actingSender, "You don't have permission to use this perk!");
            return false;
        }

        // we have to decide between online and offline players at this point
        if (player == null || !player.isOnline()) {
            // okay, this player is offline.
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) { 
                // so this player is not online and never was here
                MessageHelper.error(actingSender, "No player named " + playerName + " found.");
                return false;
            }

            MessageHelper.error(actingSender, "Player UUID: " + offlinePlayer.getUniqueId());
            List<Perk> enabledPerks = getEnabledPerks(offlinePlayer.getUniqueId());

            if (!enabledPerks.contains(perk)) {
                enabledPerks.add(perk);
            }

            playerPerks.put(offlinePlayer.getUniqueId(), enabledPerks);

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
            config.set("perks." + offlinePlayer.getUniqueId() + "." + perk.getSystemName(), true);
            try {
                config.save(playersConfig);
            } catch (IOException e) {
                e.printStackTrace();
                MessageHelper.error(Bukkit.getConsoleSender(), "Could not save player config");
            }

            MessageHelper.success(actingSender, "Activated the " + perk.getDisplayName() + " perk for OFFLINE player " + offlinePlayer.getName() + ".");
            MessageHelper.success(actingSender, "Keep in mind that for OFFLINE players the world they will spawn in will be ignored.");

            return true;

        } else {
            // this is an online player

            // case: perk to be activated is already activated
            if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
                if (isAdminCommand) {
                    MessageHelper.warning(actingSender, "That player has already activated this perk.");
                } else {
                    MessageHelper.warning(actingSender, "You already activated this perk.");
                }
                return false;
            }

            // case: perk disabled for that world (admin override)
            if(perk.disabledWorlds.contains(player.getWorld().getName())){
                if (!isAdminCommand) {
                    MessageHelper.warning(actingSender, "The " + perk.getSystemName() + " perk is disabled in this world");
                    return false;
                } else {
                    MessageHelper.warning(actingSender, "Normally, the " + perk.getSystemName() + " perk would be disabled in that world.");
                    MessageHelper.warning(actingSender, "But as you are admin, you (hopefully) know what you do, so we will allow this.");
                }
            }
            
            FancyPerks.getInstance().getPerkManager().enablePerk(player, perk);
            perk.forceGrant(player);

            if (isAdminCommand) {
                MessageHelper.success(actingSender, "Activated the " + perk.getDisplayName() + " perk for player " + player.getName() + ".");
                MessageHelper.success(player, actingSender.getName() + " activated the " + perk.getDisplayName() + " perk for you.");
            } else {
                MessageHelper.success(actingSender, "Activated the " + perk.getDisplayName() + " perk");
            }
            return true;
        }

    }


    public boolean deactivatePerkFor(String playerName, String perkStr, boolean isAdminCommand, CommandSender actingSender) {
        Perk perk = PerkRegistry.getPerkByName(perkStr);

        Player player = Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);

        // check if specified perk exists
        if (perk == null) {
            MessageHelper.error(actingSender, "There is no perk named '" + perkStr + "'.");
            return false;
        }

        // we have to decide between online and offline players at this point
        if (player == null || !player.isOnline()) {
            // okay, this player is offline.
  
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) { 
                // so this player is not online and never was here
                MessageHelper.error(actingSender, "No player named " + playerName + " found.");
                return false;
            }

            MessageHelper.error(actingSender, "Player UUID: " + offlinePlayer.getUniqueId());
            List<Perk> enabledPerks = getEnabledPerks(offlinePlayer.getUniqueId());
            enabledPerks.remove(perk);

            playerPerks.put(offlinePlayer.getUniqueId(), enabledPerks);

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
            config.set("perks." + offlinePlayer.getUniqueId() + "." + perk.getSystemName(), false);
            try {
                config.save(playersConfig);
            } catch (IOException e) {
                e.printStackTrace();
                MessageHelper.error(Bukkit.getConsoleSender(), "Could not save player config");
            }

            MessageHelper.success(actingSender, "Deactivated the " + perk.getDisplayName() + " perk for OFFLINE player " + offlinePlayer.getName() + ".");
            if (perk instanceof EffectPerk) {
                MessageHelper.info(actingSender, "Keep in mind that this perk is an effect perk. It is not possible to change effects of offline players, so the effect will remain active! Try to remove this perk when player is online.");
            }

            return true;

        } else {
            // this is an online player
            
            FancyPerks.getInstance().getPerkManager().disablePerk(player, perk);
            perk.revoke(player);

            if (isAdminCommand) {
                MessageHelper.success(actingSender, "Deactivated the " + perk.getDisplayName() + " perk for player " + player.getName() + ".");
                MessageHelper.success(player, actingSender.getName() + " deactivated your " + perk.getDisplayName() + " perk.");
            } else {
                MessageHelper.success(actingSender, "Deactivated the " + perk.getDisplayName() + " perk");
            }
            return true;
        }
 
    }

    public void getPlayerPerks(CommandSender actingAdmin, String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        String playerUUID = offlinePlayer.getUniqueId().toString();

        if (playerUUID == null) {
            MessageHelper.error(actingAdmin, "No player found with name: " + playerName);
        }
        else {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
            if (!config.isConfigurationSection("perks")) {
                MessageHelper.error(actingAdmin, "Player configuration seems to be invalid.");
                return;
            }
            
            List<String> activePerks = new ArrayList<>();
            List<String> inactivePerks = new ArrayList<>();
            if (config.getConfigurationSection("perks." + playerUUID) == null) {
                MessageHelper.error(actingAdmin, "This player has no perks yet.");
                return;
            }

            for (String perkStr : config.getConfigurationSection("perks." + playerUUID).getKeys(false)) {
                Perk perk = PerkRegistry.getPerkByName(perkStr);
                if (perk == null) {
                    continue;
                }

                boolean isActivated = config.getBoolean("perks." + playerUUID + "." + perkStr, false);

                if (isActivated) {
                    activePerks.add(perkStr);
                } else {
                    inactivePerks.add(perkStr);
                }

            }

            Collections.sort(activePerks);
            Collections.sort(inactivePerks);
            String lineActivePerks = String.join(", ", activePerks);
            String lineInactivePerks = String.join(", ", inactivePerks);

            MessageHelper.success(actingAdmin, "The player " + playerName + " has these ACTIVE perks:");
            MessageHelper.success(actingAdmin, lineActivePerks.isEmpty() ? "(none)" : lineActivePerks);
            MessageHelper.success(actingAdmin, "and these INACTIVE perks:");
            MessageHelper.success(actingAdmin, lineInactivePerks.isEmpty() ? "(none)" : lineInactivePerks);

        }
    }

    public boolean handlePerkActivation(Player player, String perkStr) {
        if (perkStr.equals("*")) {
            List<String> activatedPerks = new ArrayList<>();
            for (Perk perk : PerkRegistry.ALL_PERKS) {
                if (perk.hasPermission(player) && !FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
                    if (!perk.grant(player)) {
                        MessageHelper.warning(player, "The " + perk.getSystemName() + " perk is disabled in this world");
                        continue;
                    }
                    activatedPerks.add(perk.getDisplayName());
                }
            }

            MessageHelper.success(player, "Activated all perks (" + String.join(", ", activatedPerks) + ")");
            return true;
        }

        Perk perk = PerkRegistry.getPerkByName(perkStr);
        if (perk == null) {
            MessageHelper.error(player, "Could not find perk with name: '" + perkStr + "'");
            return false;
        }

        if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
            MessageHelper.warning(player, "This perk is already active.");
            return true;
        }

        if (!perk.hasPermission(player)) {
            MessageHelper.error(player, "You don't have permission to use this perk");
            return false;
        }

        if (!perk.grant(player)) {
            MessageHelper.warning(player, "The " + perk.getSystemName() + " perk is disabled in this world");
            return false;
        }

        MessageHelper.success(player, "Activated the " + perk.getDisplayName() + " perk");
        return true;
    }

    public boolean handlePerkDeactivation(Player player, String perkStr) {
        if (perkStr.equals("*")) {
            List<String> deactivatedPerks = new ArrayList<>();
            for (Perk perk : PerkRegistry.ALL_PERKS) {
                if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
                    perk.revoke(player);
                    deactivatedPerks.add(perk.getDisplayName());
                }
            }

            MessageHelper.success(player, "Deactivated all perks (" + String.join(", ", deactivatedPerks) + ")");
            return true;
        }

        Perk perk = PerkRegistry.getPerkByName(perkStr);
        if (perk == null) {
            MessageHelper.error(player, "Could not find perk with name: '" + perkStr + "'");
            return false;
        }

        if (!FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
            MessageHelper.warning(player, "You already deactivated this perk");
            return true;
        }

        perk.revoke(player);
        MessageHelper.success(player, "Deactivated the " + perk.getDisplayName() + " perk");
        return true;
    }


}
