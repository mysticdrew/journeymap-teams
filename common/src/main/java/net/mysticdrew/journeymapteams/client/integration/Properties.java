package net.mysticdrew.journeymapteams.client.integration;

import journeymap.client.api.option.EnumOption;
import journeymap.client.api.option.KeyedEnum;
import journeymap.client.api.option.OptionCategory;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

public class Properties
{
    OptionCategory category = new OptionCategory(MOD_ID, "JourneyMap Teams", "JourneyMap Teams Options");

    public final EnumOption<Color> defaultTeamColor;

    public Properties()
    {
        //TODO: i18n
        this.defaultTeamColor = new EnumOption<>(category, "default-team-color", "Default Team Color", Color.BLUE);
    }


    public enum Color implements KeyedEnum
    {
        BLACK("BLACK", 0),
        DARK_BLUE("DARK_BLUE", 170),
        DARK_GREEN("DARK_GREEN", 43520),
        DARK_AQUA("DARK_AQUA", 43690),
        DARK_RED("DARK_RED", 11141120),
        DARK_PURPLE("DARK_PURPLE", 11141290),
        GOLD("GOLD", 16755200),
        GRAY("GRAY", 11184810),
        DARK_GRAY("DARK_GRAY", 5592405),
        BLUE("BLUE", 5592575),
        GREEN("GREEN", 5635925),
        AQUA("AQUA", 5636095),
        RED("RED", 16733525),
        LIGHT_PURPLE("LIGHT_PURPLE", 16733695),
        YELLOW("YELLOW", 16777045),
        WHITE("WHITE", 16777215);

        private final String key;
        private final int color;

        Color(String key, int color)
        {
            this.key = key;
            this.color = color;
        }

        @Override
        public String getKey()
        {
            return this.key;
        }

        public int getColor()
        {
            return this.color;
        }
    }

}
