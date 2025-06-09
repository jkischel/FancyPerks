package de.oliver.fancyperks;

/**
 *
 * @author DatLicht
 */
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.types.PermissionNode;

public class LuckPermsHelper {

    @SuppressWarnings("unused")
    private final JavaPlugin plugin;
    public static LuckPerms luckPerms;

    public LuckPermsHelper(JavaPlugin plugin, LuckPerms luckPerms) {
        this.plugin = plugin;
        this.luckPerms = luckPerms;
    }

    public static void grantPerkAsync(UUID uuid, String permissionNode) {
        loadUserAsync(uuid).thenAccept(user -> {
            Node node = PermissionNode.builder(permissionNode).value(true).build();
            user.data().add(node);
            saveUser(user);
        });
    }

    public static void revokePerkAsync(UUID uuid, String permissionNode) {
        loadUserAsync(uuid).thenAccept(user -> {
            Node node = PermissionNode.builder(permissionNode).value(true).build();
            user.data().remove(node);
            saveUser(user);
        });
    }

    private static CompletableFuture<User> loadUserAsync(UUID uuid) {
        return luckPerms.getUserManager().loadUser(uuid);
    }

    private static void saveUser(User user) {
        luckPerms.getUserManager().saveUser(user);
    }

    public static boolean hasPerk(UUID uuid, String permissionNode) {
        User user = luckPerms.getUserManager().getUser(uuid);
        if (user == null) return false;
        return user.getCachedData().getPermissionData().checkPermission(permissionNode).asBoolean();
    }

    public static void grantPerkToPlayerAsync(Player player, String permissionNode) {
        grantPerkAsync(player.getUniqueId(), permissionNode);
    }

    public static void revokePerkFromPlayerAsync(Player player, String permissionNode) {
        revokePerkAsync(player.getUniqueId(), permissionNode);
    }

    public static void grantPerkToOfflineAsync(OfflinePlayer player, String permissionNode) {
        grantPerkAsync(player.getUniqueId(), permissionNode);
    }

    public static void revokePerkFromOfflineAsync(OfflinePlayer player, String permissionNode) {
        revokePerkAsync(player.getUniqueId(), permissionNode);
    }
}
