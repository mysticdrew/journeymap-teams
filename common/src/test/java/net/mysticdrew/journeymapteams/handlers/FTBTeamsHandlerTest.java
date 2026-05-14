package net.mysticdrew.journeymapteams.handlers;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI.API;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.integration.ftb.FTBTeamsHandler;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link FTBTeamsHandler}.
 *
 * <p>Scenarios that would exercise any code path referencing {@code TeamRank}
 * (e.g. both players on the same team) cannot be tested in this environment:
 * {@code TeamRank.<clinit>} transitively loads
 * {@code dev.ftb.mods.ftblibrary.util.text.CustomTextColor extends TextColor(final)},
 * which the JVM rejects with {@code IncompatibleClassChangeError}.  At runtime FTB
 * Library removes {@code final} from {@code TextColor} via a mixin, but no mixin
 * transformation is active in the plain JUnit environment.  Coverage of the
 * same-team / ally paths is deferred to in-game testing (Task 18).</p>
 */
class FTBTeamsHandlerTest
{
    @BeforeAll
    static void bootstrap()
    {
        MinecraftBootstrap.boot();
    }

    private final FTBTeamsHandler handler = new FTBTeamsHandler(null, () -> null);

    /**
     * Receiver has no team, remote has a team, receiver is not OP -> remote is hidden.
     * This path takes the {@code localTeam.isEmpty()} early-exit branch and therefore
     * never touches {@code TeamRank}.
     */
    @Test
    void isVisible_receiverNoTeam_remoteHasTeam_notOp_returnsFalse()
    {
        UUID recId = UUID.randomUUID();
        UUID remId = UUID.randomUUID();
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getUUID()).thenReturn(recId);
        when(remote.getUUID()).thenReturn(remId);

        Team team = mock(Team.class);
        API api = mock(API.class);
        TeamManager mgr = mock(TeamManager.class);
        when(api.getManager()).thenReturn(mgr);
        when(mgr.getTeamForPlayerID(recId)).thenReturn(Optional.empty());
        when(mgr.getTeamForPlayerID(remId)).thenReturn(Optional.of(team));

        try (MockedStatic<FTBTeamsAPI> statics = mockStatic(FTBTeamsAPI.class))
        {
            statics.when(FTBTeamsAPI::api).thenReturn(api);
            assertFalse(handler.isVisible(receiver, remote, false, true));
        }
    }
}
