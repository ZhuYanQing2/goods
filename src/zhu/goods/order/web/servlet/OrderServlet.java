package zhu.goods.order.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.servlet.BaseServlet;
import zhu.goods.book.entity.Book;
import zhu.goods.order.entity.Order;
import zhu.goods.order.service.OrderService;
import zhu.goods.pager.PageBean;
import zhu.goods.user.entity.User;

public class OrderServlet extends BaseServlet {

	private OrderService orderService = new OrderService();
	
	
	private int getPc(HttpServletRequest request){
		int pc = 1;
		String param = request.getParameter("pc");
		if(param != null && !param.trim().isEmpty()){
			try {
				pc = Integer.parseInt(param);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return pc;
	}
	//截取url，页面中的分页导航中需要使用它作为超链接的目标
		private String getUrl(HttpServletRequest request){
			String url = request.getRequestURI() +"?"+ request.getQueryString();
			//如果url中存在pc参数，截取调
			int index = url.indexOf("&pc=");
			if(index != -1){
				url = url.substring(0,index);
			}
			return url;
		}
		
		/**
		 *根据分类查询 
		 */
		public String  myOrders(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			//1.得到pc 如果页面传递，使用页面，没传 pc = 1
			int pc = getPc(request);
			//2.url
			String url = getUrl(request);
			//3.从当前session中获取
			User user = (User) request.getSession().getAttribute("sessionUser");
			//4.使用pc和cid调用service findByCategory得到pagebena
			PageBean<Order> pb = orderService.myOrders(user.getUid(), pc);
			//5.个体pageBean设置url，返回jsps/book/list.jsp
			pb.setUrl(url);
			request.setAttribute("pb",pb);
			return "f:/jsps/order/list.jsp";
		}	
}
