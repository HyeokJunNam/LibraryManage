package com.nhj.librarymanage.util;

import com.nhj.librarymanage.error.code.CommonErrorCode;
import com.nhj.librarymanage.error.exception.IdParseFailureException;
import org.springframework.util.StringUtils;

public class NumberParser {

    public static Long parseLong(String value) {
        if (StringUtils.hasText(value)) {
            try {
                return Long.parseLong(value.trim());
            } catch (NumberFormatException e) {
                throw new IdParseFailureException(CommonErrorCode.ID_PARSE_FAILURE);
            }
        }
        else {
            throw new IdParseFailureException(CommonErrorCode.ID_PARSE_FAILURE);
        }
    }

}
