package com.adsh.service.role;

import com.adsh.dao.BaseDao;
import com.adsh.dao.role.RoleDao;
import com.adsh.dao.role.RoleDaoImpl;
import com.adsh.pojo.Role;
import org.junit.Test;

import java.sql.Connection;
import java.util.List;

public class RoleServiceImpl implements RoleService{
    private RoleDao roleDao;

    public RoleServiceImpl() {
        roleDao = new RoleDaoImpl();
    }

    @Override
    public List<Role> getRoleList() {
        Connection connection = null;
        List<Role> roleList = null;
        try {
            connection = BaseDao.getConnection();
            roleList = roleDao.getRoleList(connection);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            BaseDao.closeResource(connection, null, null);
        }
        return roleList;
    }
    @Test
    public void test(){
        List<Role> roleList = new RoleServiceImpl().getRoleList();
        for (Role role : roleList) {
            System.out.println(role.getRoleName());
        }
    }
}
