package com.eazybytes.loans.dto;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@ConfigurationProperties(prefix = "loans")
@Data
@Component
public class LoansConfig {


    private String message;

    private HashMap<String, String> contact;

}
