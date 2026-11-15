package com.diumi.backend.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;

class FlywayMigrationTest {
    private static final String URL =
            "jdbc:h2:mem:flyway_migration;MODE=MySQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE";

    @Test
    void appliesAllMigrationsAndSeedsCafeAndFastFood() throws Exception {
        Flyway flyway = Flyway.configure()
                .dataSource(URL, "sa", "")
                .locations("classpath:db/migration")
                .load();

        flyway.migrate();

        try (Connection connection = DriverManager.getConnection(URL, "sa", "")) {
            assertThat(count(connection, "SELECT COUNT(*) FROM flyway_schema_history "
                    + "WHERE success = TRUE AND version IS NOT NULL"))
                    .isEqualTo(10);
            assertThat(count(connection,
                    "SELECT COUNT(*) FROM ai_guide_rule WHERE scenario_type = 'CAFE'"))
                    .isEqualTo(43);
            assertThat(count(connection,
                    "SELECT COUNT(*) FROM ai_guide_rule WHERE scenario_type = 'FAST_FOOD'"))
                    .isEqualTo(100);
            assertThat(count(connection,
                    "SELECT COUNT(*) FROM scenario_step WHERE scenario_id = 2"))
                    .isEqualTo(6);
            assertThat(count(connection,
                    "SELECT COUNT(*) FROM mission WHERE scenario_id = 2 AND is_active = TRUE"))
                    .isEqualTo(2);
        }
    }

    private long count(Connection connection, String sql) throws Exception {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }
}
