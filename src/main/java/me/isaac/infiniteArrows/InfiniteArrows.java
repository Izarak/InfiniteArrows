package me.isaac.infiniteArrows;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class InfiniteArrows extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    }

    @EventHandler (priority = EventPriority.MONITOR)
    public void onShootBow(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player player)) return;
        if (player.getGameMode() == GameMode.CREATIVE) return;
        if (e.getConsumable() == null) return;
        if (e.getConsumable().getType() == Material.ARROW) return;
        if (e.getBow() == null
            || !e.getBow().containsEnchantment(Enchantment.INFINITY)) return;
        e.setConsumeItem(false); // In case other plugins need EntityShootBowEvent#shouldConsumeItem()

        Entity projectile = e.getProjectile();
        if (projectile instanceof AbstractArrow arrow)
            arrow.setPickupStatus(AbstractArrow.PickupStatus.DISALLOWED);

        int slot = getUsedSlot(player);

        if (slot == -1 || !e.getConsumable().isSimilar(player.getInventory().getItem(slot))) {
            Map<Integer, ItemStack> items = player.getInventory().addItem(e.getConsumable());
            if (!items.isEmpty())
                for (ItemStack value : items.values()) {
                    player.getWorld().dropItemNaturally(player.getLocation(), value);
                }
            return;
        }

        ItemStack item = player.getInventory().getItem(slot);

        if (item.getAmount() < 64)
            item.setAmount(item.getAmount() + 1);
    }

    private int getUsedSlot(Player player) {
        if (player.getInventory().getItemInOffHand().getType() == Material.SPECTRAL_ARROW
            || player.getInventory().getItemInOffHand().getType() == Material.TIPPED_ARROW)
            return 40;
        for (int i = 0; i < 36; i++) {
            ItemStack inventoryItem = player.getInventory().getItem(i);
            if (inventoryItem == null) continue;
            if (inventoryItem.getType() == Material.SPECTRAL_ARROW
                || inventoryItem.getType() == Material.TIPPED_ARROW)
                return i;
        }
        return -1;
    }

}
