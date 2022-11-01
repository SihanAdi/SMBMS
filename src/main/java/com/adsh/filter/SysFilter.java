package com.adsh.filter;

import com.adsh.util.Constant;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SysFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        if (req.getSession().getAttribute(Constant.USER_SESSION) == null){
            resp.sendRedirect("/smbms/error.jsp");
        }else {
            chain.doFilter(request,response);
        }
    }

    @Override
    public void destroy() {
    }
}
