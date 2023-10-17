package net.mysticdrew.journeymapteams.handlers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.mysticdrew.journeymapteams.Constants.LOGGER;

public enum HandlerManager
{
    INSTANCE;

    private final Map<String, Class<? extends Handler>> handlerMap = new HashMap<>();

    HandlerManager()
    {
        populate();
    }

    private void populate()
    {
        handlerMap.put("minecraft", VanillaTeamsHandler.class);
        handlerMap.put("ftbteams", FTBTeamsHandler.class);
    }

    /**
     * Add any handlers for mods only on specific mod loaders.
     *
     * @param modId - modId
     * @param cls   - the HandlerClass
     */
    public <T extends Handler> void addLoaderHandler(String modId, Class<T> cls)
    {
        handlerMap.put(modId, cls);
    }

    public Handler getHandler(String modId)
    {
        return this.getHandler(Collections.singletonList(modId));
    }

    public Handler getHandler(List<String> modIds)
    {
        try
        {
            for (String modId : modIds)
            {
                var handler = handlerMap.get(modId);
                // skip minecraft here, just in-case it is in the list before the first teams mod.
                if (handler != null && !"minecraft".equals(modId))
                {
                    return handler.getDeclaredConstructor().newInstance();
                }
            }
            return handlerMap.get("minecraft").getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            LOGGER.error(String.format("Couldn't initialize teams handler for {}", e));
        }
        return null;
    }
}
