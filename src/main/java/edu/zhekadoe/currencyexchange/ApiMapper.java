package edu.zhekadoe.currencyexchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.zhekadoe.currencyexchange.exception.ApiException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ApiMapper {
    private static final ApiMapper INSTANCE = new ApiMapper();
    private static final String MAPPER_ERROR_MESSAGE = "Unknown error. Please try again later.";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public static ApiMapper getInstance() {
        return INSTANCE;
    }

    public String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ApiException(MAPPER_ERROR_MESSAGE);
        }
    }
}
