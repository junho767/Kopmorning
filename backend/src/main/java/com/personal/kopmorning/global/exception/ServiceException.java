package com.personal.kopmorning.global.exception;

import com.personal.kopmorning.global.entity.RsData;
import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final RsData<?> rsData;

    public ServiceException(String code, String message) {
        this.rsData = new RsData<>(code, message);
    }

}
