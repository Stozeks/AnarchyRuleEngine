package me.stozeks.anarchyruleengine.service;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public final class CooldownService {

    private final Map<UUID, Map<String, Long>> cooldowns =
            new HashMap<>();

    public void startCooldown(
            UUID playerId,
            String cooldownId,
            long durationMillis
    ) {
        Objects.requireNonNull(
                playerId,
                "playerId"
        );

        String normalizedId = normalizeCooldownId(cooldownId);

        if (durationMillis <= 0) {
            throw new IllegalArgumentException(
                    "Cooldown duration must be greater than zero."
            );
        }

        long expiresAt =
                System.currentTimeMillis() + durationMillis;

        cooldowns
                .computeIfAbsent(
                        playerId,
                        ignored -> new HashMap<>()
                )
                .put(normalizedId, expiresAt);
    }

    public boolean isOnCooldown(
            UUID playerId,
            String cooldownId
    ) {
        return getRemainingMillis(
                playerId,
                cooldownId
        ) > 0;
    }

    public long getRemainingMillis(
            UUID playerId,
            String cooldownId
    ) {
        Objects.requireNonNull(
                playerId,
                "playerId"
        );

        String normalizedId = normalizeCooldownId(cooldownId);

        Map<String, Long> playerCooldowns =
                cooldowns.get(playerId);

        if (playerCooldowns == null) {
            return 0;
        }

        Long expiresAt =
                playerCooldowns.get(normalizedId);

        if (expiresAt == null) {
            return 0;
        }

        long remaining =
                expiresAt - System.currentTimeMillis();

        if (remaining <= 0) {
            removeCooldown(
                    playerId,
                    normalizedId
            );

            return 0;
        }

        return remaining;
    }

    public long getRemainingSeconds(
            UUID playerId,
            String cooldownId
    ) {
        long remainingMillis =
                getRemainingMillis(
                        playerId,
                        cooldownId
                );

        if (remainingMillis <= 0) {
            return 0;
        }

        return (remainingMillis + 999) / 1000;
    }

    public void removeCooldown(
            UUID playerId,
            String cooldownId
    ) {
        Objects.requireNonNull(
                playerId,
                "playerId"
        );

        String normalizedId = normalizeCooldownId(cooldownId);

        Map<String, Long> playerCooldowns =
                cooldowns.get(playerId);

        if (playerCooldowns == null) {
            return;
        }

        playerCooldowns.remove(normalizedId);

        if (playerCooldowns.isEmpty()) {
            cooldowns.remove(playerId);
        }
    }

    public void clearCooldowns(UUID playerId) {
        Objects.requireNonNull(
                playerId,
                "playerId"
        );

        cooldowns.remove(playerId);
    }

    public void clearAllCooldowns() {
        cooldowns.clear();
    }

    private String normalizeCooldownId(String cooldownId) {
        Objects.requireNonNull(
                cooldownId,
                "cooldownId"
        );

        String normalizedId =
                cooldownId.trim().toLowerCase(Locale.ROOT);

        if (normalizedId.isEmpty()) {
            throw new IllegalArgumentException(
                    "Cooldown id cannot be empty."
            );
        }

        return normalizedId;
    }
}