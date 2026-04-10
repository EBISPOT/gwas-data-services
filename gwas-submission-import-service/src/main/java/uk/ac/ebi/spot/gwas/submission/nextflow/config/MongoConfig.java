package uk.ac.ebi.spot.gwas.submission.nextflow.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.ac.ebi.spot.gwas.deposition.config.SystemConfigProperties;

public class MongoConfig {

    @Configuration
    @EnableMongoRepositories(basePackages =  {"uk.ac.ebi.spot.gwas.submission.nextflow.mongo.repository"})
    @EnableTransactionManagement
    @Profile({"dev", "test", "local","cluster"})
    public static class MongoConfigDev extends AbstractMongoConfiguration {

        @Autowired
        NextFlowJobConfig nextFlowJobConfig;

        @Autowired
        private SystemConfigProperties systemConfigProperties;


        @Override
        protected String getDatabaseName() {
            return nextFlowJobConfig.getDbName();
        }

        @Bean
        public GridFsTemplate gridFsTemplate() throws Exception {
            return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
        }


        @Override
        public MongoClient mongoClient() {
            String mongoUri = systemConfigProperties.getMongoUri();
            String dbUser = systemConfigProperties.getDbUser();
            String dbPassword = systemConfigProperties.getDbPassword();
            String credentials = "";
            if (dbUser != null && dbPassword != null) {
                dbUser = dbUser.trim();
                dbPassword = dbPassword.trim();
                if (!dbUser.equalsIgnoreCase("") &&
                        !dbPassword.equalsIgnoreCase("")) {
                    credentials = dbUser + ":" + dbPassword + "@";
                }
            }

            //return new MongoClient(new MongoClientURI("mongodb://" + mongoUri));
            return new MongoClient(new MongoClientURI("mongodb://" + credentials + mongoUri));
            //String mongoUri = systemConfigProperties.getMongoUri();
            //return new MongoClient(new MongoClientURI("mongodb://" + mongoUri));
        }

        @Bean
        public MongoTemplate mongoTemplate() {
            return new MongoTemplate(mongoClient(), getDatabaseName());
        }


    }

}
