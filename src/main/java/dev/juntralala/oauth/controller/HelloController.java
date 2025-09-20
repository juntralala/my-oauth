package dev.juntralala.oauth.controller;

import dev.juntralala.oauth.dto.UserPrincipal;
import dev.juntralala.oauth.entity.Permission;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @RequestMapping(path = "/")
    @ResponseBody
    public String sapa1() {
        return Thread.currentThread().getName() + " : " + Thread.currentThread().isVirtual();
    }

    @RequestMapping(path = "/user/current")
    @ResponseBody
    public Map<String, Object> currentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return Map.of(
                "username", principal.getUser().getUsername(),
                "nickname", principal.getUser().getNickname(),
                "role", principal.getUser().getRole().getName(),
                "permissions", principal.getUser().getPermissions().stream().map(Permission::getName).collect(Collectors.joining())
        );
    }

    @RequestMapping(path = "/session/current")
    @ResponseBody
    public Object currentSession(HttpSession session) {
        Enumeration<String> names = session.getAttributeNames();
        Map<String, String> map = new LinkedHashMap<>();

        map.put("sessionId", session.getId());

        while (names.hasMoreElements()) {
            String name = names.nextElement();
            map.put(name, session.getAttribute(name).toString());
        }
        return map;
    }

    @RequestMapping(path = "/session/set")
    @ResponseBody
    public Object setSession(@RequestParam(name = "key") String key,
                             @RequestParam(name = "value") String value,
                             HttpSession session) {
        session.setAttribute(key, value);
        return "ok: %s %s".formatted(key, value);
    }

    @RequestMapping(path = "/request")
    @ResponseBody
    public String requestType(HttpServletRequest request) {
        return request.getClass().getTypeName();
    }

    @RequestMapping(path = "/request/inspect")
    @ResponseBody
    public Object requestInspect(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String, String> headers = new LinkedHashMap<>();
        while(headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headers.put(headerName, request.getHeader(headerName));
        }
        return headers;
    }
}
