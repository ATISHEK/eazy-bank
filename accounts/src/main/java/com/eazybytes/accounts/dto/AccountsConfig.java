package com.eazybytes.accounts.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@ConfigurationProperties(prefix = "accounts")
@Data
@Component
public class AccountsConfig {

    private String message;

    private HashMap<String, String> contact ;


}
