package net.mysticdrew.journeymapteams.handlers.properties;

import journeymap.api.v2.common.option.Option;
import journeymap.api.v2.common.option.OptionsRegistry;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DefaultServerPropertiesTest
{
    @Test
    void construction_registersSixOptionsWithExpectedDefaults()
    {
        // Unique token so this test does not collide with other tests sharing
        // the JVM-static OptionsRegistry.
        new DefaultServerProperties("test-server-options", "prop.category.label.vanilla");

        Map<String, Option<?>> registered =
                OptionsRegistry.OPTION_REGISTRY.get("journeymapteams-test-server-options");
        assertNotNull(registered, "category should be registered");
        assertEquals(6, registered.size(), "expected six options");

        assertEquals(Boolean.TRUE, registered.get("enforce-team-visibility").getDefaultValue());
        assertEquals(Boolean.FALSE, registered.get("hide-unteamed").getDefaultValue());
        assertEquals(Boolean.FALSE, registered.get("hide-allies").getDefaultValue());
        assertEquals(Boolean.TRUE, registered.get("hide-other-teams").getDefaultValue());
        assertEquals(Boolean.TRUE, registered.get("hide-teamed-from-unteamed").getDefaultValue());
        assertEquals(Boolean.TRUE, registered.get("ops-bypass-hiding").getDefaultValue());
    }
}
