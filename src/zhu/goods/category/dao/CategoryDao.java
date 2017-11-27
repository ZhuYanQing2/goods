package zhu.goods.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import zhu.goods.category.entity.Category;
import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;

/**
 * 分类持久层
 * @author 84294
 *
 */
public class CategoryDao {

	private QueryRunner qr = new TxQueryRunner();
	
	private Category toCategory(Map<String , Object> map){
		//map {cid,cname,pid ,desc ,orderby}
		//Category (cid,cname,perpent,desc)
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String) map.get("pid");
		if(pid != null){//如果父分类id不为空
			//使用一个父分类对象来装在装载pid
			//再把父分类设置给category
			Category parent = new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	
	private List<Category> toCategoryList(List<Map<String, Object>> mapList){
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String,Object> map : mapList){
			Category c = toCategory(map);
			categoryList .add(c);
		}
		return categoryList;
	} 
	
	
	public List<Category> findAll() throws SQLException{
		String sql = "select * from t_category where pid is null";
		//cid , cname , pid ,desc
		List<Map<String, Object>> mapList = qr.query(sql, new MapListHandler());
		List<Category> parents = toCategoryList(mapList);
		//x循环遍历每个一级分类，为每个一级分类添加二级分类
		for (Category parent: parents) {
			//查询出当前父分类的子分类，
			List<Category> children = findByParent(parent.getCid());
			//设置给父分类
			parent.setChildren(children);
		}
		return parents;
	}
	
	public List<Category> findByParent(String pid) throws SQLException {
		String sql = "select * from t_category where pid = ?";
		List<Map<String,Object>> maps = qr.query(sql,new MapListHandler(), pid);
		return toCategoryList(maps);
	}
}







