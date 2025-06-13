package de.oliver.fancyperks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import de.oliver.fancylib.FancyLib;
import de.oliver.fancylib.LanguageConfig;
import de.oliver.fancylib.Metrics;
import de.oliver.fancylib.serverSoftware.ServerSoftware;
import de.oliver.fancylib.serverSoftware.schedulers.BukkitScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FancyScheduler;
import de.oliver.fancylib.serverSoftware.schedulers.FoliaScheduler;
import de.oliver.fancylib.versionFetcher.MasterVersionFetcher;
import de.oliver.fancylib.versionFetcher.VersionFetcher;
import de.oliver.fancyperks.commands.FancyPerksCMD;
import de.oliver.fancyperks.commands.PerksCMD;
import de.oliver.fancyperks.gui.inventoryClick.BuyPerkInventoryItemClick;
import de.oliver.fancyperks.gui.inventoryClick.TogglePerkInventoryItemClick;
import de.oliver.fancyperks.listeners.BlockBreakListener;
import de.oliver.fancyperks.listeners.BlockDropItemListener;
import de.oliver.fancyperks.listeners.BlockPlaceListener;
import de.oliver.fancyperks.listeners.EntityDamageListener;
import de.oliver.fancyperks.listeners.EntityDeathListener;
import de.oliver.fancyperks.listeners.EntityPotionEffectListener;
import de.oliver.fancyperks.listeners.EntityTargetLivingEntityListener;
import de.oliver.fancyperks.listeners.FoodLevelChangeListener;
import de.oliver.fancyperks.listeners.LuckPermsListener;
import de.oliver.fancyperks.listeners.PlayerChangedWorldListener;
import de.oliver.fancyperks.listeners.PlayerDeathListener;
import de.oliver.fancyperks.listeners.PlayerItemDamageListener;
import de.oliver.fancyperks.listeners.PlayerJoinListener;
import de.oliver.fancyperks.listeners.PlayerMoveListener;
import de.oliver.fancyperks.perks.Perk;
import de.oliver.fancyperks.perks.PerkRegistry;
import de.oliver.fancyperks.perks.impl.LavaRunnerPerk;
import net.luckperms.api.LuckPerms;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class FancyPerks extends JavaPlugin {

    private static FancyPerks instance;
    private final PerkManager perkManager;
    private final VersionFetcher versionFetcher;
    private final FancyScheduler fancyScheduler;
    private final FancyPerksConfig config;
    private final LanguageConfig languageConfig;
    private boolean usingVault;
    private Economy vaultEconomy;
    private Permission vaultPermission;
    private boolean usingLuckPerms;
    private LuckPerms luckPerms;

    public FancyPerks() {
        instance = this;

        perkManager = new PerkManager();
        versionFetcher = new MasterVersionFetcher("FancyPerks");
        fancyScheduler = ServerSoftware.isFolia() ?
                new FoliaScheduler(instance) :
                new BukkitScheduler(instance);
        config = new FancyPerksConfig();
        languageConfig = new LanguageConfig(instance);
        usingVault = false;
    }

    public static FancyPerks getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        FancyLib.setPlugin(this);

        PluginManager pluginManager = Bukkit.getPluginManager();

        if (!ServerSoftware.isPaper()) {
            getLogger().warning("--------------------------------------------------");
            getLogger().warning("It is recommended to use Paper as server software.");
            getLogger().warning("Because you are not using paper, the plugin");
            getLogger().warning("might not work correctly.");
            getLogger().warning("--------------------------------------------------");
        }

        usingVault = pluginManager.getPlugin("Vault") != null;
        if (usingVault) {
            RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(Economy.class);
            if (economyProvider != null) {
                vaultEconomy = economyProvider.getProvider();
                getLogger().info("Found vault economy");
            } else {
                usingVault = false;
                getLogger().warning("Could not find any economy plugin");
            }

            RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServicesManager().getRegistration(Permission.class);
            if (permissionProvider != null) {
                vaultPermission = permissionProvider.getProvider();
                getLogger().info("Found vault permission");
            } else {
                usingVault = false;
                getLogger().warning("Could not find any permission plugin");
            }
        }

        usingLuckPerms = pluginManager.isPluginEnabled("LuckPerms");
        if (usingLuckPerms) {
            RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
            if (provider != null) {
                luckPerms = provider.getProvider();
                LuckPermsHelper.luckPerms = luckPerms;
            } else {
                usingLuckPerms = false;
            }
        }


        @SuppressWarnings("unused")
        Metrics metrics = new Metrics(instance, 18195);

        config.reload();

        languageConfig.addDefaultLang("activated_all_perks", "Activated all your perks (<red>${perklist}</red>).");
        languageConfig.addDefaultLang("activated_perk_for_offline_player", "Activated the <red>${perkname}</red> perk for OFFLINE player <red>${playername}</red>.");
        languageConfig.addDefaultLang("activated_perk_for_online_player", "Activated the <red>${perkname}</red> perk for player <red>${playername}</red>.");
        languageConfig.addDefaultLang("admin_activated_perk_for_you", "<red>${adminname}</red> activated the <red>${perkname}</red> perk for you.");
        languageConfig.addDefaultLang("admin_deactivated_your_perk", "<red>${adminname}</red> deactivated your <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("admin_granted_perk_for_you", "<red>${adminname}</red> granted permission and activated the <red>${perkname}</red> perk for you.");
        languageConfig.addDefaultLang("admin_notice_effect_perk_offline", "Keep in mind that the <red>${perkname}</red> perk is an effect perk. It is not possible to change effects of offline players, so the effect MIGHT remain active, even if the perk is not!");
        languageConfig.addDefaultLang("admin_revoked_perk_from_you", "<red>${adminname}</red> revoked permission and deactivated the <red>${perkname}</red> perk from you.");
        languageConfig.addDefaultLang("block_will_drop_with_drop_more_blocks_perk", "That block will only drop with the <red>drop_more_blocks</red> perk activated.");
        languageConfig.addDefaultLang("block_will_drop_with_drop_spawners_perk", "That block will only drop with the <red>drop_spawners</red> perk activated.");
        languageConfig.addDefaultLang("cannot_give_perk_permissions_to_player", "Could not give the perk permission <red>${perkpermissions}</red> to player <red>${playername}</red>.");
        languageConfig.addDefaultLang("cannot_take_perk_permissions_from_player", "Could not take the perk permission <red>${perkpermissions}</red> from player <red>${playername}</red>.");
        languageConfig.addDefaultLang("configuration_reloaded", "Reloaded the configuration.");
        languageConfig.addDefaultLang("deactivated_all_perks", "Deactivated all your perks (<red>${perklist}</red>).");
        languageConfig.addDefaultLang("deactivated_perk_for_offline_player", "Deactivated the <red>${perkname}</red> perk for OFFLINE player <red>${playername}</red>.");
        languageConfig.addDefaultLang("deactivated_perk_for_online_player", "Deactivated the <red>${perkname}</red> perk for player <red>${playername}</red>.");
        languageConfig.addDefaultLang("gui_click_to_buy", "<yellow>Click to buy</yellow>");
        languageConfig.addDefaultLang("gui_click_to_toggle", "<yellow>Click to toggle</yellow>");
        languageConfig.addDefaultLang("gui_next_page", "<yellow>Next page</yellow>");
        languageConfig.addDefaultLang("gui_perk_is_disabled", "<gradient:dark_red:red>Perk is disabled</gradient>");
        languageConfig.addDefaultLang("gui_perk_is_enabled", "<gradient:dark_green:green>Perk is enabled</gradient>");
        languageConfig.addDefaultLang("gui_previous_page", "<yellow>Previous page</yellow>");
        languageConfig.addDefaultLang("gui_price", "<yellow>Price: ${price}</yellow>");
        languageConfig.addDefaultLang("gui_you_dont_own_this_perk", "<gradient:gold:yellow>You don't own this perk</gradient>");
        languageConfig.addDefaultLang("mass_action_failed_players", "With <red>${failedplayers}</red> players, that did not work, see console for list of affected players.");
        languageConfig.addDefaultLang("mass_grant_completed", "Mass grant completed.");
        languageConfig.addDefaultLang("mass_grant_failed", "Mass grant failed.");
        languageConfig.addDefaultLang("mass_grant_successful", "Successfully granted <red>${perkname}</red> perk for <red>${successfulplayers} players</red> of <red>${totalplayers} players</red>.");
        languageConfig.addDefaultLang("mass_revoke_completed", "Mass revoke completed.");
        languageConfig.addDefaultLang("mass_revoke_failed", "Mass revoke failed.");
        languageConfig.addDefaultLang("mass_revoke_successful", "Successfully revoked <red>${perkname}</red> perk for <red>${successfulplayers} players</red> of <red>${totalplayers} players</red>.");
        languageConfig.addDefaultLang("no_perk_permission", "You don't have the permission to use this perk.");
        languageConfig.addDefaultLang("no_such_perk", "There is no perk named <red>${perkname}</red>.");
        languageConfig.addDefaultLang("no_such_player", "There is no player named <red>${playername}</red>.");
        languageConfig.addDefaultLang("not_enough_money", "You don't have enough money to buy this perk.");
        languageConfig.addDefaultLang("perk_automatically_disabled", "Automatically disabled the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("perk_automatically_enabled", "Automatically enabled the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("perk_disabled_in_this_world", "The <red>${perkname}</red> perk is disabled in this world.");
        languageConfig.addDefaultLang("perk_granted_admin", "Granted permission and activated the <red>${perkname}</red> perk for player <red>${playername}</red>.");
        languageConfig.addDefaultLang("perk_granted_and_activated", "Granted permissions and activated the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("perk_granted_and_activated_offline", "Granted and activated the <red>${perkname}</red> perk for OFFLINE player <red>${playername}</red>.");
        languageConfig.addDefaultLang("perk_list_active_perks", "The player <red>${playername}</red> has these ACTIVE perks:");
        languageConfig.addDefaultLang("perk_list_entry_active_perk", "- <gold>${perkname}</gold>");
        languageConfig.addDefaultLang("perk_list_entry_inactive_perk", "- <gray>${perkname}</gray>");
        languageConfig.addDefaultLang("perk_list_inactive_perks", "and these INACTIVE perks:");
        languageConfig.addDefaultLang("perk_normally_disabled_in_this_world", "Normally, the <red>${perkname}</red> perk would be disabled in that world.<newline>But as you are admin, you (hopefully) know what you do, so the plugin will allow this.");
        languageConfig.addDefaultLang("perk_revoked_and_deactivated", "Revoked permission and deactivated the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("perk_revoked_and_deactivated_offline", "Revoked permission and deactivated the <red>${perkname}</red> perk for OFFLINE player <red>${playername}</red>.");
        languageConfig.addDefaultLang("perk_revoked_and_deactivated_online", "Revoked permission and deactivated the <red>${perkname}</red> perk for player <red>${playername}</red>.");
        languageConfig.addDefaultLang("perk_successfully_activated", "Successfully activated the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("perk_successfully_deactivated", "Successfully deactivated the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("permission_unsuccessful", "Cannot not give you the perk permission.");
        languageConfig.addDefaultLang("player_already_activated_perk", "You already activated the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("player_configuration_invalid", "The perk configuration of player <red>${playername}</red> seems to be invalid.");
        languageConfig.addDefaultLang("player_dealready_activated_perk", "You already deactivated the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("player_has_no_perks_yet", "The player <red>${playername}</red> has no perks yet (or never used one of them).");
        languageConfig.addDefaultLang("prefix","<gradient:dark_green:green>[FancyPerks]</gradient>");
        languageConfig.addDefaultLang("sneak_to_destroy_block", "To <red>DESTROY</red> the block, mine it while sneaking.");
        languageConfig.addDefaultLang("target_player_already_activated_perk", "The player <red>${playername}</red> has already activated the <red>${perkname}</red> perk.");
        languageConfig.addDefaultLang("transaction_unsuccessful", "The transaction was not successful.");
        languageConfig.addDefaultLang("version_information", "You are using <red>FancyPerks</red>, version <red>${versioninfo}</red>.");
        languageConfig.load();

        getCommand("fancyperks").setExecutor(new FancyPerksCMD());
        getCommand("perks").setExecutor(new PerksCMD());

        pluginManager.registerEvents(new PlayerJoinListener(), instance);
        pluginManager.registerEvents(new EntityPotionEffectListener(), instance);
        pluginManager.registerEvents(new PlayerDeathListener(), instance);
        pluginManager.registerEvents(new FoodLevelChangeListener(), instance);
        pluginManager.registerEvents(new EntityDamageListener(), instance);
        pluginManager.registerEvents(new EntityDeathListener(), instance);
        pluginManager.registerEvents(new EntityTargetLivingEntityListener(), instance);
        pluginManager.registerEvents(new BlockDropItemListener(), instance);
        pluginManager.registerEvents(new PlayerItemDamageListener(), instance);
        pluginManager.registerEvents(new BlockBreakListener(), instance);
        pluginManager.registerEvents(new PlayerMoveListener(), instance);
        pluginManager.registerEvents(new PlayerChangedWorldListener(), instance);
        pluginManager.registerEvents(new BlockPlaceListener(), instance);
        if (usingLuckPerms && config.isActivatePerkOnPermissionSet()) {
            new LuckPermsListener();
        }


        TogglePerkInventoryItemClick.INSTANCE.register();
        BuyPerkInventoryItemClick.INSTANCE.register();

        perkManager.loadFromConfig();

        if (PerkRegistry.LAVA_RUNNER.isEnabled()) {
            LavaRunnerPerk lavaRunner = (LavaRunnerPerk) PerkRegistry.LAVA_RUNNER;
            fancyScheduler.runTaskTimerAsynchronously(10, 10, () -> {
                for (Player player : lavaRunner.getPlayerBlockCache().keySet()) {
                    List<Perk> perks = perkManager.getEnabledPerks(player);
                    if (perks.contains(PerkRegistry.LAVA_RUNNER)) {
                        (lavaRunner).updateBlocks(player);
                    }
                }
            });
        }
    }

    public PerkManager getPerkManager() {
        return perkManager;
    }

    public VersionFetcher getVersionFetcher() {
        return versionFetcher;
    }

    public FancyScheduler getFancyScheduler() {
        return fancyScheduler;
    }

    public FancyPerksConfig getFanyPerksConfig() {
        return config;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }

    public boolean isUsingVault() {
        return usingVault;
    }

    public Economy getVaultEconomy() {
        return vaultEconomy;
    }

    public Permission getVaultPermission() {
        return vaultPermission;
    }

    public boolean isUsingLuckPerms() {
        return usingLuckPerms;
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }

    public String itemOrDefault(String itemName, String fallbackItemName) {
        // we support player heads with custom textures, so PLAYER_HEAD:texture=.... is normally no valid item, but we handle it later
        if (itemName.toUpperCase().startsWith("PLAYER_HEAD")) {
            return itemName;
        }

        try {
            @SuppressWarnings("unused")
            Material material = Material.valueOf(itemName.toUpperCase());
            return itemName;
        } catch (IllegalArgumentException | NullPointerException e) {
            return fallbackItemName;
        }
    }

    public ItemStack createCustomSkull(String itemString) {
        // if this is not a player_head - return default item
        if (!itemString.startsWith("PLAYER_HEAD:")) {
            return new ItemStack(Material.BARRIER);
        }

        String data = itemString.substring("PLAYER_HEAD:".length());
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        if (data.startsWith("owner=")) {
            String playerName = data.substring("owner=".length());
            meta.setOwnerProfile(Bukkit.createPlayerProfile(playerName));
        }

        skull.setItemMeta(meta);
        return skull;
    }

}