package net.mysticdrew.journeymapteams.integration.betterteams.network;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.mysticdrew.journeymapteams.handlers.MinecraftBootstrap;
import net.mysticdrew.journeymapteams.integration.betterteams.MemberRecord;
import net.mysticdrew.journeymapteams.integration.betterteams.TeamRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BetterTeamsSnapshotPacketCodecTest
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
    void roundtrip_preserves_teams_and_members()
    {
        TeamRecord redTeam = new TeamRecord("red-uuid", "Red", 0xFF0000, List.of("blue-uuid"));
        TeamRecord blueTeam = new TeamRecord("blue-uuid", "Blue", 0x0000FF, List.of());
        UUID aliceUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        MemberRecord aliceMember = new MemberRecord(aliceUuid, "red-uuid");

        BetterTeamsSnapshotPacket original = new BetterTeamsSnapshotPacket(
                List.of(redTeam, blueTeam),
                List.of(aliceMember));

        RegistryFriendlyByteBuf buf = newBuf();
        BetterTeamsSnapshotPacket.STREAM_CODEC.encode(buf, original);
        BetterTeamsSnapshotPacket decoded = BetterTeamsSnapshotPacket.STREAM_CODEC.decode(buf);

        assertEquals(2, decoded.teams().size());
        assertEquals(redTeam, decoded.teams().get(0));
        assertEquals(blueTeam, decoded.teams().get(1));

        assertEquals(1, decoded.members().size());
        assertEquals(aliceMember, decoded.members().get(0));
    }

    @Test
    void roundtrip_empty_snapshot()
    {
        BetterTeamsSnapshotPacket original = new BetterTeamsSnapshotPacket(List.of(), List.of());

        RegistryFriendlyByteBuf buf = newBuf();
        BetterTeamsSnapshotPacket.STREAM_CODEC.encode(buf, original);
        BetterTeamsSnapshotPacket decoded = BetterTeamsSnapshotPacket.STREAM_CODEC.decode(buf);

        assertEquals(0, decoded.teams().size());
        assertEquals(0, decoded.members().size());
    }
}
