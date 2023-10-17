package net.mysticdrew.journeymapteams.handlers.properties;

import journeymap.client.api.option.KeyedEnum;

public enum Color implements KeyedEnum
{
    BLACK("label.color.black", 0),
    DARK_BLUE("label.color.dark_blue", 170),
    DARK_GREEN("label.color.dark_green", 43520),
    DARK_AQUA("label.color.dark_aqua", 43690),
    DARK_RED("label.color.dark_red", 11141120),
    DARK_PURPLE("label.color.dark_purple", 11141290),
    GOLD("label.color.gold", 16755200),
    GRAY("label.color.gray", 11184810),
    DARK_GRAY("label.color.dark_gray", 5592405),
    BLUE("label.color.blue", 5592575),
    GREEN("label.color.green", 5635925),
    AQUA("label.color.aqua", 5636095),
    RED("label.color.red", 16733525),
    LIGHT_PURPLE("label.color.light_purple", 16733695),
    YELLOW("label.color.yellow", 16777045),
    WHITE("label.color.white", 16777215);

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
