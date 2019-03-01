package mods.flammpfeil.slashblade.network;

import mods.flammpfeil.slashblade.SlashBlade;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * Created by Furia on 14/06/09.
 */
public class NetworkManager {
    public static final ResourceLocation SLASHBLADE_MESSAGE_RESOURCE = new ResourceLocation(SlashBlade.modname, "flammpfeil.sb");
    public static final String NETVERSION = "SB1";

    //public static final SimpleNetworkWrapper channel = NetworkRegistry.channel.newSimpleChannel(SlashBlade.modid);
    public static SimpleChannel channel;

    public static void init() {
        channel = NetworkRegistry.ChannelBuilder.named(SLASHBLADE_MESSAGE_RESOURCE).
                clientAcceptedVersions(a -> true).
                serverAcceptedVersions(a -> true).
                networkProtocolVersion(() -> NetworkManager.NETVERSION).
                simpleChannel();

        channel.messageBuilder(C2SRangeAttack.class, 0).
                decoder(C2SRangeAttack::decoder).
                encoder(C2SRangeAttack::encoder).
                consumer(new C2SRangeAttackHandler()).
                add();

        channel.messageBuilder(C2SSpecialAction.class, 1).
                decoder(C2SSpecialAction::decoder).
                encoder(C2SSpecialAction::encoder).
                consumer(new C2SSpecialActionHandler()).
                add();

        channel.messageBuilder(C2SMoveCommandState.class, 2).
                decoder(C2SMoveCommandState::decoder).
                encoder(C2SMoveCommandState::encoder).
                consumer(new C2SMoveCommandStateHandler()).
                add();

        channel.messageBuilder(S2CRankpointSynchronize.class, 3).
                decoder(S2CRankpointSynchronize::decoder).
                encoder(S2CRankpointSynchronize::encoder).
                consumer(new S2CRankpointSynchronizeHandler()).
                add();

    }
}
