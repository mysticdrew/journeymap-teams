package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.mysticdrew.journeymapteams.handlers.properties.ServerProperties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class VanillaTeamsHandlerTest
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

    private static VanillaTeamsHandler handlerWith(ServerProperties sp)
    {
        return new VanillaTeamsHandler(null, sp, () -> null);
    }

    @Test
    void isVisible_sameTeam_returnsVisibleArg()
    {
        PlayerTeam team = mock(PlayerTeam.class);
        when(team.getName()).thenReturn("red");
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(team);
        when(remote.getTeam()).thenReturn(team);
        VanillaTeamsHandler handler = handlerWith(defaults());
        assertTrue(handler.isVisible(receiver, remote, false, true));
        assertFalse(handler.isVisible(receiver, remote, false, false));
    }

    @Test
    void isVisible_differentTeam_notOp_returnsFalse()
    {
        PlayerTeam red = mock(PlayerTeam.class);
        when(red.getName()).thenReturn("red");
        PlayerTeam blue = mock(PlayerTeam.class);
        when(blue.getName()).thenReturn("blue");
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(red);
        when(remote.getTeam()).thenReturn(blue);
        assertFalse(handlerWith(defaults()).isVisible(receiver, remote, false, true));
    }

    @Test
    void isVisible_receiverUnteamed_remoteTeamed_notOp_returnsFalse()
    {
        PlayerTeam blue = mock(PlayerTeam.class);
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(null);
        when(remote.getTeam()).thenReturn(blue);
        assertFalse(handlerWith(defaults()).isVisible(receiver, remote, false, true));
    }

    @Test
    void isVisible_receiverUnteamed_remoteTeamed_op_returnsVisibleArg()
    {
        PlayerTeam blue = mock(PlayerTeam.class);
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(null);
        when(remote.getTeam()).thenReturn(blue);
        assertTrue(handlerWith(defaults()).isVisible(receiver, remote, true, true));
    }

    @Test
    void isVisible_differentTeam_hideOtherTeamsFalse_returnsVisibleArg()
    {
        PlayerTeam red = mock(PlayerTeam.class);
        when(red.getName()).thenReturn("red");
        PlayerTeam blue = mock(PlayerTeam.class);
        when(blue.getName()).thenReturn("blue");
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(red);
        when(remote.getTeam()).thenReturn(blue);

        ServerProperties sp = defaults();
        when(sp.getHideOtherTeams()).thenReturn(false);
        assertTrue(handlerWith(sp).isVisible(receiver, remote, false, true));
    }
}
