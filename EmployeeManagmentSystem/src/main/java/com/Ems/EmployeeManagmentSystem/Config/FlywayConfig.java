package com.Ems.EmployeeManagmentSystem.Config;

import org.springframework.beans.factory.annotation.Value;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FlywayConfig {

    private static final Logger logger = LoggerFactory.getLogger(FlywayConfig.class);

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Value("${flyway.baseline.enabled:false}")
    private boolean baselineEnabled;

    @Value("${flyway.migrate.enabled:false}")
    private boolean migrateEnabled;

    @Value("${flyway.repair.enabled:false}")
    private boolean repairEnabled;

    @Value("${flyway.clean.enabled:false}")
    private boolean cleanEnabled;

    @Value("${flyway.info.enabled:false}")
    private boolean infoEnabled;

    @Bean
    CommandLineRunner runFlyway() {
        logger.info("in FlywayConfig");
        return args -> {
            Flyway flyway = Flyway.configure()
                    .dataSource(dbUrl, dbUser, dbPassword)
                    .locations("classpath:db/migrations")
                    .baselineOnMigrate(baselineEnabled)
                    .outOfOrder(true)
                    .load();
            logger.info("Starting Flyway operations");
            if (infoEnabled) {
                logger.info("Running Flyway info...");
                flyway.info();
            }
            if (repairEnabled) {
                logger.info("Running Flyway repair...");
                flyway.repair();
            }
            logger.info("baseLine enabled {}", baselineEnabled);
            if (baselineEnabled) {
                logger.info("Running Flyway baseline...");
                flyway.baseline();
            }
            if (migrateEnabled) {
                logger.info("Running Flyway migrate...");
                flyway.migrate();
            }
            logger.info("Flyway run complete!");
        };
    }
}
