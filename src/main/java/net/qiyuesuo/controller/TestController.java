package net.qiyuesuo.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.qiyuesuo.utils.ResultMessage;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping({"/health"})
    public ResultMessage health() {
        return ResultMessage.newSuccessMessage("FileServer is running!");
    }

    @RequestMapping({"/error"})
    public ResultMessage sendError(@RequestParam String name, HttpServletRequest req, HttpServletResponse res) throws Exception {
        if (name.equals("error")) {
            throw new Exception("这是Controller中抛出的异常");
        }
        return ResultMessage.newSuccessMessage(name);
    }
}
