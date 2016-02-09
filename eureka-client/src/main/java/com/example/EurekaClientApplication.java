package com.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.sidecar.EnableSidecar;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SpringBootApplication(exclude = {DispatcherServletAutoConfiguration.class, WebMvcAutoConfiguration.class})
@EnableEurekaClient
@EnableSidecar
public class EurekaClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EurekaClientApplication.class, args);
    }

    @RestController("/test")
    static class Controller {

        @Autowired
        Environment env;


        @RequestMapping(method = RequestMethod.GET)
        public String hello() throws InterruptedException {
            Thread thread = Thread.currentThread();
            System.out.println(thread.getId() + " " + thread.getName());
            Thread.sleep(4000);
            Map<String, Object> map = new HashMap();
            for (Iterator it = ((AbstractEnvironment) env).getPropertySources().iterator(); it.hasNext(); ) {
                PropertySource propertySource = (PropertySource) it.next();
                if (propertySource instanceof MapPropertySource) {
                    map.putAll(((MapPropertySource) propertySource).getSource());
                }
            }

            map.forEach((x, y) -> System.out.println(x + ":" + y));

            return "Hello, World!";
        }
    }

}
