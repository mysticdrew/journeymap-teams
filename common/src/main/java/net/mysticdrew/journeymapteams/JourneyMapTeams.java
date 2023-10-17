package net.mysticdrew.journeymapteams;

import net.mysticdrew.journeymapteams.client.integration.JourneyMapCommonPlugin;
import net.mysticdrew.journeymapteams.handlers.Handler;
import net.mysticdrew.journeymapteams.handlers.HandlerManager;

import java.util.List;

public class JourneyMapTeams
{
    private static JourneyMapTeams instance;
    private final Handler handler;

    public JourneyMapTeams(List<String> modIds)
    {
        instance = this;
        this.handler = HandlerManager.INSTANCE.getHandler(modIds);
        JourneyMapCommonPlugin.init(this.handler);
    }

    public static JourneyMapTeams getInstance()
    {
        return instance;
    }

    public Handler getHandler()
    {
        return handler;
    }
}
