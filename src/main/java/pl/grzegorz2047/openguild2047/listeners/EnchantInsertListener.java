package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

/**
 * Created by grzeg on 25.08.2017.
 */
public class EnchantInsertListener implements Listener {


    @EventHandler
    private void onEnchant(PotionSplashEvent e) {
        ProjectileSource shooter = e.getPotion().getShooter();
        if (e.getPotion().getItem().getDurability() == 41) {
            Player p = (Player) shooter;
            p.sendMessage(MsgManager.get("strengthenchantblocked"));
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onDrink(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        if (e.getItem().getDurability() == 41) {
            player.sendMessage(MsgManager.get("strengthenchantblocked"));
            e.setCancelled(true);
        }
    }

}
