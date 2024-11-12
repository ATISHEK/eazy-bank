package com.eazybytes.accounts.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.HashMap;

@ConfigurationProperties("accounts")
@Data
public class AccountsConfig {

    private String message;

    private HashMap<String, String> contact ;


}
