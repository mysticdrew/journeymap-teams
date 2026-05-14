package net.mysticdrew.journeymapteams.integration.ftb;

import journeymap.api.v2.client.IClientAPI;
import journeymap.api.v2.client.IClientPlugin;
import journeymap.api.v2.common.JourneyMapPlugin;
import journeymap.api.v2.common.event.ClientEventRegistry;
import net.minecraft.client.Minecraft;
import net.mysticdrew.journeymapteams.Constants;
import net.mysticdrew.journeymapteams.handlers.properties.DefaultHandlerProperties;
import net.mysticdrew.journeymapteams.integration.RadarColorApplier;
import net.mysticdrew.journeymapteams.integration.ftb.FTBTeamsHandler;

@JourneyMapPlugin(apiVersion = "2.0.0", dependencies = {"ftbteams"})
public class FtbTeamsClientPlugin implements IClientPlugin
{
    private DefaultHandlerProperties properties;
    private RadarColorApplier colorApplier;

    @Override
    public void initialize(IClientAPI jmClientApi)
    {
        try
        {
            this.properties = new DefaultHandlerProperties("ftbteams", "prop.category.label.ftb");
            FTBTeamsHandler handler = new FTBTeamsHandler(properties, () -> Minecraft.getInstance().player);
            this.colorApplier = new RadarColorApplier(handler, RadarColorApplier.NameMode.CUSTOM_NAME);

            ClientEventRegistry.ENTITY_RADAR_UPDATE_EVENT.subscribe(Constants.MOD_ID,
                    colorApplier::onEntityRadarUpdate);
        }
        catch (Throwable e)
        {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String getModId()
    {
        return Constants.MOD_ID+"_ftb_teams_client";
    }
}
