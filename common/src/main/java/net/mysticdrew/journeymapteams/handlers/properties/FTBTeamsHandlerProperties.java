package net.mysticdrew.journeymapteams.handlers.properties;

import journeymap.client.api.option.BooleanOption;
import journeymap.client.api.option.EnumOption;
import journeymap.client.api.option.OptionCategory;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

public class FTBTeamsHandlerProperties
{
    OptionCategory category = new OptionCategory(MOD_ID + "-ftbteams", "prop.category.label.ftb");

    public final EnumOption<Color> defaultTeamColor;
    public final BooleanOption doOverrideAllyColor;
    public final EnumOption<Color> overrideAllyColor;

    public FTBTeamsHandlerProperties()
    {
        //TODO: i18n
        this.defaultTeamColor = new EnumOption<>(category, "default-team-color", "prop.option.label.ftb.default.team_color", Color.GREEN);
        this.doOverrideAllyColor = new BooleanOption(category, "over-ride-ally-color", "prop.option.label.ftb.override_ally", false);
        this.overrideAllyColor = new EnumOption<>(category, "default-ally-color", "prop.category.label.vanilla", Color.BLUE);
    }
}
