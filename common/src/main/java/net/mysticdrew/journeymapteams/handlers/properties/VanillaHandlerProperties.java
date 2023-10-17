package net.mysticdrew.journeymapteams.handlers.properties;

import journeymap.client.api.option.BooleanOption;
import journeymap.client.api.option.EnumOption;
import journeymap.client.api.option.OptionCategory;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

public class VanillaHandlerProperties
{
    OptionCategory category = new OptionCategory(MOD_ID + "-vanilla", "prop.category.label.vanilla", "prop.category.label.vanilla.tooltip");

    public final EnumOption<Color> defaultTeamColor;
    public final BooleanOption doOverrideAllyColor;
    public final EnumOption<Color> overrideAllyColor;

    public VanillaHandlerProperties()
    {
        this.defaultTeamColor = new EnumOption<>(category, "default-team-color", "prop.option.label.vanilla.default.team_color", Color.GREEN);
        this.doOverrideAllyColor = new BooleanOption(category, "over-ride-ally-color", "prop.option.label.vanilla.override_ally", false);
        this.overrideAllyColor = new EnumOption<>(category, "default-ally-color", "prop.option.label.vanilla.override_ally", Color.BLUE);
    }
}
