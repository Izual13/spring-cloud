package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigInteger;
import java.net.URI;
import java.security.SecureRandom;


@SpringBootApplication
@EnableEurekaClient
@EnableScheduling
public class MessageBrokerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageBrokerApplication.class, args);
    }

    @RestController
    static class BasicRestController {

        @Autowired
        GenerateMessagingService generateMessagingService;

        @RequestMapping(value = "/loginUser/{userName}", method = RequestMethod.GET)
        public String loginUser(@PathVariable("userName") String userName) {
            generateMessagingService.loginUser(userName);
            return "ok";
        }
    }

    @Service
    static class GenerateMessagingService {

        @Autowired
        private DiscoveryClient discoveryClient;

        public void loginUser(String userName) {
            sendMessage("/loginUser/" + userName, null);
            System.out.println("loginUser");
        }

        @Scheduled(fixedDelay = 1000)
        public void scheduler() {
            BigInteger prime = BigInteger.probablePrime(512, new SecureRandom());
            sendMessage("/scheduler/", prime.toString());
        }

        private void sendMessage(String endpoint, String request) {
            RestTemplate restTemplate = new RestTemplate();
            discoveryClient.getInstances("websocket-server").forEach((ServiceInstance instance) -> {
                URI storesUri = URI.create(instance.getUri().toString() + endpoint);
                ResponseEntity<String> string = restTemplate.postForEntity(
                        storesUri,
                        request,
                        String.class);
                System.out.println(string);
            });
        }
    }
}
