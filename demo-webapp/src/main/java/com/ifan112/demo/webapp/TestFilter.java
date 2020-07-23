package com.ifan112.demo.webapp;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.Enumeration;

@WebFilter(urlPatterns = "/*")
public class TestFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {
        System.out.println("----- TestFilter1 ----- ");

        System.out.println("filterName: " + filterConfig.getFilterName());

        Enumeration<String> initParameterNames = filterConfig.getInitParameterNames();
        while (initParameterNames.hasMoreElements()) {
            System.out.println(initParameterNames.nextElement());
        }

        filterConfig.getServletContext().getFilterRegistrations().keySet().forEach(System.out::println);

        System.out.println("----- TestFilter1 ----- ");
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("1");

        chain.doFilter(request, response);

        System.out.println("2");
    }

    public void destroy() {
        System.out.println("destroy");
    }
}
