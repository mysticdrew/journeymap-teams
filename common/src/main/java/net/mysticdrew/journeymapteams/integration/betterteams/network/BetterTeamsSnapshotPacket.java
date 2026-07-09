package net.mysticdrew.journeymapteams.integration.betterteams.network;

import commonnetwork.networking.data.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.mysticdrew.journeymapteams.integration.betterteams.BetterTeamsCache;
import net.mysticdrew.journeymapteams.integration.betterteams.MemberRecord;
import net.mysticdrew.journeymapteams.integration.betterteams.TeamRecord;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

/**
 * Server -> Client: full snapshot of all teams and members.
 * Sent once per session after the handshake is confirmed.
 *
 * <p>TEAM_CODEC and MEMBER_CODEC are public so that {@link BetterTeamsDeltaPacket}
 * can reuse them without duplication.</p>
 *
 * <p>The {@code handle(PacketContext)} method calls
 * {@link net.mysticdrew.journeymapteams.integration.betterteams.BetterTeamsCache#applySnapshot}
 * on the singleton cache.</p>
 */
public record BetterTeamsSnapshotPacket(List<TeamRecord> teams, List<MemberRecord> members)
        implements CustomPacketPayload
{
    public static final Identifier CHANNEL =
            Identifier.fromNamespaceAndPath(MOD_ID, "bt_snapshot");

    public static final CustomPacketPayload.Type<BetterTeamsSnapshotPacket> TYPE =
            new CustomPacketPayload.Type<>(CHANNEL);

    public static final StreamCodec<RegistryFriendlyByteBuf, BetterTeamsSnapshotPacket> STREAM_CODEC =
            StreamCodec.ofMember(BetterTeamsSnapshotPacket::encode, BetterTeamsSnapshotPacket::decode);

    // Shared sub-codecs: typed on RegistryFriendlyByteBuf so readVarInt/writeVarInt and readUtf/writeUtf are available.
    // BetterTeamsDeltaPacket imports and reuses these directly to avoid duplication.
    public static final StreamCodec<RegistryFriendlyByteBuf, UUID> UUID_CODEC =
            new StreamCodec<>()
            {
                @Override
                public UUID decode(RegistryFriendlyByteBuf buf)
                {
                    return new UUID(buf.readLong(), buf.readLong());
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, UUID v)
                {
                    buf.writeLong(v.getMostSignificantBits());
                    buf.writeLong(v.getLeastSignificantBits());
                }
            };

    public static final StreamCodec<RegistryFriendlyByteBuf, TeamRecord> TEAM_CODEC =
            new StreamCodec<>()
            {
                @Override
                public TeamRecord decode(RegistryFriendlyByteBuf buf)
                {
                    String teamId = buf.readUtf();
                    String name = buf.readUtf();
                    int color = buf.readInt();
                    int allyCount = buf.readVarInt();
                    List<String> allies = new ArrayList<>(allyCount);
                    for (int i = 0; i < allyCount; i++)
                    {
                        allies.add(buf.readUtf());
                    }
                    return new TeamRecord(teamId, name, color, allies);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, TeamRecord t)
                {
                    buf.writeUtf(t.teamId());
                    buf.writeUtf(t.name());
                    buf.writeInt(t.color());
                    buf.writeVarInt(t.allies().size());
                    for (String ally : t.allies())
                    {
                        buf.writeUtf(ally);
                    }
                }
            };

    public static final StreamCodec<RegistryFriendlyByteBuf, MemberRecord> MEMBER_CODEC =
            new StreamCodec<>()
            {
                @Override
                public MemberRecord decode(RegistryFriendlyByteBuf buf)
                {
                    UUID uuid = UUID_CODEC.decode(buf);
                    String teamId = buf.readUtf();
                    return new MemberRecord(uuid, teamId);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, MemberRecord m)
                {
                    UUID_CODEC.encode(buf, m.playerUuid());
                    buf.writeUtf(m.teamId());
                }
            };

    /**
     * Compact constructor: defensive-copy the input lists so the record's accessors
     * always return immutable views regardless of what the caller passed in.
     */
    public BetterTeamsSnapshotPacket
    {
        teams = List.copyOf(teams);
        members = List.copyOf(members);
    }

    /**
     * Wire-decode factory. Used as the deserializer in {@link #STREAM_CODEC}.
     * Records cannot have non-canonical constructors, so this is a static method.
     */
    public static BetterTeamsSnapshotPacket decode(RegistryFriendlyByteBuf buf)
    {
        int teamCount = buf.readVarInt();
        List<TeamRecord> teams = new ArrayList<>(teamCount);
        for (int i = 0; i < teamCount; i++)
        {
            teams.add(TEAM_CODEC.decode(buf));
        }
        int memberCount = buf.readVarInt();
        List<MemberRecord> members = new ArrayList<>(memberCount);
        for (int i = 0; i < memberCount; i++)
        {
            members.add(MEMBER_CODEC.decode(buf));
        }
        return new BetterTeamsSnapshotPacket(teams, members);
    }

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public void encode(RegistryFriendlyByteBuf buf)
    {
        buf.writeVarInt(teams.size());
        for (TeamRecord t : teams)
        {
            TEAM_CODEC.encode(buf, t);
        }
        buf.writeVarInt(members.size());
        for (MemberRecord m : members)
        {
            MEMBER_CODEC.encode(buf, m);
        }
    }

    public static void handle(PacketContext<BetterTeamsSnapshotPacket> ctx)
    {
        BetterTeamsCache.get().applySnapshot(ctx.message().teams(), ctx.message().members());
    }
}
