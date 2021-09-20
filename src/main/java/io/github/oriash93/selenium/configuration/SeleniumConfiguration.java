package io.github.oriash93.selenium.configuration;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.Duration;

@Slf4j
@Configuration
public class SeleniumConfiguration {
    private static final String DRIVER_SYSTEM_PROPERTY = "webdriver.chrome.driver";
    private static final String DRIVER_DIR_PATH = "driver";
    private static final String DRIVER_EXE_PATH = "chromedriver.exe";
    private static final String START_MAXIMIZED_FLAG = "--start-maximized";

    @Value("${selenium.headless:true}")
    private boolean headless;

    @Value("${selenium.maximized:true}")
    private boolean maximized;

    @Value("${selenium.fluent-wait.polling-interval-ms:500}")
    private long fluentWaitPollingInterval;

    @Value("${selenium.fluent-wait.timeout-interval-ms:10000}")
    private long fluentWaitTimeoutInterval;

    @PostConstruct
    public void init() throws IOException {
        log.info("Copying web driver started");
        URL resource = getClass().getClassLoader().getResource(DRIVER_EXE_PATH);
        File driverDirectory = new File(DRIVER_DIR_PATH);
        if (!driverDirectory.exists()) {
            driverDirectory.mkdirs();
            log.info("Web driver directory created in {}", driverDirectory.toPath());
        }
        File chromeDriver = new File(DRIVER_DIR_PATH + File.separator + DRIVER_EXE_PATH);
        if (!chromeDriver.exists()) {
            chromeDriver.createNewFile();
            log.info("Web driver file created in path {}", chromeDriver.toPath());
            FileUtils.copyURLToFile(resource, chromeDriver);
        }
        log.info("Copying web driver finished");
        System.setProperty(DRIVER_SYSTEM_PROPERTY, chromeDriver.getAbsolutePath());
    }

    @Bean
    public ChromeOptions chromeOptions() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(headless);
        if (maximized) {
            chromeOptions.addArguments(START_MAXIMIZED_FLAG);
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
                .pollingEvery(Duration.ofMillis(fluentWaitPollingInterval))
                .withTimeout(Duration.ofMillis(fluentWaitTimeoutInterval))
                .ignoring(NoSuchElementException.class);
    }
}
