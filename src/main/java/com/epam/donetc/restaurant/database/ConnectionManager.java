package com.epam.donetc.restaurant.database;

import com.epam.donetc.restaurant.util.PropertiesUtil;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConnectionManager {
    static {
        loadDriver();
    }

    private static final String PASSWORD_KEY = "db.password";
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String POOL_SIZE = "db.pool.size";
    private static  BlockingQueue<Connection> pool;
    private static final Integer DEFAULT_POOL_SIZE = 10;


    private static void loadDriver() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        initConnectionPool();
    }

    private  ConnectionManager() {
    }

    private static void initConnectionPool() {
        String poolSize = PropertiesUtil.get(POOL_SIZE);
        PropertiesUtil.get(poolSize);

        var size  = poolSize ==null?DEFAULT_POOL_SIZE: Integer.parseInt(poolSize);
        pool = new ArrayBlockingQueue<>(size);

        for (int i = 0; i < size; i++) {
            var connection = open();
            var proxyConnection = (Connection)Proxy.newProxyInstance(ConnectionManager.class.getClassLoader(),
                    new Class[]{Connection.class}, ((proxy, method, args) ->
                            method.getName().equals("close")
                                    ? pool.add((Connection) proxy): method.invoke(connection, args)));
            pool.add(proxyConnection);

        }

    }


    public static Connection get(){
        try {
            return pool.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    private static Connection open(){
        try {
            return DriverManager.getConnection(
                    PropertiesUtil.get(URL_KEY),
                    PropertiesUtil.get(USERNAME_KEY),
                    PropertiesUtil.get(PASSWORD_KEY)
            );
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rollback(Connection con) {
        try {
            con.rollback();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void rollback(Connection con, Savepoint s) {
        try {
            con.rollback(s);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void close(AutoCloseable closeable){
        try {
            closeable.close();
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
