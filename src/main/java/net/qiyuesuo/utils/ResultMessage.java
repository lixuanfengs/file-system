package net.qiyuesuo.utils;

import java.util.HashMap;

public class ResultMessage extends HashMap<String, Object> {
    private static final long serialVersionUID = 1;
    public static final ResultMessage SUCCESS = new ResultMessage(0, "SUCCESS", true);
    public static final ResultMessage ERROR = new ResultMessage(10000, "ERROR", false);

    public ResultMessage(Integer code, String message, boolean success) {
        if (code != null) {
            put("code", code);
        }
        if (message != null) {
            put("message", message);
        }
        put("success", Boolean.valueOf(success));
    }

    public static ResultMessage newSuccessMessage(String dataName, Object data) {
        ResultMessage resultMessage = new ResultMessage(0, "SUCCESS", true);
        if (dataName != null && data != null) {
            resultMessage.put(dataName, data);
        }
        return resultMessage;
    }

    public static ResultMessage newSuccessMessage(Object data) {
        return newSuccessMessage("result", data);
    }

    public static ResultMessage newErrorMessage(String message) {
        return new ResultMessage(10000, message, false);
    }
}
