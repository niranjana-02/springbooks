package org.example.springbooks.exception;

import lombok.*;

import java.util.Map;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
public class ApiError {
    @NonNull
    private int status;
    @NonNull
    private String message;
    private Map<String, String> errors;
}
