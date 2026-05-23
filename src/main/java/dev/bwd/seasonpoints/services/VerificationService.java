package dev.bwd.seasonpoints.services;

import dev.bwd.seasonpoints.database.repositories.VerificationRepository;
import dev.bwd.seasonpoints.integrations.discord.DiscordVerificationClient;
import java.util.UUID;

public class VerificationService {

  private final VerificationRepository verificationRepository;

  private final DiscordVerificationClient discordClient;

  public VerificationService(
    VerificationRepository verificationRepository,
    DiscordVerificationClient discordClient
  ) {
    this.verificationRepository = verificationRepository;

    this.discordClient = discordClient;
  }

  public boolean isVerified(UUID uuid, String username) {
    String storedDiscordId = verificationRepository.getDiscordId(uuid);

    if (storedDiscordId == null) {
      return false;
    }

    String currentDiscordId = discordClient.getDiscordId(username);

    if (currentDiscordId == null) {
      return false;
    }

    return storedDiscordId.equals(currentDiscordId);
  }

  public void syncVerification(UUID uuid, String username) {
    String discordId = discordClient.getDiscordId(username);

    if (discordId == null) {
      verificationRepository.unlinkDiscord(uuid);

      return;
    }

    verificationRepository.linkDiscord(uuid, discordId);
  }
}
