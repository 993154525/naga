package com.st.naga.server;

import cn.hutool.http.HttpResponse;
import com.st.naga.core.exception.ErrorCodes;
import com.st.naga.core.exception.NagaException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ShaoTian
 * @date 2020/11/11 15:22
 */
@ControllerAdvice
public class BaseController {

    protected SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseBody
    public Map<String, Object> exceptionHandler(Exception ex, HttpResponse response) {
        if (NagaException.class.isAssignableFrom(ex.getClass())) {
            NagaException nagaException = (NagaException) ex;
            return getResultMap(nagaException.getErrorCodes(), nagaException.getErrorMessages(), null);
        } else {
            return getErrorMsg(ErrorCodes.SYSTEM_EXCEPTION, ex.getMessage());
        }
    }

    public Map<String, Object> getErrorMsg(Integer errorCode, String errorMsg) {
        return getResultMap(errorCode, null, null);
    }

    protected Map<String, Object> getResult(Object o) {
        return getResultMap(null, o, null);
    }

    private Map<String, Object> getResultMap(Integer errorCode, Object data, Map<String, Object> map) {
        String currentTime = sdf.format(new Date());
        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("currentTime", currentTime);
        resultMap.put("data", data);

        if (errorCode == null || errorCode == ErrorCodes.SYSTEM_SUCCESS) {
            resultMap.put("code", ErrorCodes.SYSTEM_SUCCESS);
        } else {
            resultMap.put("code", errorCode);
        }

        if (map != null && map.isEmpty()) {
            resultMap.putAll(map);
        }

        return resultMap;
    }

}
