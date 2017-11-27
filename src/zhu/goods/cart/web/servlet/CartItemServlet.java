package zhu.goods.cart.web.servlet;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import zhu.goods.book.entity.Book;
import zhu.goods.cart.entity.CartItem;
import zhu.goods.cart.service.CartItemService;
import zhu.goods.user.entity.User;

public class CartItemServlet extends BaseServlet {

	private CartItemService cartItemService = new CartItemService();
	
	public String loadCartItems(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取cartItemIds参数
		String cartItemIds = req.getParameter("cartItemIds");
		Double total = Double.parseDouble(req.getParameter("total"));
		//2.通过service得到List<CartItem>
		List<CartItem> cartItemList = cartItemService.loadCartItems(cartItemIds);
		//3.保存，转发
		req.setAttribute("cartItemList", cartItemList);
		req.setAttribute("total", total);
		return "f:/jsps/cart/showitem.jsp";
	}
	
	public String updateQuantity(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String cartItemId = req.getParameter("cartItemId");
		int quantity = Integer.parseInt(req.getParameter("quantity"));
		CartItem cartItem = cartItemService.updateQuantity(cartItemId, quantity);
		StringBuilder sb = new StringBuilder("{");
		sb.append("\"quantity\"").append(":").append(cartItem.getQuantity());
		sb.append(",");
		sb.append("\"subtotal\"").append(":").append(cartItem.getSubtotal());
		sb.append("}");
	System.out.println(sb);
		resp.getWriter().print(sb);
		return null;
	}
	
	
	/**
	 * 批量删除
	 */
	public String batchDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取cartItemIds参数
		String cartItemIds = req.getParameter("cartItemIds");
		//2.调用service
		cartItemService.batchDelete(cartItemIds);
		//3.返回list.jsp
		return myCart(req, resp);
	}
	
	
	/**
	 * 增加条目
	 */
	public String add(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.封装表单数据到cartItem中
		Map map = req.getParameterMap();
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = (User) req.getSession().getAttribute("sessionUser");
		cartItem.setBook(book);
		cartItem.setUser(user);
		//2.调用service完成添加
		cartItemService.add(cartItem);
		//.3查询出当前用户所有条目，转发至jsps/cart/list.jsp 
		return myCart(req, resp);
	}
	
	public String myCart(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.得到uid
		User user =  (User) req.getSession().getAttribute("sessionUser");
		String uid = user.getUid();
		//2.通过service得到当前用户的购物条目
		List<CartItem> cartItems = cartItemService.myCart(uid);
		//3.保存起来，转发到/cart/list.jsp
		req.setAttribute("cartItemList", cartItems);
		return "f:/jsps/cart/list.jsp";
	}
}
