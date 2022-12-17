package com.epam.donetc.restaurant.filters;

import com.epam.donetc.restaurant.database.entity.User;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/manageOrders")
public class ClientFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        User user = (User) req.getSession().getAttribute("user");
        if (user == null){
            res.sendRedirect(req.getContextPath() + "/login");
        } else if (user.getRoleId() == 1) {
            res.sendRedirect(req.getContextPath() + "/menu");
        }else {
            chain.doFilter(req,res);
        }
    }
}
