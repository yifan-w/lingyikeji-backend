package com.lingyikeji.backend.config;

import com.mongodb.client.MongoClient;
import dev.morphia.Datastore;
import dev.morphia.Morphia;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Created by Yifan Wang on 2023/10/15. */
@Configuration
@RequiredArgsConstructor
public class MongoConfig {
  private final MongoClient client;

  @Value("${spring.data.mongodb.database}")
  private String db;

  @Bean
  public Datastore getDatastore() {
    Datastore datastore = Morphia.createDatastore(client, db);
    datastore.getMapper().mapPackage("com.lingyikeji.backend.domain.entities");
    datastore.ensureIndexes();

    return datastore;
  }
}
