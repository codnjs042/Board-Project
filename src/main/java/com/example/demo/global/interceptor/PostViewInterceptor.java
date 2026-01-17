package com.example.demo.global.interceptor;

import com.example.demo.domain.post.service.PostService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PostViewInterceptor implements HandlerInterceptor {
    private final PostService postService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        Map<String, String> pathVariables = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Long postId = Long.parseLong(pathVariables.get("postId"));

        if(isFirstView(postId, request)){
            postService.incrementView(postId);
            bakeCookie(postId, request, response);
        }

        return true;
    }

    private boolean isFirstView(Long postId, HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie c : cookies){
                if(c.getName().equals("postView")
                        && c.getValue().contains("["+postId+"]"))
                    return false;
            }
        }
        return true;
    }

    private void bakeCookie(Long postId, HttpServletRequest request, HttpServletResponse response){
        String cookieName = "postView";
        String newValue = "["+postId+"]";
        Cookie[] cookies = request.getCookies();
        Cookie targetCookie = null;

        if(cookies!=null){
            for(Cookie c : cookies){
                if(c.getName().equals(cookieName)){
                    targetCookie = c;
                    targetCookie.setValue(c.getValue()+"_["+postId+"]");
                    break;
                }
            }
        }
        if(targetCookie==null){
            targetCookie = new Cookie(cookieName, "["+postId+"]");
        }
        targetCookie.setPath("/");
        targetCookie.setMaxAge(60*60*24);
        response.addCookie(targetCookie);
    }
}
