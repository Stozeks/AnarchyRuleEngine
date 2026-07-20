package me.stozeks.anarchyruleengine.service;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;

public final class WorldGuardRegionService {

    public boolean isInsideRegion(Location location, String regionName) {
        if (location == null || location.getWorld() == null) {
            return false;
        }

        if (regionName == null || regionName.trim().isEmpty()) {
            return false;
        }

        ApplicableRegionSet regions = WorldGuard.getInstance()
                .getPlatform()
                .getRegionContainer()
                .createQuery()
                .getApplicableRegions(BukkitAdapter.adapt(location));

        for (ProtectedRegion region : regions) {
            if (region.getId().equalsIgnoreCase(regionName)) {
                return true;
            }
        }

        return false;
    }
}