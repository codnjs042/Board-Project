package com.example.demo.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.io.IOException;
import java.net.URI;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {
    //임시
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class, MethodArgumentNotValidException.class})
    public String handleIllegalArgument(HttpServletRequest request,
                                        RedirectAttributes redirectAttrs,
                                        Exception e){
        String referer = request.getHeader("Referer");
        String preUri = "/";
        if(referer!=null) preUri = URI.create(referer).getPath();
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        String errorType = e instanceof IllegalArgumentException?"Illegal Argument":"Illegal State";
        log.warn("{}: [{}] {} 요청 by {}", errorType, request.getMethod(), request.getRequestURI(), request.getUserPrincipal(), e);
        return "redirect:" + preUri;
    }

    @ExceptionHandler(ForceLogoutException.class)
    public String handleForceLogout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttrs,
                                    ForceLogoutException e){
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        log.warn("Force Logout: [{}] {} 요청 by {}", request.getMethod(), request.getRequestURI(), request.getUserPrincipal(), e);
        return "redirect:/user/login";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public void handleNoResourceFound(HttpServletRequest request,
                                 HttpServletResponse response,
                                 RedirectAttributes redirectAttrs,
                                 NoResourceFoundException e) throws IOException {
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        log.error("404 Not Found: [{}] {} 요청 by {}", request.getMethod(), request.getRequestURI(), request.getUserPrincipal(), e);
        response.sendError(HttpServletResponse.SC_NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public void handleException(HttpServletRequest request,
                                  HttpServletResponse response,
                                  RedirectAttributes redirectAttrs,
                                  Exception e) throws IOException {
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        log.error("500 Internal Server Error: [{}] {} 요청 by {}", request.getMethod(), request.getRequestURI(), request.getUserPrincipal(), e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}