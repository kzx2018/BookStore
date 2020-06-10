package com.kzx.bookstore.cart.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.kzx.bookstore.book.domain.Book;
import com.kzx.bookstore.cart.domain.CartItem;
import com.kzx.bookstore.cart.service.CartItemService;
import com.kzx.bookstore.user.domain.User;

/**
 * Servlet implementation class CartItemServlet
 */
@WebServlet("/CartItemServlet")
public class CartItemServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;
	private CartItemService cartItemService = new CartItemService();
	
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String cartItemIds = req.getParameter("cartItemIds");
		double total = Double.parseDouble(req.getParameter("total"));
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("total", total);
		req.setAttribute("cartItemIds", cartItemIds);
		return "f:/jsps/cart/showitem.jsp";
	}
	
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String cartItemId = req.getParameter("cartItemId");
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		CartItem cartItem = cartItemService.updateQuantity(cartItemId, quantity);
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
		sb.append(",");
		sb.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
		sb.append("}");
		resp.getWriter().print(sb);
		return null;
	}
	
	
	public String batchDelete(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		String cartItemIds = req.getParameter("cartItemIds");
		cartItemService.batchDelete(cartItemIds);
		return myCart(req,resp);
	}
	
	public String add(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		Map map = req.getParameterMap();
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = (User)req.getSession().getAttribute("sessionUser");
		if(user==null) {
			req.setAttribute("code", "error");
			req.setAttribute("msg", "请登陆再试试！");
			return "f:/jsps/msg.jsp";
		}
		cartItem.setBook(book);
		cartItem.setUser(user);
		cartItemService.add(cartItem);
		return myCart(req,resp);
	}
	
	public String myCart(HttpServletRequest req, HttpServletResponse resp) 
			throws ServletException, IOException {
		User user = (User)req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		List<CartItem> cartItemList =cartItemService.myCart(uid);
		req.setAttribute("cartItemList", cartItemList);
		return "f:/jsps/cart/list.jsp";
	}
}
