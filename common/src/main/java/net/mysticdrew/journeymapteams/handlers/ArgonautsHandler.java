package net.mysticdrew.journeymapteams.handlers;

import earth.terrarium.argonauts.api.guild.GuildApi;
import earth.terrarium.argonauts.api.party.PartyApi;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.properties.Properties;

public class ArgonautsHandler extends AbstractHandler
{
    public ArgonautsHandler(Properties properties)
    {
        super(properties);
    }

    @Override
    protected int getRemotePlayerColor(Player remotePlayer)
    {
        return 0;
    }

    @Override
    public boolean isVisible(Player localPlayer, Player remotePlayer, boolean isOp, boolean visible)
    {
        var localGuild = GuildApi.API.get((ServerPlayer) localPlayer);
        var remoteGuild = GuildApi.API.get((ServerPlayer) remotePlayer);

        var localParty = PartyApi.API.get(localPlayer);
        var remoteParty = PartyApi.API.get(remotePlayer);

        return false;
    }
}
