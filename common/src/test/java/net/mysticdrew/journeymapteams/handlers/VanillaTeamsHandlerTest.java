package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
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

    private final VanillaTeamsHandler handler = new VanillaTeamsHandler(null, () -> null);

    @Test
    void isVisible_sameTeam_returnsVisibleArg()
    {
        PlayerTeam team = mock(PlayerTeam.class);
        when(team.getName()).thenReturn("red");
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(team);
        when(remote.getTeam()).thenReturn(team);
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
        assertFalse(handler.isVisible(receiver, remote, false, true));
    }

    @Test
    void isVisible_receiverUnteamed_remoteTeamed_notOp_returnsFalse()
    {
        PlayerTeam blue = mock(PlayerTeam.class);
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(null);
        when(remote.getTeam()).thenReturn(blue);
        assertFalse(handler.isVisible(receiver, remote, false, true));
    }

    @Test
    void isVisible_receiverUnteamed_remoteTeamed_op_returnsVisibleArg()
    {
        PlayerTeam blue = mock(PlayerTeam.class);
        Player receiver = mock(Player.class);
        Player remote = mock(Player.class);
        when(receiver.getTeam()).thenReturn(null);
        when(remote.getTeam()).thenReturn(blue);
        assertTrue(handler.isVisible(receiver, remote, true, true));
    }
}
