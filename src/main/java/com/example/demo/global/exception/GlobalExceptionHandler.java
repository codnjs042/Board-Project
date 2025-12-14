package com.example.demo.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.security.sasl.AuthenticationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = IllegalArgumentException.class, produces = "text/html")
    public String handleIllegalArgumentView(IllegalArgumentException e, HttpServletRequest request, RedirectAttributes redirectAttrs) {
        String uri = request.getRequestURI();
        redirectAttrs.addFlashAttribute("error", e.getMessage());
        return "redirect:"+uri;
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