package zhu.goods.book.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import zhu.goods.book.entity.Book;
import zhu.goods.category.entity.Category;
import zhu.goods.pager.Expression;
import zhu.goods.pager.PageBean;
import zhu.goods.pager.PageConstants;

public class BookDao {

	private QueryRunner queryRunner = new TxQueryRunner();
	
	//按bid查询
	public Book findByBid(String bid) throws SQLException{
		String sql = "select * from t_book where bid =?";
		Map<String ,Object> map = queryRunner.query(sql,new MapHandler(), bid );
		Book book = CommonUtils.toBean(map, Book.class);
		Category category= CommonUtils.toBean(map,Category.class);
		book.setCategory(category);
		return book;
		
	}
	
	//安分类查询
	public PageBean<Book> findByCaregory(String cid,int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("cid","=",cid));
		return findByCriteria(exprList, pc);
		}
	
	//按书名模糊查询
	public PageBean<Book> findByBname(String bname, int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+bname+"%"));
		return findByCriteria(exprList, pc);
	}
	
	//按作者查询
	public PageBean<Book> findByAuthor(String author, int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("author","like","%"+author+"%"));
		return findByCriteria(exprList, pc);
	}
	
	//按出版社查询
	public PageBean<Book> findByPress(String press, int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("press","like","%"+press+"%"));
		return findByCriteria(exprList, pc);
	}
	
	//多条件组合查询
	public PageBean<Book> findByCombination(Book book, int pc) throws SQLException{
		List<Expression> exprList = new ArrayList<Expression>();
		exprList.add(new Expression("bname","like","%"+book.getBname()+"%"));
		exprList.add(new Expression("author","like","%"+book.getAuthor()+"%"));
		exprList.add(new Expression("press","like","%"+book.getPress()+"%"));
		return findByCriteria(exprList, pc);
	}
	
	//通用查询方法
	private PageBean<Book> findByCriteria(List<Expression> expression,int pc) throws SQLException{
		//1.得到pc
		int ps = PageConstants.BOOK_PAGE_SIZE;
		//1.2通过expression生城where字句
		StringBuilder whereSql = new StringBuilder(" where 1 = 1");
		List<Object> params = new ArrayList<Object>();//对应问好的值
		for(Expression expr : expression){
			//添加一个条件，以and开头，条件名称，条件运算符
			whereSql.append(" and ").append(expr.getName())
			.append(" ").append(expr.getOperator()).append(" ");
			if(!expr.getOperator().equals("is null")){
				whereSql.append("?");
				params.add(expr.getValue());
			}
		}
		//2.得到tr
		String sql = "select count(*) from t_book"+whereSql;
		Number number = (Number) queryRunner.query(sql, new ScalarHandler(),params.toArray());
		int tr = number.intValue();//获得总记录数
		//3.得到beanList
		sql = "select * from t_book " + whereSql +" order by orderby limit ? , ? ";
		params.add((pc-1)*ps);//当前页 首行记录的下标
		params.add(ps);//一共查询多少行
		List<Book> beanList = queryRunner.query(sql, new BeanListHandler<Book>(Book.class),params.toArray());
		//4.创建pageBean，返回
		PageBean<Book> pb = new PageBean<Book>();
		pb.setPc(pc);
		pb.setPs(ps);
		pb.setTr(tr);
		pb.setBeanList(beanList);
		
//		System.out.print(sql);
//		System.out.println(params);
//		return  pageBean;
		return pb;
	}
	
	public static void main(String[] args) throws SQLException {
		BookDao dao = new BookDao();
		Book book = new Book();
//		book.setBname("Java编程思想（第4版）");
//		book.setAuthor("布鲁斯.艾克尔");
//		book.setPress("机械工业出版社");
//		List<Expression> list =  new ArrayList<Expression>();
//		list.add(new Expression("bid","=","1"));
//		list.add(new Expression("bname","like","%java%"));
//		list.add(new Expression("edition","is null",null));
//		dao.findByCaregory("5F79D0D246AD4216AC04E9C5FAB3199E", 1);
//		dao.findByAuthor("刘西杰", 1);
		PageBean<Book> bean =dao.findByBname("Java", 1);
		System.out.println(bean);
//		dao.findByCombination(book, 1);
	}
}
