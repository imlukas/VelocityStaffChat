package me.xii69.velocitystaffchat.data.database.databases;

import me.xii69.velocitystaffchat.data.database.impl.sql.impl.MySQLDatabase;
import me.xii69.velocitystaffchat.data.database.impl.sql.statement.BooleanStatementObject;
import me.xii69.velocitystaffchat.data.database.impl.sql.statement.StringStatementObject;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MySQLStorage extends MySQLDatabase implements ChatStorage {

    private static final String TABLE_NAME = "staff-chat";
    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " (uuid VARCHAR(36) PRIMARY KEY, toggled BOOLEAN NOT NULL)";
    private static final String IS_TOGGLED = "SELECT toggled FROM " + TABLE_NAME + " WHERE uuid = ?";
    private static final String SET_TOGGLED = "INSERT INTO " + TABLE_NAME + " (uuid, toggled) VALUES (?, ?) ON DUPLICATE KEY UPDATE toggled = values(toggled)";
    private static final String WIPE = "DELETE FROM " + TABLE_NAME;


    @Override
    public CompletableFuture<Boolean> isToggled(UUID playerId) {
        return runQueryAsync(IS_TOGGLED, List.of(StringStatementObject.create(playerId.toString())), resultSet -> {
            if (resultSet == null || resultSet.isClosed() || !resultSet.next()) {
                return false;
            }

            return resultSet.getBoolean("toggled");
        });
    }

    @Override
    public CompletableFuture<Void> setToggled(UUID playerId, boolean toggled) {
        return runUpdateAsync(SET_TOGGLED, List.of(
                StringStatementObject.create(playerId.toString()),
                BooleanStatementObject.create(toggled)));
    }

    @Override
    public CompletableFuture<Void> wipe() {
        return runUpdateAsync(WIPE);
    }

    @Override
    protected Collection<String> getTables() {
        return List.of(TABLE_NAME);
    }

    @Override
    protected void createTables() {
        runUpdate(CREATE_TABLE);
    }
}
