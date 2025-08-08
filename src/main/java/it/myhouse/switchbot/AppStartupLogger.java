package it.myhouse.switchbot;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AppStartupLogger {

    private static final Logger logger = LoggerFactory.getLogger(AppStartupLogger.class);

    @Autowired
    private Environment env;

    @PostConstruct
    public void logConfiguration() {
        logger.info("=== CONFIGURATION SUMMARY ===");

        logMasked("SWITCHBOT_TOKEN", env.getProperty("switchbot.token"));
        logMasked("SWITCHBOT_SECRET", env.getProperty("switchbot.secret"));

        log("DB URL", env.getProperty("spring.datasource.url"));
        log("DB USER", env.getProperty("spring.datasource.username"));
        logMasked("DB PWD", env.getProperty("spring.datasource.password"));

        log("ENABLE_SCHEDULER", env.getProperty("ENABLE_SCHEDULER"));

        logger.info("==============================");
    }

    private void log(String key, String value) {
        logger.info("{} = {}", key, value != null ? value : "(null)");
    }

    private void logMasked(String key, String value) {
        String masked = (value != null && value.length() > 4)
                ? "****" + value.substring(value.length() - 4)
                : "****";
        logger.info("{} = {}", key, masked);
    }
}
