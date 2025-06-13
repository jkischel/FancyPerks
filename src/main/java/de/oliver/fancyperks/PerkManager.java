package de.oliver.fancyperks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.oliver.fancylib.MessageHelper;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import de.oliver.fancyperks.perks.impl.EffectPerk;
import net.milkbowl.vault.permission.Permission;


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

    @SuppressWarnings("CallToPrintStackTrace")
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

    @SuppressWarnings("CallToPrintStackTrace")
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

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean activatePerkFor(String playerName, String perkStr, boolean isAdminCommand, CommandSender actingSender) {
 
        if (perkStr.equals("*")) {
            for (Perk perk : PerkRegistry.ALL_PERKS) {
                activatePerkFor(playerName, perk.getSystemName(), isAdminCommand, actingSender);
            }
            return true;
        }

        Perk perk = PerkRegistry.getPerkByName(perkStr);
 
        // check if specified perk exists
        if (perk == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perkStr);
            String message = LanguageHelper.getLocalizedMessage("no_such_perk", replacements);
            MessageHelper.error(actingSender, message);
            return false;
        }

        Player player = Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);

        // forbid to use perk if no admin and no permissions
        if (!isAdminCommand && !perk.hasPermission(player)) {
            String message = LanguageHelper.getLocalizedMessage("no_perk_permission", null);
            MessageHelper.error(actingSender, message);
            return false;
        }

        // we have to decide between online and offline players at this point
        if (player == null || !player.isOnline()) {
            // okay, this player is offline.
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) { 
                // so this player is not online and never was here
                Map<String, String> replacements = new HashMap<>();
                replacements.put("playername", playerName);
                String message = LanguageHelper.getLocalizedMessage("no_such_player", replacements);
                MessageHelper.error(actingSender, message);
                return false;
            }

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

            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perk.getDisplayName());
            replacements.put("playername", offlinePlayer.getName());
            String message = LanguageHelper.getLocalizedMessage("activated_perk_for_offline_player", replacements);
            MessageHelper.success(actingSender, message);
            return true;

        } else {
            // this is an online player

            // case: perk to be activated is already activated
            if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
                if (isAdminCommand) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getDisplayName());
                    replacements.put("playername", player.getName());
                    String message = LanguageHelper.getLocalizedMessage("target_player_already_activated_perk", replacements);
                    MessageHelper.warning(actingSender, message);
                } else {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getDisplayName());
                    String message = LanguageHelper.getLocalizedMessage("player_already_activated_perk", replacements);
                    MessageHelper.warning(actingSender, message);
                }
                return false;
            }

            // case: perk disabled for that world (admin override)
            if(perk.disabledWorlds.contains(player.getWorld().getName())){
                if (!isAdminCommand) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);
                    MessageHelper.warning(actingSender, message);
                    return false;
                } else {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    String message = LanguageHelper.getLocalizedMessage("perk_normally_disabled_in_this_world", replacements);
                    MessageHelper.warning(actingSender, message);
                }
            }
            
            FancyPerks.getInstance().getPerkManager().enablePerk(player, perk);
            perk.forceGrant(player);

            if (isAdminCommand) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getDisplayName());
                replacements.put("playername", player.getName());
                replacements.put("adminname", actingSender.getName());
                String message = LanguageHelper.getLocalizedMessage("activated_perk_for_online_player", replacements);                
                MessageHelper.success(actingSender, message);
                message = LanguageHelper.getLocalizedMessage("admin_activated_perk_for_you", replacements);
                MessageHelper.success(player, message);
            } else {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getDisplayName());
                String message = LanguageHelper.getLocalizedMessage("perk_successfully_activated", replacements);
                MessageHelper.success(actingSender, message);
            }
            return true;
        }

    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean deactivatePerkFor(String playerName, String perkStr, boolean isAdminCommand, CommandSender actingSender) {
        if (perkStr.equals("*")) {
            for (Perk perk : PerkRegistry.ALL_PERKS) {
                deactivatePerkFor(playerName, perk.getSystemName(), isAdminCommand, actingSender);
            }
            return true;
        }

        Perk perk = PerkRegistry.getPerkByName(perkStr);

        Player player = Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);

        // check if specified perk exists
        if (perk == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perkStr);
            String message = LanguageHelper.getLocalizedMessage("no_such_perk", replacements);
            MessageHelper.error(actingSender, message);
            return false;
        }

        // we have to decide between online and offline players at this point
        if (player == null || !player.isOnline()) {
            // okay, this player is offline.
  
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) { 
                // so this player is not online and never was here
                Map<String, String> replacements = new HashMap<>();
                replacements.put("playername", playerName);
                String message = LanguageHelper.getLocalizedMessage("no_such_player", replacements);
                MessageHelper.error(actingSender, message);
                return false;
            }

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

            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perk.getDisplayName());
            replacements.put("playername", offlinePlayer.getName());
            String message = LanguageHelper.getLocalizedMessage("deactivated_perk_for_offline_player", replacements);
            MessageHelper.success(actingSender, message);

            if (perk instanceof EffectPerk) {
                replacements = new HashMap<>();
                replacements.put("perkname", perk.getDisplayName());
                message = LanguageHelper.getLocalizedMessage("admin_notice_effect_perk_offline", replacements);
                MessageHelper.info(actingSender, message);
            }

            return true;

        } else {
            // this is an online player
            
            FancyPerks.getInstance().getPerkManager().disablePerk(player, perk);
            perk.revoke(player);

            if (isAdminCommand) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getDisplayName());
                replacements.put("playername", player.getName());
                replacements.put("adminname", actingSender.getName());
                String message = LanguageHelper.getLocalizedMessage("deactivated_perk_for_online_player", replacements);
                MessageHelper.success(actingSender, message);
                message = LanguageHelper.getLocalizedMessage("admin_deactivated_your_perk", replacements);
                MessageHelper.success(player, message);
            } else {
                String message = LanguageHelper.getLocalizedMessage("perk_successfully_deactivated", null);
                MessageHelper.success(actingSender, message);
            }
            return true;
        }
 
    }

    public void getPlayerPerks(CommandSender actingAdmin, String playerName) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
        String playerUUID = offlinePlayer.getUniqueId().toString();

        if (playerUUID == null) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("playername", playerName);
                String message = LanguageHelper.getLocalizedMessage("no_such_player", replacements);
                MessageHelper.error(actingAdmin, message);
        }
        else {

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
            if (!config.isConfigurationSection("perks")) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("playername", playerName);
                String message = LanguageHelper.getLocalizedMessage("player_configuration_invalid", replacements);
                MessageHelper.error(actingAdmin, message);                
                return;
            }
            
            List<String> activePerks = new ArrayList<>();
            List<String> inactivePerks = new ArrayList<>();
            if (config.getConfigurationSection("perks." + playerUUID) == null) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("playername", playerName);
                String message = LanguageHelper.getLocalizedMessage("player_has_no_perks_yet", replacements);
                MessageHelper.error(actingAdmin, message);                
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

            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", playerName);
            String message = LanguageHelper.getLocalizedMessage("perk_list_active_perks", replacements);
            MessageHelper.success(actingAdmin, message);

            for (String perkName : activePerks) {
                replacements = new HashMap<>();
                replacements.put("perkname", perkName);
                message = LanguageHelper.getLocalizedMessage("perk_list_entry_active_perk", replacements);
                MessageHelper.success(actingAdmin, message);
            }            

            replacements = new HashMap<>();
            replacements.put("player", playerName);
            message = LanguageHelper.getLocalizedMessage("perk_list_inactive_perks", replacements);
            MessageHelper.success(actingAdmin, message);

            for (String perkName : inactivePerks) {
                replacements = new HashMap<>();
                replacements.put("perkname", perkName);
                message = LanguageHelper.getLocalizedMessage("perk_list_entry_inactive_perk", replacements);
                MessageHelper.success(actingAdmin, message);
            }            

        }
    }

    public boolean handlePerkActivation(Player player, String perkStr) {
        if (perkStr.equals("*")) {
            List<String> activatedPerks = new ArrayList<>();
            for (Perk perk : PerkRegistry.ALL_PERKS) {
                if (perk.hasPermission(player) && !FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
                    if (!perk.grant(player)) {
                        Map<String, String> replacements = new HashMap<>();
                        replacements.put("perkname", perk.getSystemName());
                        String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);
                        MessageHelper.warning(player, message);                        
                        continue;
                    }
                    activatedPerks.add(perk.getDisplayName());
                }
            }

            Map<String, String> replacements = new HashMap<>();
            replacements.put("perklist", String.join(", ", activatedPerks));
            String message = LanguageHelper.getLocalizedMessage("activated_all_perks", replacements);
            MessageHelper.success(player, message);   
            return true;
        }

        Perk perk = PerkRegistry.getPerkByName(perkStr);
        if (perk == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perkStr);
            String message = LanguageHelper.getLocalizedMessage("no_such_perk", replacements);
            MessageHelper.error(player, message);            
            return false;
        }

        if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perk.getDisplayName());
            String message = LanguageHelper.getLocalizedMessage("player_already_activated_perk", replacements);
            MessageHelper.warning(player, message);
            return true;
        }

        if (!perk.hasPermission(player)) {
            String message = LanguageHelper.getLocalizedMessage("no_perk_permission", null);
            MessageHelper.error(player, message);            
            return false;
        }

        if (!perk.grant(player)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perk.getSystemName());
            String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);
            MessageHelper.warning(player, message);
            return false;
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("perkname", perk.getDisplayName());
        String message = LanguageHelper.getLocalizedMessage("perk_successfully_activated", replacements);
        MessageHelper.success(player, message);
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
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perklist", String.join(", ", deactivatedPerks));
            String message = LanguageHelper.getLocalizedMessage("deactivated_all_perks", replacements);
            MessageHelper.success(player, message);
            return true;
        }

        Perk perk = PerkRegistry.getPerkByName(perkStr);
        if (perk == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perkStr);
            String message = LanguageHelper.getLocalizedMessage("no_such_perk", replacements);
            MessageHelper.error(player, message);
            return false;
        }

        if (!FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perk.getDisplayName());
            String message = LanguageHelper.getLocalizedMessage("player_dealready_activated_perk", replacements);
            MessageHelper.warning(player, message);
            return true;
        }

        perk.revoke(player);
        Map<String, String> replacements = new HashMap<>();
        replacements.put("perkname", perk.getDisplayName());
        String message = LanguageHelper.getLocalizedMessage("perk_successfully_deactivated", replacements);
        MessageHelper.success(player, message);
        return true;
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean grantPerkTo(String playerName, String perkStr, boolean isAdminCommand, CommandSender actingSender, boolean performSilent) {

        if (perkStr.equals("*")) {
            for (Perk mperk : PerkRegistry.ALL_PERKS) {
                grantPerkTo(playerName, mperk.getSystemName(), isAdminCommand, actingSender, performSilent);
            }
            return true;
        }

        Perk perk = PerkRegistry.getPerkByName(perkStr);
 
        // check if specified perk exists
        if (perk == null) {
            if (performSilent) {
                Bukkit.getLogger().log(Level.WARNING, "There is no perk named {0}.", perkStr);
            } else {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perkStr);
                String message = LanguageHelper.getLocalizedMessage("no_such_perk", replacements);
                MessageHelper.error(actingSender, message);
            } 
            return false;
        }

        Player player = Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);

        // forbid to use perk if no admin and no permissions
        if (!isAdminCommand && !perk.hasPermission(player)) {
            if (!performSilent) {
                String message = LanguageHelper.getLocalizedMessage("no_perk_permission", null);
                MessageHelper.error(actingSender, message);
            }
            return false;
        }

        // we have to decide between online and offline players at this point
        if (player == null || !player.isOnline()) {
            // okay, this player is offline.
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) { 
                // so this player is not online and never was here
                if (performSilent) {
                    Bukkit.getLogger().log(Level.WARNING, "No player named {0} found.", playerName);
                } else {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("playername", playerName);
                    String message = LanguageHelper.getLocalizedMessage("no_such_player", replacements);
                    MessageHelper.error(actingSender, message);
                }
                return false;
            }

            List<Perk> enabledPerks = getEnabledPerks(offlinePlayer.getUniqueId());

            if (!enabledPerks.contains(perk)) {
                enabledPerks.add(perk);
            }

            // grant perk permissions
            String permission = "fancyperks.perk." + perkStr.toLowerCase();
            LuckPermsHelper.grantPerkAsync(offlinePlayer.getUniqueId(), permission);

            playerPerks.put(offlinePlayer.getUniqueId(), enabledPerks);

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
            config.set("perks." + offlinePlayer.getUniqueId() + "." + perk.getSystemName(), true);
            try {
                config.save(playersConfig);
            } catch (IOException e) {
                e.printStackTrace();
                if (performSilent) {
                    Bukkit.getLogger().log(Level.WARNING, "Could not save player config.");
                } else {
                    MessageHelper.error(Bukkit.getConsoleSender(), "Could not save player config");
                }
            }

            if (!performSilent) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perk.getDisplayName());
                replacements.put("playername", offlinePlayer.getName());
                String message = LanguageHelper.getLocalizedMessage("perk_granted_and_activated_offline", replacements);
                MessageHelper.success(actingSender, message);              
            }

            return true;

        } else {
            // this is an online player

            Permission permission = FancyPerks.getInstance().getVaultPermission();
            // only give the permission if the player not already has it
            if (!permission.has(player, "fancyperks.perk." + perk.getSystemName())) {
                if (!permission.playerAdd(null, player, "fancyperks.perk." + perk.getSystemName())) {
                    
                    if (performSilent) {
                        Bukkit.getLogger().log(Level.WARNING, "Could not give the perk permissions to player {0}.", playerName);
                    } else {
                        Map<String, String> replacements = new HashMap<>();
                        replacements.put("perkpermission", "fancyperks.perk." + perk.getSystemName());
                        replacements.put("playername", playerName);
                        String message = LanguageHelper.getLocalizedMessage("cannot_give_perk_permissions_to_player", replacements);
                        MessageHelper.warning(actingSender, message);                          
                    }
                    return false;
                }
            }
            
            // case: perk to be activated is already activated
            if (FancyPerks.getInstance().getPerkManager().hasPerkEnabled(player, perk)) {
                if (performSilent) {
                    return true; // in bulk actions, simply ignore this
                } else {
                    if (isAdminCommand) {
                        Map<String, String> replacements = new HashMap<>();
                        replacements.put("perkname", perk.getDisplayName());
                        replacements.put("playername", playerName);
                        String message = LanguageHelper.getLocalizedMessage("target_player_already_activated_perk", replacements);
                        MessageHelper.warning(actingSender, message);
                    } else {
                        Map<String, String> replacements = new HashMap<>();
                        replacements.put("perkname", perk.getDisplayName());
                        replacements.put("playername", playerName);
                        String message = LanguageHelper.getLocalizedMessage("player_already_activated_perk", replacements);
                        MessageHelper.warning(actingSender, message);
                    }
                    return false;
                }
            }

            // case: perk disabled for that world (admin override)
            if(perk.disabledWorlds.contains(player.getWorld().getName())){
                if (!isAdminCommand) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    String message = LanguageHelper.getLocalizedMessage("perk_disabled_in_this_world", replacements);
                    MessageHelper.warning(actingSender, message);
                    return false;
                } else {
                    if (!performSilent) {
                        Map<String, String> replacements = new HashMap<>();
                        replacements.put("perkname", perk.getSystemName());
                        String message = LanguageHelper.getLocalizedMessage("perk_normally_disabled_in_this_world", replacements);
                        MessageHelper.warning(actingSender, message);
                    }
                }
            }
            
            FancyPerks.getInstance().getPerkManager().enablePerk(player, perk);
            perk.forceGrant(player);

            if (!performSilent) {
                if (isAdminCommand) {

                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    replacements.put("playername", playerName);
                    replacements.put("adminname", actingSender.getName());

                    String message = LanguageHelper.getLocalizedMessage("perk_granted_admin", replacements);
                    MessageHelper.success(actingSender, message);

                    message = LanguageHelper.getLocalizedMessage("admin_granted_perk_for_you", replacements);
                    MessageHelper.success(player, message);                    

                } else {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());

                    String message = LanguageHelper.getLocalizedMessage("perk_granted_admin", replacements);
                    MessageHelper.success(actingSender, message);                   
                }
            }
            return true;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public boolean revokePerkFrom(String playerName, String perkStr, boolean isAdminCommand, CommandSender actingSender, boolean performSilent) {
        if (perkStr.equals("*")) {
            for (Perk mperk : PerkRegistry.ALL_PERKS) {
                revokePerkFrom(playerName, mperk.getSystemName(), isAdminCommand, actingSender, performSilent);
            }
            return true;
        }
        
        Perk perk = PerkRegistry.getPerkByName(perkStr);

        Player player = Bukkit.getOnlinePlayers().stream()
            .filter(p -> p.getName().equalsIgnoreCase(playerName))
            .findFirst()
            .orElse(null);

        // check if specified perk exists
        if (perk == null) {
            if (!performSilent) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("perkname", perkStr);
                String message = LanguageHelper.getLocalizedMessage("no_such_perk", replacements);
                MessageHelper.error(actingSender, message);            }
            return false;
        }

        // we have to decide between online and offline players at this point
        if (player == null || !player.isOnline()) {
            // okay, this player is offline.
  
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerName);
            if (!offlinePlayer.hasPlayedBefore()) { 
                // so this player is not online and never was here
                if (!performSilent) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("playername", playerName);
                    String message = LanguageHelper.getLocalizedMessage("no_such_player", replacements);
                    MessageHelper.error(actingSender, message);                }
                return false;
            }

            // revoke perk permissions
            String permission = "fancyperks.perk." + perkStr.toLowerCase();
            LuckPermsHelper.revokePerkAsync(offlinePlayer.getUniqueId(), permission);

            List<Perk> enabledPerks = getEnabledPerks(offlinePlayer.getUniqueId());
            enabledPerks.remove(perk);

            playerPerks.put(offlinePlayer.getUniqueId(), enabledPerks);

            YamlConfiguration config = YamlConfiguration.loadConfiguration(playersConfig);
            config.set("perks." + offlinePlayer.getUniqueId() + "." + perk.getSystemName(), false);
            try {
                config.save(playersConfig);
            } catch (IOException e) {
                e.printStackTrace();
                if (!performSilent) {
                    MessageHelper.error(Bukkit.getConsoleSender(), "Could not save player config");
                }
            }

            if (!performSilent) {
                Map<String, String> replacements = new HashMap<>();
                replacements.put("playername", offlinePlayer.getName());
                replacements.put("perkname", perk.getSystemName());

                String message = LanguageHelper.getLocalizedMessage("perk_revoked_and_deactivated_offline", replacements);
                MessageHelper.success(actingSender, message);

                if (perk instanceof EffectPerk) {
                    replacements = new HashMap<>();
                    replacements.put("perkname", perk.getDisplayName());
                    message = LanguageHelper.getLocalizedMessage("admin_notice_effect_perk_offline", replacements);
                    MessageHelper.info(actingSender, message);                    
                }
            }

            return true;

        } else {
            // this is an online player
            
            Permission permission = FancyPerks.getInstance().getVaultPermission();
            if (!permission.playerRemove(null, player, "fancyperks.perk." + perk.getSystemName())) {
                if (!performSilent) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkpermission", "fancyperks.perk." + perk.getSystemName());
                    replacements.put("playername", playerName);
                    String message = LanguageHelper.getLocalizedMessage("cannot_take_perk_permissions_from_player", replacements);
                    MessageHelper.warning(actingSender, message);
                }
                return false;
            }

            FancyPerks.getInstance().getPerkManager().disablePerk(player, perk);
            perk.revoke(player);

            if (!performSilent) {
                if (isAdminCommand) {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    replacements.put("playername", playerName);
                    replacements.put("adminname", actingSender.getName());

                    String message = LanguageHelper.getLocalizedMessage("perk_revoked_and_deactivated_online", replacements);
                    MessageHelper.success(actingSender, message);

                    message = LanguageHelper.getLocalizedMessage("admin_revoked_perk_from_you", replacements);
                    MessageHelper.success(player, message);

                } else {
                    Map<String, String> replacements = new HashMap<>();
                    replacements.put("perkname", perk.getSystemName());
                    String message = LanguageHelper.getLocalizedMessage("perk_revoked_and_deactivated", replacements);
                    MessageHelper.success(actingSender, message);
                }
            }
            return true;
        }

    }

    public void massGrantPerkAsync(String perkStr, CommandSender actingSender) {
        JavaPlugin plugin = FancyPerks.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean result = massGrantPerkSync(perkStr, actingSender);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (result) {
                    String message = LanguageHelper.getLocalizedMessage("mass_grant_completed", null);
                    MessageHelper.success(actingSender, message);                    
                } else {
                    String message = LanguageHelper.getLocalizedMessage("mass_grant_failed", null);
                    MessageHelper.success(actingSender, message);          
                }
            });
        });
    }

    public void massRevokePerkAsync(String perkStr, CommandSender actingSender) {
        JavaPlugin plugin = FancyPerks.getInstance();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            boolean result = massRevokePerkSync(perkStr, actingSender);

            Bukkit.getScheduler().runTask(plugin, () -> {
                if (result) {
                    String message = LanguageHelper.getLocalizedMessage("mass_revoke_completed", null);
                    MessageHelper.success(actingSender, message);                    
                } else {
                    String message = LanguageHelper.getLocalizedMessage("mass_revoke_failed", null);
                    MessageHelper.success(actingSender, message);          
                }
            });
        });
    }

    public boolean massGrantPerkSync(String perkStr, CommandSender actingSender) {

        if (perkStr.equals("*")) {
            for (Perk perk : PerkRegistry.ALL_PERKS) {
                massGrantPerkSync(perk.getSystemName(), actingSender);
            }
            return true;
        }

        // check if specified perk exists
        Perk perk = PerkRegistry.getPerkByName(perkStr);
        if (perk == null) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("perkname", perkStr);
            String message = LanguageHelper.getLocalizedMessage("no_such_perk", replacements);
            MessageHelper.error(actingSender, message);
            return false;
        }

        List<String> players = Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .filter(name -> name != null)
            .distinct()
            .collect(Collectors.toList());

        int successCount = 0;
        int failedCount = 0;
        int totalPlayerCount = 0;
        List<String> failedPlayers = new ArrayList<>();

        for (String playerName : players) {
            try {
                totalPlayerCount++;
                boolean success = grantPerkTo(playerName, perkStr, true, actingSender, true);
                if (success) {
                    successCount++;
                } else {
                    failedCount++;
                    failedPlayers.add(playerName);
                }
            } catch (Exception e) {
                failedPlayers.add(playerName);
            }
        }
       
        Map<String, String> replacements = new HashMap<>();
        replacements.put("perkname", perkStr);
        replacements.put("successfulplayers", String.valueOf(successCount));
        replacements.put("totalplayers", String.valueOf(totalPlayerCount));
        String message = LanguageHelper.getLocalizedMessage("mass_grant_successful", replacements);
        MessageHelper.success(actingSender, message);

        if (!failedPlayers.isEmpty()) {
            replacements = new HashMap<>();
            replacements.put("failedplayers", String.valueOf(failedCount));
            message = LanguageHelper.getLocalizedMessage("mass_action_failed_players", replacements);
            MessageHelper.error(actingSender, message);

            Bukkit.getLogger().log(Level.WARNING, "Failed granting perks for {0} perk for these players'':", perkStr);
            for (String failed : failedPlayers) {
                Bukkit.getLogger().log(Level.WARNING, "- {0}", failed);
            }
        }
        return true;
    }

    public boolean massRevokePerkSync(String perkStr, CommandSender actingSender) {
        
        if (perkStr.equals("*")) {
            for (Perk perk : PerkRegistry.ALL_PERKS) {
                massRevokePerkSync(perk.getSystemName(), actingSender);
            }
            return true;
        }

        List<String> players = Arrays.stream(Bukkit.getOfflinePlayers())
            .map(OfflinePlayer::getName)
            .filter(name -> name != null)
            .distinct()
            .collect(Collectors.toList());

        int successCount = 0;
        int failedCount = 0;
        int totalPlayerCount = 0;

        List<String> failedPlayers = new ArrayList<>();

        for (String playerName : players) {
            try {
                totalPlayerCount++;
                boolean success = revokePerkFrom(playerName, perkStr, true, actingSender, true);
                if (success) {
                    successCount++;
                } else {
                    failedCount++;
                    failedPlayers.add(playerName);
                }
            } catch (Exception e) {
                failedPlayers.add(playerName);
            }
        }

        Map<String, String> replacements = new HashMap<>();
        replacements.put("perkname", perkStr);
        replacements.put("successfulplayers", String.valueOf(successCount));
        replacements.put("totalplayers", String.valueOf(totalPlayerCount));
        String message = LanguageHelper.getLocalizedMessage("mass_revoke_successful", replacements);
        MessageHelper.success(actingSender, message);

        if (!failedPlayers.isEmpty()) {
            replacements = new HashMap<>();
            replacements.put("failedplayers", String.valueOf(failedCount));
            message = LanguageHelper.getLocalizedMessage("mass_action_failed_players", replacements);
            MessageHelper.error(actingSender, message);

            Bukkit.getLogger().log(Level.WARNING, "Failed granting perks for ''{0} perk for these players'':", perkStr);
            for (String failed : failedPlayers) {
                Bukkit.getLogger().log(Level.WARNING, "- {0}", failed);
            }
        }
        return true;
    }

}
