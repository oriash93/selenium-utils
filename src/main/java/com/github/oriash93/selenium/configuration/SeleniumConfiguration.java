package com.github.oriash93.selenium.configuration;

import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

import java.time.Duration;

@Configuration
public class SeleniumConfiguration {

    @Value("${selenium.driver-system-property}")
    private String driverSystemProperty;

    @Value("${selenium.driver-path}")
    private String driverPath;

    @Value("${selenium.run-headless}")
    private boolean isHeadless;

    @Value("${selenium.start-maximized}")
    private boolean isMaximized;

    @PostConstruct
    public void init() {
        String path = getClass().getClassLoader().getResource(driverPath).getPath();
        System.setProperty(driverSystemProperty, path);
    }

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(isHeadless);
        if (isMaximized) {
            chromeOptions.addArguments("--start-maximized");
        }
        return chromeOptions;
    }

    @Bean
    public ChromeDriver chromeDriver() {
        return new ChromeDriver(chromeOptions());
    }

    @Bean
    public Actions actions() {
        return new Actions(chromeDriver());
    }

    @Bean
    public FluentWait<ChromeDriver> fluentWait() {
        return new FluentWait<>(chromeDriver())
                .pollingEvery(Duration.ofMillis(500))
                .withTimeout(Duration.ofMillis(10000))
                .ignoring(NoSuchElementException.class);
    }
}
