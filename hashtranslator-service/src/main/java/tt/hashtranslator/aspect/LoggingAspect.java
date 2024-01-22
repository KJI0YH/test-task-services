package tt.hashtranslator.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@Slf4j
public class LoggingAspect {
    @Pointcut(value = "@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void loggingPathsAspectPointcut() {

    }

    @Before("loggingPathsAspectPointcut()")
    public void logBefore(JoinPoint joinPoint) {
        logRequestDetails();
    }

    private void logRequestDetails() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
            log.info("Request {} {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI());
        }
    }
}

