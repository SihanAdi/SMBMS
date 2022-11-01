package com.adsh.servlet.user;

import com.adsh.pojo.Role;
import com.adsh.pojo.User;
import com.adsh.service.role.RoleService;
import com.adsh.service.role.RoleServiceImpl;
import com.adsh.service.user.UserService;
import com.adsh.service.user.UserServiceImpl;
import com.adsh.util.Constant;
import com.adsh.util.PageSupport;
import com.alibaba.fastjson.JSONArray;
import com.mysql.cj.util.StringUtils;
import com.mysql.cj.xdevapi.JsonArray;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;

public class UserServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getParameter("method");
        if (method.equals("savepwd") && method != null){
            this.updatePwd(req,resp);
        }else if (method.equals("pwdmodify") && method != null){
            this.pwdModify(req,resp);
        } else if (method.equals("query") && method != null) {
            this.query(req,resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
    //修改密码
    public void updatePwd(HttpServletRequest req, HttpServletResponse resp){
        //从session中拿id
        Object attribute = req.getSession().getAttribute(Constant.USER_SESSION);
        String newpassword = req.getParameter("newpassword");

        if (attribute != null && newpassword != null){
            UserService userService = new UserServiceImpl();
            if (userService.updatePwd(((User)attribute).getId(),newpassword)){
                req.setAttribute("message","修改成功，请退出，使用新密码登录");
                //密码修改成功，移除当前session
                req.getSession().removeAttribute(Constant.USER_SESSION);
            }else {
                req.setAttribute("message","修改失败");
            }
        }else {
            req.setAttribute("message","新密码存在问题");
        }
        try {
            req.getRequestDispatcher("/jsp/pwdmodify.jsp").forward(req,resp);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //验证旧密码
    public void pwdModify(HttpServletRequest req, HttpServletResponse resp){
        //从session中拿id
        Object attribute = req.getSession().getAttribute(Constant.USER_SESSION);
        String oldpassword = req.getParameter("oldpassword");
        HashMap<String,String> resultMap = new HashMap<>();
        //session失效了
        if (attribute == null){
            resultMap.put("result","sessionerror");
        }else if (StringUtils.isNullOrEmpty(oldpassword)){
            resultMap.put("result","error");
        }else {
            String userPassword = ((User) attribute).getUserPassword();
            if (oldpassword.equals(userPassword)){
                resultMap.put("result","true");
            }else {
                resultMap.put("result","false");
            }
        }

        try {
            resp.setContentType("application/json");
            PrintWriter writer = resp.getWriter();
            writer.write(JSONArray.toJSONString(resultMap));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void query(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        //查询用户列表
        //从前端获取数据
        String queryname = req.getParameter("queryname");
        String temp = req.getParameter("queryUserRole");
        String pageIndex = req.getParameter("pageIndex");
        int queryUserRole = 0;
        UserService userService = new UserServiceImpl();
        List<User> userList = null;
        //第一次走这个请求，一定是第一页，页面大小是固定的
        //设置页面容量
        int pageSize = 5;
        //默认当前页码
        int currentPageNo = 1;

        if (queryname == null){
            queryname = "";
        }
        if (temp != null && !temp.equals("")){
            queryUserRole = Integer.parseInt(temp);
        }
        if (pageIndex != null){
            try {
                currentPageNo = Integer.valueOf(pageIndex);
            }catch (Exception e){
                resp.sendRedirect("/smbms/error.jsp");
            }
        }
        //总数量（表）
        int totalCount = userService.getUserCount(queryname, queryUserRole);
        //总页数
        PageSupport pageSupport = new PageSupport();
        pageSupport.setCurrentPageNo(currentPageNo);
        pageSupport.setPageSize(pageSize);
        pageSupport.setTotalCount(totalCount);

        int totalPageCount = pageSupport.getTotalPageCount();
        //控制首页和尾页
        //如果页面小于第一页，就显示第一页
        if (currentPageNo < 1){
            currentPageNo = 1;
        } else if (currentPageNo > totalPageCount) {
            currentPageNo = totalPageCount;
        }
        //获取用户列表展示
        userList = userService.getUserList(queryname, queryUserRole, currentPageNo, pageSize);
        req.setAttribute("userList",userList);
        for (User user : userList) {
            System.out.println(user.getId());
            System.out.println(queryname);
            System.out.println(queryUserRole);
        }
        //获取角色列表
        List<Role> roleList = null;
        RoleService roleService = new RoleServiceImpl();
        roleList = roleService.getRoleList();
        req.setAttribute("roleList",roleList);
        req.setAttribute("queryUserName", queryname);
        req.setAttribute("queryUserRole", queryUserRole);
        req.setAttribute("totalPageCount", totalPageCount);
        req.setAttribute("totalCount", totalCount);
        req.setAttribute("currentPageNo", currentPageNo);

        try {
            req.getRequestDispatcher("/jsp/userlist.jsp").forward(req,resp);
        } catch (ServletException e) {
            throw new RuntimeException(e);
        }
    }
}
