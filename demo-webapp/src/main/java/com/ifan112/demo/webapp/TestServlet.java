package com.ifan112.demo.webapp;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(urlPatterns = "/test")
public class TestServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("receive");

        System.out.println("remoteAddr: " + req.getRemoteAddr());
        System.out.println("remoteHost: " + req.getRemoteHost());
        System.out.println("remotePort: " + req.getRemotePort());

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getOutputStream().write("test".getBytes());
    }
}
