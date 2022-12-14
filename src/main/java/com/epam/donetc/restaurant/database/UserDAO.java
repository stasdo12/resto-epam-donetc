package com.epam.donetc.restaurant.database;

import com.epam.donetc.restaurant.database.entity.User;
import com.epam.donetc.restaurant.exception.DBException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {



    public static User getUserByLogin(String login) throws DBException {
        try(Connection con = ConnectionManager.get();
            PreparedStatement ps = con.prepareStatement(DBManager.GET_USER_BY_LOGIN)){
            ps.setString(1, login);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    return createUser(rs);
                } else{
                    return null;
                }
            }
        }catch (SQLException ex){
            throw new DBException("Cannot find user by login", ex);
        }
    }

    public static User getUserById(int id){
        try(Connection connection = ConnectionManager.get();
            PreparedStatement ps = connection.prepareStatement(DBManager.GET_USER_BY_ID)) {
            ps.setInt(1,id);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createUser(rs);
                }else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private static User createUser(ResultSet rs) throws SQLException{
        return new User(rs.getInt(1), rs.getString(2),rs.getString(3), rs.getInt(4));
    }

    public static User signUp(String login, String password){
        try(Connection connection = ConnectionManager.get();
            PreparedStatement ps = connection.prepareStatement(DBManager.SIGN_UP)) {
            int k = 0;
            ps.setString(++k, login);
            ps.setString(++k, password);
            if (ps.executeUpdate() == 0){
                throw new DBException("Sign Up Failed");
            }
            return getUserByLogin(login);
        } catch (SQLException | DBException e) {
            throw new RuntimeException(e);
        }
    }

    public static User logIn(String login, String password) throws DBException {
        try(Connection connection = ConnectionManager.get();
            PreparedStatement ps = connection.prepareStatement(DBManager.LOG_IN)){
            int k = 0;
            ps.setString(++k, login);
            ps.setString(++k, password);
            try(ResultSet rs = ps.executeQuery()){
                if (rs.next()){
                    return createUser(rs);
                }else {
                    return null;
                }
            }
        } catch (SQLException e) {
            throw new DBException("Login Error" + e);
        }
    }


}
