package dev.bwd.seasonpoints.integrations.discord;

import java.util.UUID;

public class DiscordVerificationClient {

  public boolean isVerified(UUID uuid) {
    /*
            Dummy implementation for now.

            Later this will:
            - call your Discord bot API
            - query another database
            - verify guild membership
            - validate linked account
        */

    return true;
  }

  public String getDiscordId(UUID uuid) {
    /*
            Dummy implementation.

            Later:
            - fetch linked Discord ID
            - return null if not linked
        */

    return "795526316832849932";
  }
}
