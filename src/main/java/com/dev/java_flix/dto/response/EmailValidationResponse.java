package com.dev.java_flix.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EmailValidationResponse {

    private boolean exists;
    private boolean available;
}
