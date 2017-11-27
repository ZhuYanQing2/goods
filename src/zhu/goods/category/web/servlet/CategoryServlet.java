package zhu.goods.category.web.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import zhu.goods.category.entity.Category;
import zhu.goods.category.service.CategoryService;
import cn.itcast.servlet.BaseServlet;

/**
 * 分类模块web层
 * @author 84294
 *
 */
public class CategoryServlet extends BaseServlet {

	private CategoryService categoryService = new CategoryService();
	
	public String findAll(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.t通过service得到所有分类
		List<Category> parents = categoryService.findAll();
		//2.保存到request中，转发到left.jsp
		req.setAttribute("parents",parents);
		return "f:/jsps/left.jsp";
	}
}
