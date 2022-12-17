package com.epam.donetc.restaurant.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.epam.donetc.restaurant.database.CartDAO;
import com.epam.donetc.restaurant.database.ReceiptDAO;
import com.epam.donetc.restaurant.database.entity.Dish;
import com.epam.donetc.restaurant.database.entity.Receipt;
import com.epam.donetc.restaurant.database.entity.User;
import com.epam.donetc.restaurant.exception.AppException;
import com.epam.donetc.restaurant.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/myOrders")
public class ClientOrdersServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(ClientOrdersServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        String curPage = request.getParameter("currentPage");
        if(curPage == null || curPage.isEmpty()) curPage = "1";
        int currentPage = Integer.parseInt(curPage);
        try{
            List<Receipt> receipts;
            receipts = ReceiptDAO.getReceiptByUserId(user.getId());
            if(receipts.size()>0){
                int maxPage = ReceiptDAO.countMaxPage(receipts.size());
                receipts = ReceiptDAO.getReceiptOnPage(receipts, currentPage);
                log.trace("current page == " + currentPage);
                log.trace("receipts == " + receipts);
                session.setAttribute("maxPage", maxPage);
            }
            session.setAttribute("receipts", receipts);
            request.getRequestDispatcher("/WEB-INF/jsp/client-orders.jsp").forward(request, response);
        } catch(DBException ex){
            log.error("In client orders servlet doGet() ", ex);
            throw new AppException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        Map<Dish, Integer> cart = (Map<Dish, Integer>) session.getAttribute("cart");
        try{
            CartDAO.submitOrder(user.getId(), cart);
            CartDAO.cleanCart(user.getId());
        }catch (DBException ex){
            log.error("In client orders servlet doPost() ", ex);
            throw new AppException(ex);
        }
        response.sendRedirect(request.getContextPath() + "/myOrders");
    }
}