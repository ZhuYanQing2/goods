package zhu.goods.category.service;

import java.sql.SQLException;
import java.util.List;

import zhu.goods.category.dao.CategoryDao;
import zhu.goods.category.entity.Category;

/**
 * 分类模块的业务层
 * @author 84294
 *
 */
public class CategoryService {

	private CategoryDao categoryDao = new CategoryDao();
	
	public List<Category> findAll(){
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
