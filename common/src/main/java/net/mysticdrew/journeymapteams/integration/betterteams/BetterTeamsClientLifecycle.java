package net.mysticdrew.journeymapteams.integration.betterteams;

/**
 * Per-loader entry points (fabric/forge/neoforge) call {@link #onDisconnect()}
 * from their loader-specific client disconnect event. The lifecycle code lives
 * here so all three loaders share one cache-reset code path.
 */
public final class BetterTeamsClientLifecycle
{
    private BetterTeamsClientLifecycle() {}

    public static void onDisconnect()
    {
        BetterTeamsCache.get().clear();
    }
}
