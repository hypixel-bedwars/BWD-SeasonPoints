package dev.bwd.seasonpoints.models;

import java.util.UUID;

public record LeaderboardEntry(UUID uuid, String username, int points) {}
