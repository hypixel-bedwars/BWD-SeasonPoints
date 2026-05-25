package dev.bwd.seasonpoints.database.repositories;

import dev.bwd.seasonpoints.database.connection.DatabaseManager;
import dev.bwd.seasonpoints.models.TransactionType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;
import javax.annotation.Nullable;

public class TransactionRepository {

  private final DatabaseManager databaseManager;

  public TransactionRepository(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }

  public void createTransaction(
    int seasonId,
    @Nullable UUID senderUuid,
    UUID receiverUuid,
    int amount,
    TransactionType transactionType
  ) {
    String sql = """
          INSERT INTO point_transactions (

              season_id,

              sender_uuid,
              receiver_uuid,

              amount,

              transaction_type

          )

          VALUES (?, ?, ?, ?, ?)
      """;

    try (
      Connection connection = databaseManager.getConnection();
      PreparedStatement statement = connection.prepareStatement(sql)
    ) {
      statement.setInt(1, seasonId);

      statement.setObject(2, senderUuid);

      statement.setObject(3, receiverUuid);

      statement.setInt(4, amount);

      statement.setString(5, transactionType.name());

      statement.executeUpdate();
    } catch (SQLException exception) {
      exception.printStackTrace();
    }
  }
}
