package ru.syntez.vault.sds;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Main class for console running
 *
 * @author Skyhunter
 * @date 26.05.2021
 */
@SpringBootApplication
@EnableSwagger2
public class VaultSdsMain {

    public static void main(String[] args) {
        SpringApplication.run(VaultSdsMain.class, args);
    }

}