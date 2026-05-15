package net.mysticdrew.journeymapteams.handlers.properties;

import journeymap.api.v2.common.option.BooleanOption;
import journeymap.api.v2.common.option.OptionCategory;

import static net.mysticdrew.journeymapteams.Constants.MOD_ID;

/**
 * Server-side admin options backed by the JourneyMap server-options API. The
 * six {@link BooleanOption} instances self-register into the common
 * {@code OptionsRegistry} on construction, so this object must only be built
 * inside an {@code OPTIONS_REGISTRY_EVENT} handler.
 */
public class DefaultServerProperties implements ServerProperties
{
    private final OptionCategory category;
    private final BooleanOption enforceTeamVisibility;
    private final BooleanOption hideUnteamed;
    private final BooleanOption hideAllies;
    private final BooleanOption hideOtherTeams;
    private final BooleanOption hideTeamedFromUnteamed;
    private final BooleanOption opsBypassHiding;

    /**
     * @param token    a short suffix making the category id unique per handler,
     *                 e.g. {@code "vanilla"} or {@code "ftbteams"}.
     * @param labelKey i18n key for the category label.
     */
    public DefaultServerProperties(String token, String labelKey)
    {
        this.category = new OptionCategory(MOD_ID + "-" + token, labelKey);
        this.enforceTeamVisibility = new BooleanOption(category, "enforce-team-visibility",
                "prop.option.label.enforce_visibility", true, true);
        this.hideUnteamed = new BooleanOption(category, "hide-unteamed",
                "prop.option.label.hide_unteamed", false);
        this.hideAllies = new BooleanOption(category, "hide-allies",
                "prop.option.label.hide_allies", false);
        this.hideOtherTeams = new BooleanOption(category, "hide-other-teams",
                "prop.option.label.hide_other_teams", true);
        this.hideTeamedFromUnteamed = new BooleanOption(category, "hide-teamed-from-unteamed",
                "prop.option.label.hide_teamed_from_unteamed", true);
        this.opsBypassHiding = new BooleanOption(category, "ops-bypass-hiding",
                "prop.option.label.ops_bypass", true);
        this.enforceTeamVisibility.setSortOrder(1);
        this.hideUnteamed.setSortOrder(2);
        this.hideAllies.setSortOrder(3);
        this.hideOtherTeams.setSortOrder(4);
        this.hideTeamedFromUnteamed.setSortOrder(5);
        this.opsBypassHiding.setSortOrder(6);
    }

    public OptionCategory getCategory()
    {
        return category;
    }

    @Override
    public boolean getEnforceTeamVisibility()
    {
        return enforceTeamVisibility.get();
    }

    @Override
    public boolean getHideUnteamed()
    {
        return hideUnteamed.get();
    }

    @Override
    public boolean getHideAllies()
    {
        return hideAllies.get();
    }

    @Override
    public boolean getHideOtherTeams()
    {
        return hideOtherTeams.get();
    }

    @Override
    public boolean getHideTeamedFromUnteamed()
    {
        return hideTeamedFromUnteamed.get();
    }

    @Override
    public boolean getOpsBypassHiding()
    {
        return opsBypassHiding.get();
    }
}
