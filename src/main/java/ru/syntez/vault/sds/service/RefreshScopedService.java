package ru.syntez.vault.sds.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@RefreshScope
public class RefreshScopedService {

    @Value("${tps}")
    Integer tps;

    String responseMessage;

    @PostConstruct
    public void refresh(){
        responseMessage = responseMessage = "Service is running with tps: " + tps;
    }

}
