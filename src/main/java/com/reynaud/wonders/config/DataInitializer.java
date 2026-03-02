package com.reynaud.wonders.config;

import com.reynaud.wonders.dao.CardDAO;
import com.reynaud.wonders.dao.EffectDAO;
import com.reynaud.wonders.dao.UserDAO;
import com.reynaud.wonders.dao.WonderDAO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * Configuration class that initializes the database with static game data
 * and test users only on first startup (when tables are empty).
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(CardDAO cardDAO, EffectDAO effectDAO, 
                                   WonderDAO wonderDAO, UserDAO userDAO, 
                                   DataSource dataSource) {
        return args -> {
            // Only load data if the database is empty
            if (cardDAO.count() == 0 && effectDAO.count() == 0 && wonderDAO.count() == 0) {
                System.out.println("Database is empty. Loading initial data from data.sql...");
                
                ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                populator.addScript(new ClassPathResource("data.sql"));
                populator.setContinueOnError(false);
                populator.execute(dataSource);
                
                System.out.println("Initial data loaded successfully.");
            } else {
                System.out.println("Database already contains data. Skipping data.sql execution.");
                
                // Always recreate test users on restart
                if (userDAO.findByUsername("alice") == null || 
                    userDAO.findByUsername("bob") == null || 
                    userDAO.findByUsername("charlie") == null || 
                    userDAO.findByUsername("admin") == null) {
                    
                    System.out.println("Recreating test users...");
                    ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
                    populator.addScript(new ClassPathResource("test-users.sql"));
                    populator.setContinueOnError(true);
                    populator.execute(dataSource);
                    System.out.println("Test users recreated.");
                }
            }
        };
    }
}
