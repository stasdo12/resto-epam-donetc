package com.epam.donetc.restaurant.servlets;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.List;

import com.epam.donetc.restaurant.database.CartDAO;
import com.epam.donetc.restaurant.database.DishDAO;
import com.epam.donetc.restaurant.database.ReceiptDAO;
import com.epam.donetc.restaurant.database.entity.Dish;
import com.epam.donetc.restaurant.database.entity.User;
import com.epam.donetc.restaurant.exception.AppException;
import com.epam.donetc.restaurant.exception.DBException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@WebServlet("/menu")
public class ClientMenuServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(ClientMenuServlet.class);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String category = request.getParameter("category");
        String sortBy = request.getParameter("sortBy");
        if(sortBy == null) sortBy = "category";
        String curPage = request.getParameter("currentPage");
        if(curPage == null || curPage.isEmpty()) curPage = "1";
        int currentPage = Integer.parseInt(curPage);
        HttpSession session = request.getSession();
        try{
            List<Dish> dishes;
            if(category == null || category.isEmpty() || category.equalsIgnoreCase("All")){
                dishes = DishDAO.getAllDishes();
            }else{
                dishes = DishDAO.getDishesByCategory(category);
            }
            int maxPage = ReceiptDAO.countMaxPage(dishes.size());

            log.debug("dishes size before sorting == " + dishes.size());
            dishes = DishDAO.sortBy(dishes, sortBy);
            log.debug("dishes were sorted");

            log.trace("current page == " + currentPage);
            log.debug("dishes size before getDishOnPage == " + dishes.size());
            dishes = DishDAO.getDishesOnePage(dishes, currentPage);

            session.setAttribute("maxPage", maxPage);
            session.setAttribute("dishes", dishes);
            request.getRequestDispatcher("/WEB-INF/jsp/client-menu.jsp").forward(request, response);
        }catch (DBException ex){
            log.error("In Client menu servlet doGet() ", ex);
            throw new AppException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        int dishId = Integer.parseInt(request.getParameter("id"));
        int amount = Integer.parseInt(request.getParameter("amount"));
        try{
            if(!CartDAO.getCart(user.getId()).containsKey(DishDAO.getDishByID(dishId))){
                CartDAO.addDishToCart(user.getId(), dishId, amount);
            }
        }catch (DBException ex){
            log.error("In Client menu servlet doPost() ", ex);
            throw new AppException(ex);
        }
        response.sendRedirect(request.getContextPath() + "/menu");
    }
}
