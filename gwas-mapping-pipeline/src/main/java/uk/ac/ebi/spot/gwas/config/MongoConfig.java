package uk.ac.ebi.spot.gwas.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

public class MongoConfig {

    @Configuration
    @EnableMongoRepositories(basePackages =  {"uk.ac.ebi.spot.gwas.repository.mongo"})
    @EnableTransactionManagement
    @Profile({"dev", "test", "local","cluster"})
    public static class MongoConfigDev extends AbstractMongoClientConfiguration {

        @Autowired
        Config config;


        @Override
        protected String getDatabaseName() {
            return config.getDbName();
        }



        @Override
        public MongoClient mongoClient() {
            String mongoUri = config.getMongoUri();
            String dbUser = config.getDbUser();
            String dbPassword = config.getDbPassword();
            String credentials = "";
            if (dbUser != null && dbPassword != null) {
                dbUser = dbUser.trim();
                dbPassword = dbPassword.trim();
                if (!dbUser.equalsIgnoreCase("") &&
                        !dbPassword.equalsIgnoreCase("")) {
                    credentials = dbUser + ":" + dbPassword + "@";
                }
            }

         return MongoClients.create(MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString("mongodb://" + credentials + mongoUri))
                    .build());
            //return new MongoClient(new MongoClientURI("mongodb://" + mongoUri));
           // return new MongoClient(new MongoClientURI("mongodb://" + credentials + mongoUri));
            //String mongoUri = systemConfigProperties.getMongoUri();
            //return new MongoClient(new MongoClientURI("mongodb://" + mongoUri));
        }

        @Bean
        public MongoTemplate mongoTemplate() {
            return new MongoTemplate(mongoClient(), getDatabaseName());
        }


    }

}
