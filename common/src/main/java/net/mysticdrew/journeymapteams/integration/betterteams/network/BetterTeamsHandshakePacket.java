package net.mysticdrew.journeymapteams.integration.betterteams.network;

import commonnetwork.networking.data.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;
import java.util.function.BiConsumer;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

/**
 * Client -> Server: announces that this client has the BetterTeams addon installed.
 * Carries the addon version string so the server can gate sync on version compatibility.
 *
 * <p>The actual handler logic lives in {@link ServerHook} to keep this packet class
 * free of Paper-side dependencies. The Paper plugin calls {@link ServerHook#register}
 * at init time to install the real callback.</p>
 *
 * <p>The {@code handle(PacketContext)} method delegates to {@link ServerHook#onHandshake}
 * so loader-level code only needs to call {@link ServerHook#register} once at init time.</p>
 */
public record BetterTeamsHandshakePacket(String addonVersion) implements CustomPacketPayload
{
    public static final Identifier CHANNEL =
            Identifier.fromNamespaceAndPath(MOD_ID, "bt_handshake");
    public static final CustomPacketPayload.Type<BetterTeamsHandshakePacket> TYPE =
            new CustomPacketPayload.Type<>(CHANNEL);
    public static final StreamCodec<RegistryFriendlyByteBuf, BetterTeamsHandshakePacket> STREAM_CODEC =
            StreamCodec.ofMember(BetterTeamsHandshakePacket::encode, BetterTeamsHandshakePacket::new);

    public BetterTeamsHandshakePacket(RegistryFriendlyByteBuf buf)
    {
        this(buf.readUtf());
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public void encode(RegistryFriendlyByteBuf buf)
    {
        buf.writeUtf(addonVersion);
    }

    public static void handle(PacketContext<BetterTeamsHandshakePacket> ctx)
    {
        ServerPlayer player = ctx.sender();
        if (player == null)
        {
            return;
        }
        ServerHook.onHandshake(player.getUUID(), ctx.message().addonVersion());
    }

    /**
     * Indirection layer: the Paper plugin registers a real handler at startup;
     * everything else sees a no-op by default. Loader-level code calls
     * {@link #onHandshake(UUID, String)} from the platform-specific handle method.
     */
    public static final class ServerHook
    {
        private static BiConsumer<UUID, String> handler = (uuid, version) -> {
        };

        private ServerHook()
        {
        }

        public static void register(BiConsumer<UUID, String> h)
        {
            handler = h;
        }

        public static void onHandshake(UUID playerUuid, String addonVersion)
        {
            handler.accept(playerUuid, addonVersion);
        }
    }
}
