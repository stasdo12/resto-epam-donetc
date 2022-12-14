package com.epam.donetc.restaurant.database;

import com.epam.donetc.restaurant.database.entity.Dish;
import com.epam.donetc.restaurant.database.entity.Receipt;
import com.epam.donetc.restaurant.database.entity.Status;
import com.epam.donetc.restaurant.exception.DBException;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReceiptDAO {

    public static List<Receipt> getAllReceipt() throws DBException {
        List<Receipt> receipts = new ArrayList<>();
        try(Connection connection = ConnectionManager.get()) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(DBManager.GET_ALL_RECEIPT);
            while (rs.next()){
                Receipt receipt = createReceipt(rs);
                receipts.add(receipt);
            }
        } catch (SQLException e) {
            throw new DBException("Cannot get all receipt", e);
        }
        return receipts;
    }

    private static Receipt createReceipt(ResultSet rs) throws DBException {
        Receipt receipt;
        try {
            receipt = new Receipt(rs.getInt(1),
                    UserDAO.getUserById(rs.getInt(2)),
                    Status.getStatusById(rs.getInt(3)));
            receipt.setDishes(getDishesByReceiptId(receipt.getId()));
            receipt.countTotal();
            System.out.println(receipt);
        } catch (SQLException ex) {
            throw new DBException("Cannot create receipt", ex);
        }
        return receipt;
    }

    private static Map<Dish, Integer> getDishesByReceiptId(int receiptId) throws DBException {
        Map<Dish, Integer> dishes = new HashMap<>();
        try (Connection con = ConnectionManager.get();
             PreparedStatement ps = con.prepareStatement(DBManager.GET_DISHES_BY_RECEIPT_ID)) {
            ps.setInt(1, receiptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    dishes.put(DishDAO.getDishByID(rs.getInt(2)), rs.getInt(3));
                }
            } catch (SQLException ex) {
                throw new DBException("Cannot put dishes into map", ex);
            }
        } catch (SQLException ex) {
            throw new DBException("Cannot find dishes by receipt id", ex);
        }
        return dishes;
    }
    public static void changeStatus(int receiptId, Status status)throws DBException{
        try(Connection connection = ConnectionManager.get();
            PreparedStatement ps = connection.prepareStatement(DBManager.CHANGE_RECEIPT_STATUS)) {
            ps.setInt(1, status.getId());
            ps.setInt(2, receiptId);
            if (ps.executeUpdate() == 0){
                throw new SQLException("Change status failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Receipt> getReceiptByUserId(int userId) throws DBException {
        List<Receipt> receipts = new ArrayList<>();
        try(Connection connection = ConnectionManager.get();
            PreparedStatement ps = connection.prepareStatement(DBManager.GET_RECEIPT_BY_USER_ID)) {
            ps.setInt(1, userId);
            try(ResultSet rs = ps.executeQuery()){
                while (rs.next()){
                    Receipt receipt = createReceipt(rs);
                    receipts.add(receipt);
                }
            }
        } catch (SQLException e) {
            throw new DBException("Cannot find receipts by User id", e);
        }
        return receipts;
    }

    public static int countMaxPage(int amount){
        if (amount % 10 == 0){
            return amount/10;
        }else {
            return amount / 10 + 1;
        }
    }

    public static List<Receipt> getReceiptOnPage(List<Receipt> receipts, int currantPage){
        int begin = (currantPage - 1) * 10;
        if (receipts.size() > 0 && receipts.size() < begin +10 ){
            receipts = receipts.subList(begin, receipts.size());
        }else {
            receipts = receipts.subList(begin, begin + 10);
        }
        return receipts;
    }
}