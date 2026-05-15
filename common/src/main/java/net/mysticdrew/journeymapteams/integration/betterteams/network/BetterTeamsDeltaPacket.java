package net.mysticdrew.journeymapteams.integration.betterteams.network;

import commonnetwork.networking.data.PacketContext;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.mysticdrew.journeymapteams.integration.betterteams.BetterTeamsCache;
import net.mysticdrew.journeymapteams.integration.betterteams.DeltaKind;
import net.mysticdrew.journeymapteams.integration.betterteams.MemberRecord;
import net.mysticdrew.journeymapteams.integration.betterteams.TeamRecord;

import java.util.Optional;
import java.util.UUID;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

/**
 * Server -> Client: single-record incremental update.
 *
 * <p>Sub-codecs (TEAM_CODEC, MEMBER_CODEC) are shared from {@link BetterTeamsSnapshotPacket}
 * to avoid duplication.</p>
 *
 * <p>The {@code handle(PacketContext)} method calls
 * {@link BetterTeamsCache#applyDelta} on the singleton cache.</p>
 */
public record BetterTeamsDeltaPacket(
        DeltaKind kind,
        Optional<TeamRecord> team,
        Optional<String> removedTeam,
        Optional<MemberRecord> member,
        Optional<UUID> removedUuid) implements CustomPacketPayload
{
    public static final Identifier CHANNEL =
            Identifier.fromNamespaceAndPath(MOD_ID, "bt_delta");

    // Custom StreamCodec: encoding is conditional on kind, so composite() does not apply.
    public static final StreamCodec<RegistryFriendlyByteBuf, BetterTeamsDeltaPacket> STREAM_CODEC =
            new StreamCodec<>()
            {
                private static final DeltaKind[] KINDS = DeltaKind.values();

                @Override
                public BetterTeamsDeltaPacket decode(RegistryFriendlyByteBuf buf)
                {
                    DeltaKind k = KINDS[buf.readVarInt()];
                    return switch (k)
                    {
                        case TEAM_UPSERT ->
                                teamUpsert(BetterTeamsSnapshotPacket.TEAM_CODEC.decode(buf));
                        case TEAM_REMOVE ->
                                teamRemove(buf.readUtf());
                        case MEMBER_UPSERT ->
                                memberUpsert(BetterTeamsSnapshotPacket.MEMBER_CODEC.decode(buf));
                        case MEMBER_REMOVE ->
                                memberRemove(new UUID(buf.readLong(), buf.readLong()));
                    };
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, BetterTeamsDeltaPacket p)
                {
                    buf.writeVarInt(p.kind().ordinal());
                    switch (p.kind())
                    {
                        case TEAM_UPSERT ->
                                BetterTeamsSnapshotPacket.TEAM_CODEC.encode(buf, p.team().orElseThrow());
                        case TEAM_REMOVE ->
                                buf.writeUtf(p.removedTeam().orElseThrow());
                        case MEMBER_UPSERT ->
                                BetterTeamsSnapshotPacket.MEMBER_CODEC.encode(buf, p.member().orElseThrow());
                        case MEMBER_REMOVE ->
                        {
                            UUID id = p.removedUuid().orElseThrow();
                            buf.writeLong(id.getMostSignificantBits());
                            buf.writeLong(id.getLeastSignificantBits());
                        }
                    }
                }
            };

    @Override
    public Type<? extends CustomPacketPayload> type()
    {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public static CustomPacketPayload.Type<CustomPacketPayload> staticType()
    {
        return new CustomPacketPayload.Type<>(CHANNEL);
    }

    public static void handle(PacketContext<BetterTeamsDeltaPacket> ctx)
    {
        BetterTeamsCache.get().applyDelta(ctx.message());
    }

    // -------------------------------------------------------------------------
    // Static factories - signatures must stay byte-identical to the original
    // stub so that BetterTeamsCacheTest continues to compile and pass.
    // -------------------------------------------------------------------------

    public static BetterTeamsDeltaPacket teamUpsert(TeamRecord team)
    {
        return new BetterTeamsDeltaPacket(DeltaKind.TEAM_UPSERT, Optional.of(team),
                Optional.empty(), Optional.empty(), Optional.empty());
    }

    public static BetterTeamsDeltaPacket teamRemove(String teamId)
    {
        return new BetterTeamsDeltaPacket(DeltaKind.TEAM_REMOVE, Optional.empty(),
                Optional.of(teamId), Optional.empty(), Optional.empty());
    }

    public static BetterTeamsDeltaPacket memberUpsert(MemberRecord member)
    {
        return new BetterTeamsDeltaPacket(DeltaKind.MEMBER_UPSERT, Optional.empty(),
                Optional.empty(), Optional.of(member), Optional.empty());
    }

    public static BetterTeamsDeltaPacket memberRemove(UUID playerUuid)
    {
        return new BetterTeamsDeltaPacket(DeltaKind.MEMBER_REMOVE, Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(playerUuid));
    }
}
