package com.epam.donetc.restaurant.database;

import com.epam.donetc.restaurant.database.entity.Dish;
import com.epam.donetc.restaurant.exception.DBException;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class CartDAO {

    public static Map<Dish, Integer> getCart(int id) throws DBException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement ps = connection.prepareStatement(DBManager.GET_CART_BY_USER_ID)) {
            Map<Dish, Integer> cart = new HashMap<>();
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Dish dish = DishDAO.getDishByID(rs.getInt(2));
                    cart.put(dish, rs.getInt(3));
                }
                return cart;
            }
        } catch (SQLException e) {
            throw new DBException("Cannot get a cart", e);
        }
    }

    public static void addDishToCart(int userId, int dishId, int amount) throws DBException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement ps = connection.prepareStatement(DBManager.PUT_DISH_INTO_CART)) {
            int k = 0;
            ps.setInt(++k, userId);
            ps.setInt(++k, dishId);
            ps.setInt(++k, amount);
            if (ps.executeUpdate() == 0) {
                throw new DBException("Inserting Failed");
            }
        } catch (SQLException e) {
            throw new DBException("Cannot add dish to cart", e);
        }
    }

    public static void changeAmountOfDish(int userId, int dishId, int amount) throws DBException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement ps = connection.prepareStatement(DBManager.UPDATE_DISH_AMOUNT_IN_CART)) {
            int k = 0;
            ps.setInt(++k, amount);
            ps.setInt(++k, userId);
            ps.setInt(++k, dishId);
            if (ps.executeUpdate() == 0) {
                throw new DBException("Inserting failed");
            }
        } catch (SQLException e) {
            throw new DBException("Cannot update dish amount in cart", e);
        }
    }

    public static void deleteDishFromCart(int userId, int dishId) throws DBException {
        try (Connection connection = ConnectionManager.get();
             PreparedStatement ps = connection.prepareStatement(DBManager.DELETE_DISH_FROM_CART)) {
            int k = 0;
            ps.setInt(++k, userId);
            ps.setInt(++k, dishId);
            if (ps.executeUpdate() == 0) {
                throw new DBException("Deleting failed");
            }
        } catch (SQLException e) {
            throw new DBException("Cannot delete dish amount in cart", e);
        }
    }

    public static void submitOrder(int userId, Map<Dish, Integer> cart) throws DBException {
        Connection con = null;
        try {
            con = ConnectionManager.get();
            con.setAutoCommit(false);
            int receiptId = createNewReceipt(con, userId);
            for (Dish d : cart.keySet()) {
                putDishIntoReceipt(con, receiptId, d, cart.get(d));
            }
        } catch (SQLException ex) {
            if (con != null) ConnectionManager.rollback(con);
            throw new DBException("Cannot submit order", ex);
        } finally {
            ConnectionManager.close(con);
        }
    }

    private static void putDishIntoReceipt(Connection connection, int receiptId, Dish dish, int amount) throws  SQLException {
        try(PreparedStatement ps = connection.prepareStatement(DBManager.PUT_DISH_INTO_RECEIPT)) {
            int k = 0;
            ps.setInt(++k, receiptId);
            ps.setInt(++k, dish.getId());
            ps.setInt(++k, amount);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Putting dish into receipt failed");
            }
            connection.commit();
        }

    }


    private static int createNewReceipt(Connection connection, int userId)  throws SQLException{
        try(PreparedStatement ps = connection.prepareStatement(DBManager.CREATE_NEW_RECEIPT_BY_USER_ID, Statement.RETURN_GENERATED_KEYS)){
            ps.setInt(1, userId);
            if (ps.executeUpdate() == 0){
                throw new SQLException("Cannot create new receipt");
            }
            try(ResultSet rs = ps.getGeneratedKeys()){
                if (rs.next()){
                    connection.commit();
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public static void cleanCart(int id) throws DBException {
        try (Connection con = ConnectionManager.get();
             PreparedStatement ps = con.prepareStatement(DBManager.CLEAR_CART)) {
            ps.setInt(1, id);
            if (ps.executeUpdate() == 0) {
                throw new SQLException("Cleaning cart error");
            }
        } catch (SQLException ex) {
            throw new DBException("Cannot clean cart", ex);
        }
    }
}