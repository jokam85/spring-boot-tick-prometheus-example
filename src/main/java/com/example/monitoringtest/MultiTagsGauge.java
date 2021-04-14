package com.example.monitoringtest;

import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
public class MultiTagsGauge {

    private final MeterRegistry meterRegistry;
    private int valueA;
    private int valueB;

    @PostConstruct
    void setup() {
        Gauge.builder("aaa.random.numbers", () -> valueA).tags("ab", "A").register(meterRegistry);
        Gauge.builder("aaa.random.numbers", () -> valueB).tags("ab", "B").register(meterRegistry);
    }

    @Scheduled(fixedRate = 1500)
    void ping() {
        valueA += (int) (Math.random() * 10);
        valueB += (int) (Math.random() * 12); // faster growing
    }
}
