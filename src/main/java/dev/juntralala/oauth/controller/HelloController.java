package dev.juntralala.oauth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Slf4j
@Controller
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello";
    }

    @RequestMapping(path = "/sapa")
    @ResponseBody
    public String sapa() {
        return "Hai";
    }

    @RequestMapping
    @ResponseBody
    public String sapa1() {
        return Thread.currentThread().getName() + " : "+ Thread.currentThread().isVirtual();

    }

    @RequestMapping(path = "/user/current")
    @ResponseBody
    public Object currentUser() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @RequestMapping(path = "/session/current")
    @ResponseBody
    public Object currentSession(HttpSession session) {
        return session.getId();
    }

    @RequestMapping(path = "/request")
    @ResponseBody
    public String requestType(HttpServletRequest request) {
        return request.getClass().getTypeName();
    }
}
