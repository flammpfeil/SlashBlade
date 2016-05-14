package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.network.MessageRangeAttack;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import mods.flammpfeil.slashblade.network.MessageSpecialAction;
import mods.flammpfeil.slashblade.network.MessageSpecialActionHandler;

/**
 * Created by Furia on 14/06/09.
 */
public class NetworkManager {
    //public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(SlashBlade.modid);
    public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel("flammpfeil.sb");


    public static void init() {
        INSTANCE.registerMessage(MessageRangeAttackHandler.class, MessageRangeAttack.class, 0, Side.SERVER);
        INSTANCE.registerMessage(MessageSpecialActionHandler.class, MessageSpecialAction.class, 1, Side.SERVER);
        INSTANCE.registerMessage(MessageMoveCommandStateHandler.class, MessageMoveCommandState.class, 2, Side.SERVER);
    }
}
