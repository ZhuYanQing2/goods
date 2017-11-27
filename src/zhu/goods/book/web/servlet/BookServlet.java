package zhu.goods.book.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;
import zhu.goods.book.entity.Book;
import zhu.goods.book.service.BookService;
import zhu.goods.pager.PageBean;

public class BookServlet extends BaseServlet {

	private BookService bookService = new BookService();
	
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
	 * 按bid加载详细信息
	 */
	public String load(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String bid = request.getParameter("bid");
		Book book = bookService.load(bid);
		request.setAttribute("book", book);
		return "f:/jsps/book/desc.jsp";
	}

	
	/**
	 *根据分类查询 
	 */
	public String  findByCategory(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.得到pc 如果页面传递，使用页面，没传 pc = 1
		int pc = getPc(request);
		//2.url
		String url = getUrl(request);
		//3.获取查询条件，笨方法就是cid，即分类的id
		String  cid = request.getParameter("cid");
		//4.使用pc和cid调用service findByCategory得到pagebena
		PageBean<Book> pb = bookService.findByCatetory(cid, pc);
		//5.个体pageBean设置url，返回jsps/book/list.jsp
		pb.setUrl(url);
		request.setAttribute("pb",pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 *根据作者查询 
	 */
	public String  findByAuthor(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.得到pc 如果页面传递，使用页面，没传 pc = 1
		int pc = getPc(request);
		//2.url
		String url = getUrl(request);
		//3.获取查询条件，笨方法就是cid，即分类的id
		String  author = request.getParameter("author");
		//4.使用pc和cid调用service findByCategory得到pagebena
		PageBean<Book> pb = bookService.findByAuthor(author, pc);
		//5.个体pageBean设置url，返回jsps/book/list.jsp
		pb.setUrl(url);
		request.setAttribute("pb",pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 *根据出版社查询 
	 */
	public String  findByPress(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.得到pc 如果页面传递，使用页面，没传 pc = 1
		int pc = getPc(request);
		//2.url
		String url = getUrl(request);
		//3.获取查询条件，笨方法就是cid，即分类的id
		String  press = request.getParameter("press");
		//4.使用pc和cid调用service findByCategory得到pagebena
		PageBean<Book> pb = bookService.findByPress(press, pc);
		//5.个体pageBean设置url，返回jsps/book/list.jsp
		pb.setUrl(url);
		request.setAttribute("pb",pb);
		return "f:/jsps/book/list.jsp";
	}
	
	/**
	 *根据书名查询 
	 */
	public String  findByBname(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.得到pc 如果页面传递，使用页面，没传 pc = 1
		int pc = getPc(request);
		//2.url
		String url = getUrl(request);
		//3.获取查询条件，笨方法就是cid，即分类的id
		String  bname = request.getParameter("bname");
		//4.使用pc和cid调用service findByCategory得到pagebena
		PageBean<Book> pb = bookService.findByBname(bname, pc);
		//5.个体pageBean设置url，返回jsps/book/list.jsp
		pb.setUrl(url);
		request.setAttribute("pb",pb);
		return "f:/jsps/book/list.jsp";
	}

	/**
	 *多条件组合查询 
	 */
	public String  findByCombination(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//1.得到pc 如果页面传递，使用页面，没传 pc = 1
		int pc = getPc(request);
		//2.url
		String url = getUrl(request);
		//3.获取查询条件，笨方法就是cid，即分类的id
		Book book = CommonUtils.toBean(request.getParameterMap(), Book.class);
		//4.使用pc和cid调用service findByCategory得到pagebena
		PageBean<Book> pb = bookService.findByCombination(book, pc);
		//5.个体pageBean设置url，返回jsps/book/list.jsp
		pb.setUrl(url);
		request.setAttribute("pb",pb);
		return "f:/jsps/book/list.jsp";
	}
}
