package com.adsh.dao.user;

import com.adsh.dao.BaseDao;
import com.adsh.pojo.Role;
import com.adsh.pojo.User;
import com.mysql.cj.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao{
//    得到要登陆的用户
    @Override
    public User getLoginUser(Connection connection, String userCode) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        User user = null;
        if (connection != null){
            String sql = "SELECT * FROM smbms_user WHERE userCode=?";
            Object[] params = {userCode};
            resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql, params);
            if (resultSet.next()){
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUserCode(resultSet.getString("userCode"));
                user.setUserName(resultSet.getString("userName"));
                user.setUserPassword(resultSet.getString("userPassword"));
                user.setGender(resultSet.getInt("gender"));
                user.setBirthday(resultSet.getDate("birthday"));
                user.setPhone(resultSet.getString("phone"));
                user.setAddress(resultSet.getString("address"));
                user.setUserRole(resultSet.getInt("userRole"));
                user.setCreatedBy(resultSet.getInt("createdBy"));
                user.setCreationDate(resultSet.getTimestamp("creationDate"));
                user.setModifyBy(resultSet.getInt("modifyBy"));
                user.setModifyDate(resultSet.getTimestamp("modifyDate"));
            }
            BaseDao.closeResource(null,preparedStatement,resultSet);
        }
        return user;
    }
//    修改当前用户密码
    @Override
    public int updatePwd(Connection connection, int id, String password) throws SQLException {
        PreparedStatement preparedStatement = null;
        int ans = 0;
        if (connection != null){
            String sql = "Update smbms_user SET userPassword= ? WHERE id = ?";
            Object[] params = {password, id};
            ans = BaseDao.execute(connection,preparedStatement,sql,params);
            BaseDao.closeResource(null,preparedStatement,null);
        }
        return ans;
    }

    @Override
    public int getUserCount(Connection connection, String username, int userRole) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int count = 0;

        if (connection != null){
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT COUNT(1) AS count FROM smbms_user u, smbms_role r WHERE r.id = userRole");
            ArrayList<Object> arrayList = new ArrayList<>();
            if (!StringUtils.isNullOrEmpty(username)){
                sql.append(" AND u.userName LIKE ?");
                arrayList.add(username);
            }
            if (userRole > 0){
                sql.append(" AND u.userRole = ?");
                arrayList.add(userRole);
            }
            Object[] params = arrayList.toArray();
            System.out.println("UserDaoImpl SQL:" + sql.toString());
            resultSet = BaseDao.execute(connection, preparedStatement, resultSet, sql.toString(), params);
            if (resultSet.next()){
                count = resultSet.getInt("count");
            }
            BaseDao.closeResource(null,preparedStatement,resultSet);
        }
        return count;
    }

    @Override
    public List<User> getUserList(Connection connection, String username, int userRole, int currentPageNo, int pageSize) throws SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<User> ans = new ArrayList<>();
        if (connection != null){
            StringBuffer sql = new StringBuffer();
            ArrayList<Object> arrayList = new ArrayList<>();
            sql.append("select u.*,r.roleName as userRoleName from smbms_user u,smbms_role r where u.userRole = r.id");

            if (!StringUtils.isNullOrEmpty(username)){
                sql.append(" AND u.userName LIKE ?");
                arrayList.add(username);
            }
            if (userRole > 0){
                sql.append(" AND u.userRole = ?");
                arrayList.add(userRole);
            }
            //在mysql数据库中，分页使用 limit startIndex，pageSize ; 总数
            sql.append(" order by creationDate DESC limit ?,?");
            currentPageNo = (currentPageNo - 1) * pageSize;
            arrayList.add(currentPageNo);
            arrayList.add(pageSize);

            Object[] params = arrayList.toArray();
            System.out.println("getUserList SQL: " + sql.toString());
            resultSet  = BaseDao.execute(connection, preparedStatement, resultSet, sql.toString(), params);
            while (resultSet.next()){
                User _user = new User();
                _user.setId(resultSet.getInt("id"));
                _user.setUserCode(resultSet.getString("userCode"));
                _user.setUserName(resultSet.getString("userName"));
                _user.setGender(resultSet.getInt("gender"));
                _user.setBirthday(resultSet.getDate("birthday"));
                _user.setPhone(resultSet.getString("phone"));
                _user.setUserRole(resultSet.getInt("userRole"));
                _user.setUserRoleName(resultSet.getString("userRoleName"));
                ans.add(_user);
            }

            BaseDao.closeResource(null,preparedStatement,resultSet);
        }
        return ans;
    }


}
