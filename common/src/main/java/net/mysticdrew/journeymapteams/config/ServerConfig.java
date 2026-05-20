package net.mysticdrew.journeymapteams.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

import static net.mysticdrew.journeymapteams.Constants.LOGGER;

/**
 * Server-side configuration for JourneyMap-Teams, stored in
 * {@code config/journeymapteams-server.json} and loaded once per server start.
 *
 * <p>Only consulted server-side (the radar visibility handler), so on a pure
 * client {@link #load(Path)} is never called and the built-in defaults apply.
 */
public class ServerConfig
{
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String FILE_NAME = "journeymapteams-server.json";

    private static volatile ServerConfig instance = new ServerConfig();

    /**
     * Human-readable hint serialized into the JSON file purely so admins
     * editing it by hand know what the setting does. Never read by code.
     */
    @SuppressWarnings("unused")
    private String _comment = "Set opsBypassTeamVisibility to false to also hide other teams from server operators.";

    /**
     * When {@code true} (default) server operators bypass team-based hiding
     * and see every player on the radar. When {@code false}, operators are
     * filtered by team like everyone else.
     */
    private boolean opsBypassTeamVisibility = true;

    public static ServerConfig getInstance()
    {
        return instance;
    }

    public boolean opsBypassTeamVisibility()
    {
        return opsBypassTeamVisibility;
    }

    /**
     * Loads the config from {@code configDir/journeymapteams-server.json},
     * creating it with defaults if it does not exist. Any failure leaves the
     * built-in defaults in place.
     *
     * @param configDir the loader's config directory
     */
    public static void load(Path configDir)
    {
        Path file = configDir.resolve(FILE_NAME);
        try
        {
            if (Files.exists(file))
            {
                try (Reader reader = Files.newBufferedReader(file))
                {
                    // Keys absent from the file keep their field-initializer
                    // default, so adding settings later stays backward-compatible.
                    ServerConfig loaded = GSON.fromJson(reader, ServerConfig.class);
                    if (loaded != null)
                    {
                        instance = loaded;
                    }
                }
            }
            else
            {
                save(file);
            }
        }
        catch (Exception e)
        {
            LOGGER.error("Failed to load {}, using defaults", FILE_NAME, e);
        }
    }

    private static void save(Path file)
    {
        try
        {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file))
            {
                GSON.toJson(instance, writer);
            }
        }
        catch (IOException e)
        {
            LOGGER.error("Failed to write {}", FILE_NAME, e);
        }
    }
}
