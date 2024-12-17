package edu.zhekadoe.currencyexchange.utils;

import edu.zhekadoe.currencyexchange.exception.ApiBadRequestException;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ApiServletPath {
    public static final String INVALID_PATH_MESSAGE = "Invalid path format.";
    public static final int BEGIN_BASE_INDEX = 1;
    public static final int BEGIN_TARGET_INDEX = 4;
    public static final String PATH_REGEX = "/[A-Za-z]{%s}";

    public void validatePath(String path, int size) {
        if (path == null || path.isEmpty() || !path.matches(PATH_REGEX.formatted(size))) {
            throw new ApiBadRequestException(INVALID_PATH_MESSAGE + " Expected format: /" + "X".repeat(size));
        }
    }
}
