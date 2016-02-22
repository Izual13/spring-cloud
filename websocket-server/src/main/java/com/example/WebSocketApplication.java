package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.core.MessageSendingOperations;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

@SpringBootApplication
@EnableDiscoveryClient
@EnableZuulProxy
public class WebSocketApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebSocketApplication.class, args);
    }

    @Configuration
    @EnableWebSocketMessageBroker
    static class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {

        @Override
        public void configureMessageBroker(MessageBrokerRegistry config) {
            config.enableSimpleBroker("/topic", "/scheduler");
        }

        @Override
        public void registerStompEndpoints(StompEndpointRegistry registry) {
            registry.addEndpoint("/socket").withSockJS();
        }

    }


    @RestController
    static class BasicRestController {

        @Autowired
        MessagingService messagingService;

        @RequestMapping(value = "/loginUser/{userName}", method = RequestMethod.POST)
        public String loginUser(@PathVariable("userName") String userName) {
            messagingService.loginUser(userName);
            return "ok";
        }

        @RequestMapping(value = "/scheduler", method = RequestMethod.POST)
        public String scheduler(@RequestBody String prime) {
            messagingService.scheduler(prime);
            return "ok";
        }
    }

    @Controller
    static class RedirectController {
        @Value("${spring.cloud.client.ipAddress}")
        String host;
        @Value("${server.port}")
        String port;

        @RequestMapping(value = "/redirect", method = RequestMethod.GET)
        private String processForm() {
            String url = "http://" + host + ":" + port;
            System.out.println(url);
            return "redirect:" + url;
        }
    }

    @Configuration
    static class StaticResourceConfiguration extends WebMvcConfigurerAdapter {

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
            registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        }

        @Override
        public void addViewControllers(ViewControllerRegistry registry) {
            registry.addViewController("/")
                    .setViewName("forward:/index.html");
        }
    }

    @Service
    static class MessagingService {
        @Autowired
        private MessageSendingOperations<String> messagingTemplate;

        public void loginUser(String userName) {
            System.out.println("loginUser");
            String destination = "/topic";
            this.messagingTemplate.convertAndSend(destination, userName);
        }

        public void scheduler(String prime) {
            String destination = "/scheduler";
            this.messagingTemplate.convertAndSend(destination, prime);
        }
    }
}
