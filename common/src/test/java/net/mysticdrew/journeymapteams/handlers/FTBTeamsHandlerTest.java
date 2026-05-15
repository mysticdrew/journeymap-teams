package net.mysticdrew.journeymapteams.handlers;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.FTBTeamsAPI.API;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.TeamManager;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;
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
 * <p>Scenarios that exercise any code path referencing {@code TeamRank} (the
 * ally check, hence the {@code ALLIED} / {@code OTHER_TEAM} / {@code SAME_TEAM}
 * relationships) cannot be tested here: {@code TeamRank.<clinit>} transitively
 * loads {@code dev.ftb.mods.ftblibrary.util.text.CustomTextColor extends
 * TextColor(final)}, which the JVM rejects with {@code IncompatibleClassChangeError}.
 * At runtime FTB Library removes {@code final} from {@code TextColor} via a
 * mixin, but no mixin transformation is active in the plain JUnit environment.
 * Relationship detection checks the unteamed cases before computing {@code allied},
 * so the tests below stay on TeamRank-free paths. Coverage of the team/ally paths
 * is deferred to in-game testing.</p>
 */
class FTBTeamsHandlerTest
{
    @BeforeAll
    static void bootstrap()
    {
        MinecraftBootstrap.boot();
    }

    /** A ServerProperties mock whose values reproduce the historical hardcoded behavior. */
    private static ServerProperties defaults()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getOpsBypassHiding()).thenReturn(true);
        when(sp.getHideUnteamed()).thenReturn(false);
        when(sp.getHideAllies()).thenReturn(false);
        when(sp.getHideOtherTeams()).thenReturn(true);
        when(sp.getHideTeamedFromUnteamed()).thenReturn(true);
        return sp;
    }

    private static FTBTeamsHandler handlerWith(ServerProperties sp)
    {
        return new FTBTeamsHandler(null, sp, () -> null);
    }

    /**
     * Receiver has no team, remote has a (non-player) team, receiver is not OP.
     * Relationship is VIEWER_UNTEAMED_REMOTE_TEAMED; with the default
     * hide-teamed-from-unteamed=true the remote is hidden. This path never
     * touches TeamRank.
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
            assertFalse(handlerWith(defaults()).isVisible(receiver, remote, false, true));
        }
    }

    /**
     * Remote player has no team at all. Relationship is REMOTE_UNTEAMED; with
     * hide-unteamed=true the remote is hidden. This path never touches TeamRank.
     */
    @Test
    void isVisible_remoteHasNoTeam_hideUnteamedTrue_returnsFalse()
    {
        UUID recId = UUID.randomUUID();
        UUID remId = UUID.randomUUID();
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getUUID()).thenReturn(recId);
        when(remote.getUUID()).thenReturn(remId);

        API api = mock(API.class);
        TeamManager mgr = mock(TeamManager.class);
        when(api.getManager()).thenReturn(mgr);
        when(mgr.getTeamForPlayerID(recId)).thenReturn(Optional.empty());
        when(mgr.getTeamForPlayerID(remId)).thenReturn(Optional.empty());

        ServerProperties sp = defaults();
        when(sp.getHideUnteamed()).thenReturn(true);

        try (MockedStatic<FTBTeamsAPI> statics = mockStatic(FTBTeamsAPI.class))
        {
            statics.when(FTBTeamsAPI::api).thenReturn(api);
            assertFalse(handlerWith(sp).isVisible(receiver, remote, false, true));
        }
    }
}
