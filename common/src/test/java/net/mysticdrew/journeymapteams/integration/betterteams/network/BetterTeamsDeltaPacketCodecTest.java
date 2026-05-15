package net.mysticdrew.journeymapteams.integration.betterteams.network;

import io.netty.buffer.Unpooled;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.mysticdrew.journeymapteams.handlers.MinecraftBootstrap;
import net.mysticdrew.journeymapteams.integration.betterteams.DeltaKind;
import net.mysticdrew.journeymapteams.integration.betterteams.MemberRecord;
import net.mysticdrew.journeymapteams.integration.betterteams.TeamRecord;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BetterTeamsDeltaPacketCodecTest
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
    void roundtrip_teamUpsert()
    {
        TeamRecord team = new TeamRecord("red-uuid", "Red", 0xFF0000, List.of("blue-uuid"));
        BetterTeamsDeltaPacket original = BetterTeamsDeltaPacket.teamUpsert(team);

        RegistryFriendlyByteBuf buf = newBuf();
        BetterTeamsDeltaPacket.STREAM_CODEC.encode(buf, original);
        BetterTeamsDeltaPacket decoded = BetterTeamsDeltaPacket.STREAM_CODEC.decode(buf);

        assertEquals(DeltaKind.TEAM_UPSERT, decoded.kind());
        assertTrue(decoded.team().isPresent());
        assertEquals(team, decoded.team().get());
    }

    @Test
    void roundtrip_teamRemove()
    {
        BetterTeamsDeltaPacket original = BetterTeamsDeltaPacket.teamRemove("red-uuid");

        RegistryFriendlyByteBuf buf = newBuf();
        BetterTeamsDeltaPacket.STREAM_CODEC.encode(buf, original);
        BetterTeamsDeltaPacket decoded = BetterTeamsDeltaPacket.STREAM_CODEC.decode(buf);

        assertEquals(DeltaKind.TEAM_REMOVE, decoded.kind());
        assertTrue(decoded.removedTeam().isPresent());
        assertEquals("red-uuid", decoded.removedTeam().get());
    }

    @Test
    void roundtrip_memberUpsert()
    {
        UUID aliceUuid = UUID.fromString("00000000-0000-0000-0000-000000000001");
        MemberRecord member = new MemberRecord(aliceUuid, "red-uuid");
        BetterTeamsDeltaPacket original = BetterTeamsDeltaPacket.memberUpsert(member);

        RegistryFriendlyByteBuf buf = newBuf();
        BetterTeamsDeltaPacket.STREAM_CODEC.encode(buf, original);
        BetterTeamsDeltaPacket decoded = BetterTeamsDeltaPacket.STREAM_CODEC.decode(buf);

        assertEquals(DeltaKind.MEMBER_UPSERT, decoded.kind());
        assertTrue(decoded.member().isPresent());
        assertEquals(member, decoded.member().get());
    }

    @Test
    void roundtrip_memberRemove()
    {
        UUID bobUuid = UUID.fromString("00000000-0000-0000-0000-000000000002");
        BetterTeamsDeltaPacket original = BetterTeamsDeltaPacket.memberRemove(bobUuid);

        RegistryFriendlyByteBuf buf = newBuf();
        BetterTeamsDeltaPacket.STREAM_CODEC.encode(buf, original);
        BetterTeamsDeltaPacket decoded = BetterTeamsDeltaPacket.STREAM_CODEC.decode(buf);

        assertEquals(DeltaKind.MEMBER_REMOVE, decoded.kind());
        assertTrue(decoded.removedUuid().isPresent());
        assertEquals(bobUuid, decoded.removedUuid().get());
    }
}
