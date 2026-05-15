package net.mysticdrew.journeymapteams.integration;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ClientEventRegistry;
import net.minecraft.client.Minecraft;
import net.mysticdrew.journeymapteams.Constants;
import net.mysticdrew.journeymapteams.ModEnvironment;
import net.mysticdrew.journeymapteams.handlers.VanillaTeamsHandler;
import net.mysticdrew.journeymapteams.handlers.properties.DefaultHandlerProperties;

@JourneyMapPlugin(apiVersion = "2.0.0")
public class VanillaTeamsClientPlugin implements IClientPlugin
{
    private DefaultHandlerProperties properties;
    private RadarColorApplier colorApplier;

    @Override
    public void initialize(IClientAPI jmClientApi)
    {
        if (ModEnvironment.isLoaded("ftbteams"))
        {
            return; // self-disable
        }
        this.properties = new DefaultHandlerProperties("vanilla", "prop.category.label.vanilla");
        VanillaTeamsHandler handler = new VanillaTeamsHandler(properties, null,
                () -> Minecraft.getInstance().player);
        this.colorApplier = new RadarColorApplier(handler, RadarColorApplier.NameMode.VANILLA_TEAM);

        ClientEventRegistry.ENTITY_RADAR_UPDATE_EVENT.subscribe(Constants.MOD_ID,
                colorApplier::onEntityRadarUpdate);
    }

    @Override
    public String getModId()
    {
        return Constants.MOD_ID;
    }
}
