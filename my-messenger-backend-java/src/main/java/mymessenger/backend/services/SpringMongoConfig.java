/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mymessenger.backend.services;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 *
 * @author guilherme
 */
@Configuration
@EnableMongoRepositories("mymessenger.backend")
public class SpringMongoConfig extends AbstractMongoConfiguration {

//    @Value("${spring.profiles.active}")
//    private String profileActive;

    @Value("${spring.data.mongodb.host}")
    private String mongoHost;

    @Value("${spring.data.mongodb.port}")
    private int mongoPort;

    @Value("${spring.data.mongodb.database}")
    private String database;

    @Value("${spring.data.mongodb.username}")
    private String username;

    @Value("${spring.data.mongodb.password}")
    private String password;

    @Value("${spring.data.mongodb.ssl}")
    private boolean ssl;

    @Override
    @Bean
    public Mongo mongo() throws Exception {
        
        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());
        
        MongoClientOptions.Builder options = MongoClientOptions
                .builder()
                .sslEnabled(ssl)
                .sslInvalidHostNameAllowed(true);

        ServerAddress sa = new ServerAddress(mongoHost, mongoPort);
        
        MongoClient mongoClient = new MongoClient(sa,
                                           Arrays.asList(credential),
                                           options.build());
        
        return mongoClient;
    }

    @Override
    protected String getDatabaseName() {
        return database;
    }
}
