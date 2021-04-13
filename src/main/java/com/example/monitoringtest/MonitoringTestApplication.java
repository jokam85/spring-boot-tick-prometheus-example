package com.example.monitoringtest;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@SpringBootApplication
@EnableScheduling
public class MonitoringTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringTestApplication.class, args);
    }

    @RestController
    @RequiredArgsConstructor
    @RequestMapping("/api")
    static class MetricsApi {
        private final ErrorCounterService errorCounterService;

        @GetMapping("/set-errors/{numOfErrors}")
        int increment(@PathVariable int numOfErrors) {
            errorCounterService.setNumOfErrors(numOfErrors);
            return numOfErrors;
        }
    }


    @Service
    @Slf4j
    @RequiredArgsConstructor
    static class CounterService {
        private final MeterRegistry meterRegistry;
        private Counter counter;

        @PostConstruct
        void setup() {
            this.counter = Counter.builder("xxx.counter")
                    .baseUnit("TIMES")
                    .register(meterRegistry);
        }

        @Scheduled(fixedRate = 3000)
        void count() {
            counter.increment();
            log.info("counting + 1 ..." + counter.count());
        }
    }


    @Service
    @Slf4j
    @RequiredArgsConstructor
    static class ErrorCounterService {

        @Getter
        Integer numOfErrors = 0;

        @Getter
        Integer numOfSuccesses = 0;

        void setNumOfErrors(int numOfErrors) {
            this.numOfErrors = numOfErrors;
            log.debug("Errors set to " + this.numOfErrors);
        }

        @Scheduled(fixedRate = 2000)
        void incrementSuccess() {
            this.numOfSuccesses++;
            log.debug("Success incremented to " + this.numOfSuccesses);
        }
    }


    @Component
    @RequiredArgsConstructor
    static class MetricCollector {

        private final ErrorCounterService errorCounterService;
        private final MeterRegistry meterRegistry;

        @PostConstruct
        void setup() {
            Gauge.builder("xxx.error.count.gauge", errorCounterService, ErrorCounterService::getNumOfErrors)
                    .register(meterRegistry);
            Gauge.builder("xxx.success.count.gauge", errorCounterService, ErrorCounterService::getNumOfSuccesses)
                    .register(meterRegistry);
        }

    }

}
