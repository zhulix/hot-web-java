package com.hotlist.exception;

import com.hotlist.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler({Exception.class})
    public R handleException(Exception e) {
        return R.error(e.getMessage());
    }


}
