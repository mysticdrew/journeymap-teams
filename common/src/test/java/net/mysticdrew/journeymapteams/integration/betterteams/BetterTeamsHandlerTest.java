package net.mysticdrew.journeymapteams.integration.betterteams;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.LocalPlayerSupplier;
import net.mysticdrew.journeymapteams.handlers.MinecraftBootstrap;
import net.mysticdrew.journeymapteams.handlers.VisibilityRelationship;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class BetterTeamsHandlerTest
{
    @BeforeAll
    static void bootstrap()
    {
        MinecraftBootstrap.boot();
    }

    @Mock private BetterTeamsDataSource dataSource;
    @Mock private Properties properties;
    @Mock private ServerProperties serverProperties;
    @Mock private LocalPlayerSupplier localPlayerSupplier;
    @Mock private Player receiver;
    @Mock private Player remote;

    private static final UUID RECV = UUID.fromString("00000000-0000-0000-0000-00000000A001");
    private static final UUID REM = UUID.fromString("00000000-0000-0000-0000-00000000A002");
    private static final String TEAM_RED = "red";
    private static final String TEAM_BLUE = "blue";

    private BetterTeamsHandler handler;

    @BeforeEach
    void setUp()
    {
        handler = new BetterTeamsHandler(properties, serverProperties, localPlayerSupplier, dataSource);
        when(receiver.getUUID()).thenReturn(RECV);
        when(remote.getUUID()).thenReturn(REM);
    }

    @Test
    void relationship_remoteUnteamed()
    {
        when(dataSource.teamFor(REM)).thenReturn(Optional.empty());

        assertEquals(VisibilityRelationship.REMOTE_UNTEAMED, handler.relationship(receiver, remote));
    }

    @Test
    void relationship_viewerUnteamedRemoteTeamed()
    {
        when(dataSource.teamFor(REM)).thenReturn(Optional.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())));
        when(dataSource.teamFor(RECV)).thenReturn(Optional.empty());

        assertEquals(VisibilityRelationship.VIEWER_UNTEAMED_REMOTE_TEAMED, handler.relationship(receiver, remote));
    }

    @Test
    void relationship_sameTeam()
    {
        TeamRecord red = new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of());
        when(dataSource.teamFor(REM)).thenReturn(Optional.of(red));
        when(dataSource.teamFor(RECV)).thenReturn(Optional.of(red));

        assertEquals(VisibilityRelationship.SAME_TEAM, handler.relationship(receiver, remote));
    }

    @Test
    void relationship_allied()
    {
        when(dataSource.teamFor(RECV)).thenReturn(Optional.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of(TEAM_BLUE))));
        when(dataSource.teamFor(REM)).thenReturn(Optional.of(new TeamRecord(TEAM_BLUE, "Blue", 0x0000FF, List.of(TEAM_RED))));

        assertEquals(VisibilityRelationship.ALLIED, handler.relationship(receiver, remote));
    }

    @Test
    void relationship_otherTeam()
    {
        when(dataSource.teamFor(RECV)).thenReturn(Optional.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())));
        when(dataSource.teamFor(REM)).thenReturn(Optional.of(new TeamRecord(TEAM_BLUE, "Blue", 0x0000FF, List.of())));

        assertEquals(VisibilityRelationship.OTHER_TEAM, handler.relationship(receiver, remote));
    }

    @Test
    void isVisible_hideOtherTeams_appliesPolicy()
    {
        when(dataSource.teamFor(RECV)).thenReturn(Optional.of(new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of())));
        when(dataSource.teamFor(REM)).thenReturn(Optional.of(new TeamRecord(TEAM_BLUE, "Blue", 0x0000FF, List.of())));
        when(serverProperties.getEnforceTeamVisibility()).thenReturn(true);
        when(serverProperties.getOpsBypassHiding()).thenReturn(false);
        when(serverProperties.getHideOtherTeams()).thenReturn(true);

        assertFalse(handler.isVisible(receiver, remote, false, true));
    }

    @Test
    void isVisible_sameTeam_alwaysVisibleEvenIfPolicyOn()
    {
        TeamRecord red = new TeamRecord(TEAM_RED, "Red", 0xFF0000, List.of());
        when(dataSource.teamFor(RECV)).thenReturn(Optional.of(red));
        when(dataSource.teamFor(REM)).thenReturn(Optional.of(red));
        when(serverProperties.getEnforceTeamVisibility()).thenReturn(true);
        when(serverProperties.getOpsBypassHiding()).thenReturn(false);

        assertTrue(handler.isVisible(receiver, remote, false, true));
    }
}
