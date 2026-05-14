package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        return new AbstractHandler(props, () -> null)
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
}
