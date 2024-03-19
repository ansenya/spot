package ru.senya.spot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import ru.senya.spot.configs.security.RsaKeyProperties;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyProperties.class)
@EnableJpaRepositories(basePackages = "ru.senya.spot.repos.jpa")
@EnableRedisRepositories(basePackages = "ru.senya.spot.repos.redis")
@EnableElasticsearchRepositories(basePackages = "ru.senya.spot.repos.es")
public class MytybeApplication {

    public static String MAIN_IP="video-spot.ru";
    public static String MAIN_PORT = "1984";
    public static String STORAGE_HOST="video-spot.ru/storage";

    public static void main(String[] args) {
        SpringApplication.run(MytybeApplication.class, args);
    }
}
