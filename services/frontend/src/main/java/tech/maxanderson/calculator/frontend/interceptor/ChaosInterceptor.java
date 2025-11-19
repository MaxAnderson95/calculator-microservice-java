package tech.maxanderson.calculator.frontend.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.maxanderson.calculator.frontend.config.ChaosConfiguration;
import tech.maxanderson.calculator.frontend.exception.ChaosException;

import java.util.Random;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChaosInterceptor implements HandlerInterceptor {

    private final ChaosConfiguration chaosConfiguration;
    private final Random random = new Random();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!chaosConfiguration.isChaosEnabled()) {
            return true;
        }

        double randomValue = random.nextDouble();
        if (randomValue < chaosConfiguration.getFailureRate()) {
            log.warn("Chaos engineering triggered! Failing request to {} (random: {}, threshold: {})",
                    request.getRequestURI(), randomValue, chaosConfiguration.getFailureRate());
            throw new ChaosException("Service temporarily unavailable due to chaos engineering");
        }

        return true;
    }
}
