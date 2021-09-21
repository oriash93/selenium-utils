package io.github.oriash93.selenium.configuration;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Configuration
public class SeleniumConfiguration {
    private static final String DRIVER_SYSTEM_PROPERTY = "webdriver.chrome.driver";
    private static final String DRIVER_PATH_PROPERTY = "CHROMEDRIVER_PATH";
    private static final String EXECUTABLE_PATH_PROPERTY = "GOOGLE_CHROME_BIN";

    @Value("${selenium.headless:true}")
    private boolean headless;

    @Value("${selenium.fluent-wait.polling-interval-ms:500}")
    private long fluentWaitPollingInterval;

    @Value("${selenium.fluent-wait.timeout-interval-ms:10000}")
    private long fluentWaitTimeoutInterval;

    @PostConstruct
    public void init() {
        String driverPath = System.getenv(DRIVER_PATH_PROPERTY);
        log.info("Driver path = {}", driverPath);
        System.setProperty(DRIVER_SYSTEM_PROPERTY, driverPath);
    }

    @Bean
    public ChromeOptions chromeOptions() {
        String executablePath = System.getenv(EXECUTABLE_PATH_PROPERTY);
        log.info("Executable path = {}", executablePath);

        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.setHeadless(headless);
        chromeOptions.setBinary(executablePath);
        chromeOptions.addArguments("--disable-gpu", "--no-sandbox");
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
