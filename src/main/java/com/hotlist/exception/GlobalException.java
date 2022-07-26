package com.hotlist.exception;

import com.hotlist.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class GlobalException {

    @ExceptionHandler(ClientAbortException.class)
    public Object clientAbortException(ClientAbortException ex) {
        // 捕获这个异常 防打日志
        // 服务器资源还没发完 用户关闭浏览器就会抛这个异常
        return null;
    }

    @ExceptionHandler({Exception.class})
    public R handleException(Exception e) {
//        e.printStackTrace();
        return R.error();
    }


}
