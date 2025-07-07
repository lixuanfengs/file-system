package net.cactus.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

@Component
public class ExceptionResolver implements HandlerExceptionResolver {
    private Logger logger = LoggerFactory.getLogger((Class<?>) ExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest req, HttpServletResponse res, Object object, Exception e) {
        this.logger.error("{} Exception", req.getRequestURI(), e);
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView());
        modelAndView.addObject("msg", "服务器出错了，请稍后再试！");
        return modelAndView;
    }
}
