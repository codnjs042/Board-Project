package com.example.demo.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import javax.security.sasl.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ForceLogoutException.class)
    public String handleForceLogout(HttpServletRequest request,
                                    HttpServletResponse response,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttrs,
                                    Exception e){
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        return "redirect:/user/login";
    }

    @ExceptionHandler(value = Exception.class, produces = "text/html")
    public String handleException(HttpServletRequest request, RedirectAttributes redirectAttrs, Exception e) {
        String uri = request.getRequestURI();
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        return "redirect:" + uri;
}

//    private ResponseEntity<ErrorMessage> buildErrorResponse(ErrorCode errorCode, String message){
//        ErrorMessage error = ErrorMessage.builder()
//                .status(errorCode.getStatus())
//                .error(errorCode.getError())
//                .message(message)
//                .build();
//        return ResponseEntity.status(errorCode.getStatus()).body(error);
//    }
//    @ExceptionHandler(value = IllegalArgumentException.class, produces = "application/json")
//    public ResponseEntity<ErrorMessage> handleIllegalArgument(IllegalArgumentException e) {
//        return buildErrorResponse(ErrorCode.BAD_REQUEST, e.getMessage());
//    }
//    @ExceptionHandler(AuthenticationException.class)
//    public ResponseEntity<ErrorMessage> handleAuthentication(AuthenticationException e) {
//        return buildErrorResponse(ErrorCode.UNAUTHORIZED, e.getMessage());
//    }
//    @ExceptionHandler(AccessDeniedException.class)
//    public ResponseEntity<ErrorMessage> handleAccessDenied(AccessDeniedException e) {
//        return buildErrorResponse(ErrorCode.FORBIDDEN, e.getMessage());
//    }
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public ResponseEntity<ErrorMessage> handleResourceNotFound(ResourceNotFoundException e){
//        return buildErrorResponse(ErrorCode.NOT_FOUND, e.getMessage());
//    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorMessage> handleOthers(Exception e) {
//        return buildErrorResponse(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
//    }
}