package pl.grzegorz2047.openguild2047.packets;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by grzeg on 19.08.2017.
 */
public class PacketCoordinator {

    public void sendPacketToPlayer(Player p, PacketContainer packet) {
        try {
            ProtocolLibrary.getProtocolManager()
                    .sendServerPacket(p, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

}
