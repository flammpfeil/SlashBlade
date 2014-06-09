package mods.flammpfeil.slashblade;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

/**
 * Created by Furia on 14/06/09.
 */
public class PacketHandler {
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SlashBlade.modid);


    public static void init() {
        INSTANCE.registerMessage(MessageRangeAttack.class, MessageRangeAttack.class, 0, Side.SERVER);
    }
}
