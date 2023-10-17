package net.mysticdrew.journeymapteams.handlers.properties;

import journeymap.client.api.option.EnumOption;
import journeymap.client.api.option.OptionCategory;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

public class VanillaHandlerProperties
{
    OptionCategory category = new OptionCategory(MOD_ID + "-vanilla", "prop.category.label.vanilla", "prop.category.label.vanilla.tooltip");

    public final EnumOption<Color> defaultTeamColor;

    public VanillaHandlerProperties()
    {
        this.defaultTeamColor = new EnumOption<>(category, "vanilla-team-color", "prop.option.label.vanilla.team_color", Color.BLUE);
    }
}
