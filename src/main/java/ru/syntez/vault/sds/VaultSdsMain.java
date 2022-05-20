package ru.syntez.vault.sds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import ru.syntez.vault.sds.config.VaultConfig;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main class for console running
 *
 * @author Skyhunter
 * @date 20.05.2022
 */
@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties(VaultConfig.class)
public class VaultSdsMain {

    public static void main(String[] args) {
        SpringApplication.run(VaultSdsMain.class, args);
    }

}