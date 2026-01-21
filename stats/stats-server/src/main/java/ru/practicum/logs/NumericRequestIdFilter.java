package ru.practicum.logs;

import jakarta.servlet.*;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class NumericRequestIdFilter implements Filter {

    private final AtomicLong requestCounter = new AtomicLong(0);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            long requestId = requestCounter.incrementAndGet();
            MDC.put("requestId", String.valueOf(requestId));
            chain.doFilter(request, response);
        } finally {
            MDC.remove("requestId");
        }
    }
}
