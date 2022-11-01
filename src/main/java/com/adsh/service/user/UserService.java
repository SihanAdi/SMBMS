package com.adsh.service.user;

import com.adsh.pojo.Role;
import com.adsh.pojo.User;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public interface UserService {
    //用户登录
    public User login(String userCode, String password);
    //修改当前用户密码
    public boolean updatePwd(int id, String password);
    //查询记录数
    public int getUserCount(String username, int userRole);
    //根据条件查询用户列表
    public List<User> getUserList(String queryUserName, int queryUserRole, int currentPageNo, int pageSize);


}
