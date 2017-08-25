package pl.grzegorz2047.openguild2047.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.Potion;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.projectiles.ProjectileSource;
import pl.grzegorz2047.openguild2047.managers.MsgManager;

import java.util.Collection;

/**
 * Created by grzeg on 25.08.2017.
 */
public class EnchantInsertListener implements Listener {


    @EventHandler
    private void onEnchant(PotionSplashEvent e) {
        ThrownPotion potion = e.getPotion();

        Collection<PotionEffect> effects = potion.getEffects();
        boolean isStrength = isStrength(effects);
        if (!isStrength) {
            return;
        }
        if (!(potion.getShooter() instanceof Player)) {
            blockEvent(e, potion);
            return;
        }
        Player shooter = (Player) potion.getShooter();
        blockEvent(e, potion);
        shooter.sendMessage(MsgManager.get("strengthenchantblocked"));
    }

    private void blockEvent(PotionSplashEvent e, ThrownPotion potion) {
        e.getAffectedEntities().clear();
        e.setCancelled(true);
        potion.setItem(new ItemStack(Material.SPLASH_POTION));
    }


    @EventHandler
    private void onDrink(BrewEvent e) {
        boolean glowstoneDust = false;
        boolean potion = false;
        for (ItemStack it : e.getContents()) {

            if (it != null) {
                Material type = it.getType();
                if (type.equals(Material.GLOWSTONE_DUST)) {
                    glowstoneDust = true;
                }
                if (it.getData().getItemTypeId() == 373) {
                    potion = true;
                }
            }
        }
        if (potion && glowstoneDust) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    private void onDrink(PlayerItemConsumeEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();

        if (item.getType().equals(Material.POTION)) {
            Potion p = Potion.fromItemStack(item);
//            System.out.println("Potion! " + p.getType().name());
            if (p.getType().equals(PotionType.STRENGTH)) {
//                System.out.println("Strength!");
                if (p.getLevel() >= 2) {
//                    System.out.println("2");
                    e.setCancelled(true);
                    e.setItem(new ItemStack(Material.AIR));
                    player.sendMessage(MsgManager.get("strengthenchantblocked"));

                } else {
//                    System.out.println("inny!");
                }
            } else {
                //System.out.println("inny nie strength");
            }
        } else {
//            System.out.println("nie potka!");
        }
    }

    private boolean isStrength(Collection<PotionEffect> effects) {
        for (PotionEffect potionEffect : effects) {
//            System.out.println("Typ efektu " + potionEffect.getType().getName());
            if (potionEffect.getType().equals(PotionEffectType.INCREASE_DAMAGE)) {
                int level = potionEffect.getAmplifier();
                if (level >= 1) {
                    return true;
                }
            }
        }
        return false;
    }


}
