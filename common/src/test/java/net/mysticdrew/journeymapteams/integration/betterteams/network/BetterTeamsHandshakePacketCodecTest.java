package net.mysticdrew.journeymapteams.integration.betterteams.network;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.mysticdrew.journeymapteams.handlers.MinecraftBootstrap;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BetterTeamsHandshakePacketCodecTest
{
    @BeforeAll
    static void bootstrap()
    {
        MinecraftBootstrap.boot();
    }

    private RegistryFriendlyByteBuf newBuf()
    {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), RegistryAccess.EMPTY);
    }

    @Test
    void roundtrip_preserves_addonVersion()
    {
        BetterTeamsHandshakePacket original = new BetterTeamsHandshakePacket("1.2.3");

        RegistryFriendlyByteBuf buf = newBuf();
        BetterTeamsHandshakePacket.STREAM_CODEC.encode(buf, original);
        BetterTeamsHandshakePacket decoded = BetterTeamsHandshakePacket.STREAM_CODEC.decode(buf);

        assertEquals("1.2.3", decoded.addonVersion());
    }
}
