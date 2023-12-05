package com.fsc.metric;

import com.fsc.common.config.ConfigTools3;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication()
@ComponentScan(basePackages = {"com.fsc"})
@EnableScheduling
public class GraphMetricServerApplication {

    public static void main(String[] args) throws Exception {

        ConfigTools3.load("cfg");
        SpringApplication.run(GraphMetricServerApplication.class, args);
        System.out.println("********* Graph Metric Server Start *********");

    }

}
