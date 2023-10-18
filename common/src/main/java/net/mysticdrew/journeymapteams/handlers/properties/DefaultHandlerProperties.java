package net.mysticdrew.journeymapteams.handlers.properties;

import journeymap.client.api.option.BooleanOption;
import journeymap.client.api.option.EnumOption;
import journeymap.client.api.option.OptionCategory;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

public class DefaultHandlerProperties implements Properties
{
    private final OptionCategory category;
    private final EnumOption<Color> teamColor;
    private final EnumOption<Color> allyColor;
    private final BooleanOption forceAllyColor;
    private final BooleanOption forceTeamColor;
    private final BooleanOption showIconColor;
    private final BooleanOption showNameColor;

    public DefaultHandlerProperties(String token, String labelKey)
    {
        this.category = new OptionCategory(MOD_ID + "-" + token, labelKey);
        this.teamColor = new EnumOption<>(category, "team-color", "prop.option.label.team_color", Color.GREEN);
        this.allyColor = new EnumOption<>(category, "ally-color", "prop.option.label.ally_color", Color.BLUE);
        this.forceAllyColor = new BooleanOption(category, "force-ally-color", "prop.option.label.force_ally", false);
        this.forceTeamColor = new BooleanOption(category, "force-team-color", "prop.option.label.force_team", false);
        this.showIconColor = new BooleanOption(category, "show-icon-color", "prop.option.label.show_icon_color", true);
        this.showNameColor = new BooleanOption(category, "show-name-color", "prop.option.label.show_name_color", true);
    }

    public int getTeamColor()
    {
        return teamColor.get().getColor();
    }

    public int getAllyColor()
    {
        return allyColor.get().getColor();
    }

    public boolean getForceAllyColor()
    {
        return forceAllyColor.get();
    }

    public boolean getForceTeamColor()
    {
        return forceTeamColor.get();
    }

    public boolean getShowIconColor()
    {
        return showIconColor.get();
    }

    public boolean getShowNameColor()
    {
        return showNameColor.get();
    }
}
