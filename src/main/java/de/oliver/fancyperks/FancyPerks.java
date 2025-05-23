package de.oliver.fancyperks;

import java.util.List;

import org.apache.maven.artifact.versioning.ComparableVersion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

        new Thread(() -> {
            ComparableVersion newestVersion = versionFetcher.fetchNewestVersion();
            ComparableVersion currentVersion = new ComparableVersion(getDescription().getVersion());
            if (newestVersion == null) {
                getLogger().warning("Could not fetch latest plugin version");
            } else if (newestVersion.compareTo(currentVersion) > 0) {
                getLogger().warning("-------------------------------------------------------");
                getLogger().warning("You are not using the latest version the FancyPerks plugin.");
                getLogger().warning("Please update to the newest version (" + newestVersion + ").");
                getLogger().warning(versionFetcher.getDownloadUrl());
                getLogger().warning("-------------------------------------------------------");
            }
        }).start();

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
            } else {
                usingLuckPerms = false;
            }
        }


        Metrics metrics = new Metrics(instance, 18195);

        config.reload();

        languageConfig.addDefaultLang("gui_perk_is_enabled", "<gradient:dark_green:green>Perk is enabled</gradient>");
        languageConfig.addDefaultLang("gui_perk_is_disabled", "<gradient:dark_red:red>Perk is disabled</gradient>");
        languageConfig.addDefaultLang("gui_click_to_toggle", "<yellow>Click to toggle</yellow>");
        languageConfig.addDefaultLang("gui_you_dont_own_this_perk", "<gradient:gold:yellow>You don't own this perk</gradient>");
        languageConfig.addDefaultLang("gui_price", "<yellow>Price: ${price}</yellow>");
        languageConfig.addDefaultLang("gui_click_to_buy", "<yellow>Click to buy</yellow>");
        languageConfig.addDefaultLang("gui_previous_page", "<yellow>Previous page</yellow>");
        languageConfig.addDefaultLang("gui_next_page", "<yellow>Next page</yellow>");
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
}
