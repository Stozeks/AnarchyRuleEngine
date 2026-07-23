package me.stozeks.anarchyruleengine.condition;

import me.stozeks.anarchyruleengine.model.InteractionContext;
import me.stozeks.anarchyruleengine.service.WorldGuardRegionService;

public final class RegionCondition implements RuleCondition {

    private final String regionName;
    private final WorldGuardRegionService regionService;

    public RegionCondition(String regionName) {
        this.regionName = regionName;
        this.regionService = new WorldGuardRegionService();
    }

    @Override
    public boolean matches(InteractionContext context) {
        return regionService.isInsideRegion(
                context.getLocation(),
                regionName
        );
    }
}