package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AbstractHandlerTest
{
    @BeforeAll
    static void bootstrap()
    {
        MinecraftBootstrap.boot();
    }

    private static AbstractHandler handlerWith(Properties props)
    {
        return new AbstractHandler(props, null, () -> null)
        {
            @Override
            public boolean isVisible(Player r, Player rm, boolean op, boolean vis) { return vis; }
            @Override
            protected int getRemotePlayerColor(Player p) { return 0xAAAAAA; }
        };
    }

    @Test
    void getColor_allied_forced_returnsAllyColor()
    {
        Properties props = mock(Properties.class);
        when(props.getForceAllyColor()).thenReturn(true);
        when(props.getAllyColor()).thenReturn(0x0000FF);
        assertEquals(0x0000FF, handlerWith(props).getColor(false, true, 0x123456));
    }

    @Test
    void getColor_allied_notForced_returnsTeamColorArg()
    {
        Properties props = mock(Properties.class);
        when(props.getForceAllyColor()).thenReturn(false);
        assertEquals(0x123456, handlerWith(props).getColor(false, true, 0x123456));
    }

    @Test
    void getColor_teammate_forced_returnsTeamColor()
    {
        Properties props = mock(Properties.class);
        when(props.getForceAllyColor()).thenReturn(false);
        when(props.getForceTeamColor()).thenReturn(true);
        when(props.getTeamColor()).thenReturn(0x00FF00);
        assertEquals(0x00FF00, handlerWith(props).getColor(true, false, 0x123456));
    }

    @Test
    void getColor_neitherTeammateNorAllied_returnsTeamColorArg()
    {
        // A player who is neither a teammate nor an ally is on an unrelated team.
        // They must show their OWN team's color (the teamColor arg), not the
        // user's configured team-color option.
        Properties props = mock(Properties.class);
        when(props.getTeamColor()).thenReturn(0x999999);
        assertEquals(0x123456, handlerWith(props).getColor(false, false, 0x123456));
    }

    @Test
    void getRemotePlayerNameColor_showNameColorFalse_returnsCurrent()
    {
        Properties props = mock(Properties.class);
        when(props.getShowNameColor()).thenReturn(false);
        assertEquals(0x999999, handlerWith(props).getRemotePlayerNameColor(mock(Player.class), 0x999999));
    }

    private static AbstractHandler handlerWithServer(ServerProperties sp)
    {
        return new AbstractHandler(null, sp, () -> null)
        {
            @Override
            public boolean isVisible(Player r, Player rm, boolean op, boolean vis) { return vis; }
            @Override
            protected int getRemotePlayerColor(Player p) { return 0xAAAAAA; }
        };
    }

    @Test
    void applyVisibilityPolicy_nullServerProperties_returnsVisibleArg()
    {
        AbstractHandler h = handlerWithServer(null);
        assertTrue(h.applyVisibilityPolicy(VisibilityRelationship.OTHER_TEAM, false, true));
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.OTHER_TEAM, false, false));
    }

    @Test
    void applyVisibilityPolicy_masterOff_returnsVisibleArg()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(false);
        AbstractHandler h = handlerWithServer(sp);
        assertTrue(h.applyVisibilityPolicy(VisibilityRelationship.OTHER_TEAM, false, true));
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.OTHER_TEAM, false, false));
    }

    @Test
    void applyVisibilityPolicy_opsBypassOn_opSeesEveryone()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getOpsBypassHiding()).thenReturn(true);
        when(sp.getHideOtherTeams()).thenReturn(true);
        AbstractHandler h = handlerWithServer(sp);
        assertTrue(h.applyVisibilityPolicy(VisibilityRelationship.OTHER_TEAM, true, true));
    }

    @Test
    void applyVisibilityPolicy_opsBypassOff_opStillHidden()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getOpsBypassHiding()).thenReturn(false);
        when(sp.getHideOtherTeams()).thenReturn(true);
        AbstractHandler h = handlerWithServer(sp);
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.OTHER_TEAM, true, true));
    }

    @Test
    void applyVisibilityPolicy_sameTeam_alwaysVisible()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        AbstractHandler h = handlerWithServer(sp);
        assertTrue(h.applyVisibilityPolicy(VisibilityRelationship.SAME_TEAM, false, true));
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.SAME_TEAM, false, false));
    }

    @Test
    void applyVisibilityPolicy_remoteUnteamed_hiddenWhenHideUnteamedTrue()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getHideUnteamed()).thenReturn(true);
        AbstractHandler h = handlerWithServer(sp);
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.REMOTE_UNTEAMED, false, true));
    }

    @Test
    void applyVisibilityPolicy_remoteUnteamed_shownWhenHideUnteamedFalse()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getHideUnteamed()).thenReturn(false);
        AbstractHandler h = handlerWithServer(sp);
        assertTrue(h.applyVisibilityPolicy(VisibilityRelationship.REMOTE_UNTEAMED, false, true));
    }

    @Test
    void applyVisibilityPolicy_allied_hiddenWhenHideAlliesTrue()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getHideAllies()).thenReturn(true);
        AbstractHandler h = handlerWithServer(sp);
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.ALLIED, false, true));
    }

    @Test
    void applyVisibilityPolicy_otherTeam_hiddenWhenHideOtherTeamsTrue()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getHideOtherTeams()).thenReturn(true);
        AbstractHandler h = handlerWithServer(sp);
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.OTHER_TEAM, false, true));
    }

    @Test
    void applyVisibilityPolicy_viewerUnteamedRemoteTeamed_hiddenWhenHideTeamedFromUnteamedTrue()
    {
        ServerProperties sp = mock(ServerProperties.class);
        when(sp.getEnforceTeamVisibility()).thenReturn(true);
        when(sp.getHideTeamedFromUnteamed()).thenReturn(true);
        AbstractHandler h = handlerWithServer(sp);
        assertFalse(h.applyVisibilityPolicy(VisibilityRelationship.VIEWER_UNTEAMED_REMOTE_TEAMED, false, true));
    }
}
