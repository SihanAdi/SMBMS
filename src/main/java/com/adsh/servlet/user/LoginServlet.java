package com.adsh.servlet.user;

import com.adsh.pojo.User;
import com.adsh.service.user.UserService;
import com.adsh.service.user.UserServiceImpl;
import com.adsh.util.Constant;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class LoginServlet extends HttpServlet {
    //servlet  ： 控制层调业务层
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        获取用户名和密码
        System.out.println("LoginServlet--start...");
        String userCode = req.getParameter("userCode");
        String userPassword = req.getParameter("userPassword");
        //调用service方法，进行用户匹配
        UserService userService = new UserServiceImpl();
        User user = userService.login(userCode, userPassword);
        if (user != null){
            //放入session
            req.getSession().setAttribute(Constant.USER_SESSION,user);
            //页面重定向（frame.jsp）
            resp.sendRedirect("/smbms/jsp/frame.jsp");
        }else {
            //页面转发（login.jsp）带出提示信息--转发
            req.setAttribute("error", "用户名或密码不正确");
            req.getRequestDispatcher("login.jsp").forward(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
