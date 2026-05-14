package net.mysticdrew.journeymapteams.handlers;

import net.minecraft.SharedConstants;
import net.minecraft.server.Bootstrap;

/**
 * Utility that boots the Minecraft registry system exactly once per JVM.
 * Call {@link #boot()} in a {@code @BeforeAll} on every test class that
 * uses real (or Mockito-mocked) Minecraft types.
 */
final class MinecraftBootstrap
{
    private static volatile boolean booted = false;

    private MinecraftBootstrap() {}

    static synchronized void boot()
    {
        if (!booted)
        {
            SharedConstants.tryDetectVersion();
            Bootstrap.bootStrap();
            booted = true;
        }
    }
}
