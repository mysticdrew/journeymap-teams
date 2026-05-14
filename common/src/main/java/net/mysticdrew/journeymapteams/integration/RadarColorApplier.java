package net.mysticdrew.journeymapteams.integration;

import journeymap.api.v2.client.entity.WrappedEntity;
import journeymap.api.v2.client.event.EntityRadarUpdateEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.mysticdrew.journeymapteams.handlers.Handler;

/**
 * Applies a {@link Handler}'s color decisions to a player entity on the radar.
 * Replaces the old client {@code DrawEntityMixin}.
 */
public final class RadarColorApplier
{
    /**
     * How a handler's name color is painted onto the wrapped entity.
     */
    public enum NameMode
    {
        /**
         * For teams backed by vanilla scoreboard teams. JourneyMap draws a
         * team-name label colored by {@code teamColor} for scoreboard-teamed
         * players and the player-name label colored by {@code labelColor}
         * otherwise, so both are set.
         */
        VANILLA_TEAM,

        /**
         * For modded teams that are not vanilla scoreboard teams (e.g. FTB
         * Teams). Those players have no scoreboard team, so {@code setTeamColor}
         * is a no-op; instead the player's name is replaced with a custom-name
         * Component styled in the team color.
         */
        CUSTOM_NAME
    }

    private final Handler handler;
    private final NameMode nameMode;

    public RadarColorApplier(Handler handler, NameMode nameMode)
    {
        this.handler = handler;
        this.nameMode = nameMode;
    }

    public void onEntityRadarUpdate(EntityRadarUpdateEvent event)
    {
        if (event.getType() != EntityRadarUpdateEvent.EntityType.PLAYER)
        {
            return;
        }
        WrappedEntity wrapped = event.getWrappedEntity();
        Entity entity = wrapped.getEntityRef().get();
        if (!(entity instanceof Player remote))
        {
            return;
        }

        wrapped.setColor(handler.getRemotePlayerIconColor(remote, wrapped.getColor()));

        if (nameMode == NameMode.VANILLA_TEAM)
        {
            wrapped.setTeamColor(handler.getRemotePlayerNameColor(remote, wrapped.getTeamColor()));
            wrapped.setLabelColor(handler.getRemotePlayerNameColor(remote, wrapped.getLabelColor()));
        }
        else
        {
            int current = wrapped.getLabelColor();
            int nameColor = handler.getRemotePlayerNameColor(remote, current);
            if (nameColor != current)
            {
                wrapped.setCustomName(remote.getName().copy()
                        .withStyle(style -> style.withColor(nameColor)));
            }
        }
    }
}
