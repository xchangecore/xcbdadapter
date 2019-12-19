package com.spotonresponse.adapter.services.unpw;

import com.fasterxml.jackson.annotation.JsonValue;

import java.util.HashMap;
import java.util.Map;

public class ValidationErrors {
    @JsonValue
    private final Map<String, String> errors = new HashMap<>();

    public void addError(String errorKey, String errorMessage){
        this.errors.put(errorKey, errorMessage);
    }

    public boolean hasErrors(){
        return !errors.isEmpty();
    }
}
