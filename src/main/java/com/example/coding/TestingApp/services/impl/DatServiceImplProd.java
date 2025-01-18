package com.example.coding.TestingApp.services.impl;

import com.example.coding.TestingApp.services.DataService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("prod")
public class DatServiceImplProd implements DataService {
    @Override
    public String getData() {
        return "Prod Data";
    }
}
