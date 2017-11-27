package zhu.goods.order.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.sun.org.apache.bcel.internal.generic.LALOAD;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import zhu.goods.book.entity.Book;
import zhu.goods.order.entity.Order;
import zhu.goods.order.entity.OrderItem;
import zhu.goods.pager.Expression;
import zhu.goods.pager.PageBean;
import zhu.goods.pager.PageConstants;

public class OrderDao {

	private QueryRunner qr = new TxQueryRunner();
	
	//通用查询方法
		private PageBean<Order> findByCriteria(List<Expression> expression,int pc) throws SQLException{
			//1.得到pc
			int ps = PageConstants.ORDER_PAGE_SIZE;
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
			String sql = "select count(*) from t_order"+whereSql;
			Number number = (Number) qr.query(sql, new ScalarHandler(),params.toArray());
			int tr = number.intValue();//获得总记录数
			//3.得到beanList
			sql = "select * from t_book " + whereSql +" order by ordertime desc limit ? , ? ";
			params.add((pc-1)*ps);//当前页 首行记录的下标
			params.add(ps);//一共查询多少行
			List<Order> beanList = qr.query(sql, new BeanListHandler<Order>(Order.class),params.toArray());
			//遍历每个订单，为其增加条目
			for(Order order :beanList){
				loadOrderItem(order);
			}
			//4.创建pageBean，返回
			PageBean<Order> pb = new PageBean<Order>();
			pb.setPc(pc);
			pb.setPs(ps);
			pb.setTr(tr);
			pb.setBeanList(beanList);
			
//			System.out.print(sql);
//			System.out.println(params);
//			return  pageBean;
			return pb;
		}
		
		//为指定指定Order加载所有OrderItem
		private void loadOrderItem(Order order) throws SQLException {
			//1.给sql语句select * from t_orderitem where oid = ？
			String sql = "select * from t_orderitem where oid = ? ";
			//2.执行，得到List<OrderItem>
			List<Map<String ,Object>> mapList = qr.query(sql, new MapListHandler(),order.getOid());
			List<OrderItem> orderItemList = toOrderItemList(mapList);
			//3.设置给Order对象
			order.setOrderItemList(orderItemList);
		}

		//吧多个map转换成多个OrderItem
		private List<OrderItem> toOrderItemList(List<Map<String, Object>> mapList) {
			List<OrderItem> orderItemList = new ArrayList<OrderItem>();
			for(Map<String ,Object> map : mapList){
				OrderItem orderItem = toOrderItem(map);
				orderItemList.add(orderItem);
			}
			return orderItemList;
		}

		//吧一个map转换成一个OrderIt
		private OrderItem toOrderItem(Map<String, Object> map) {
			OrderItem orderItem = CommonUtils.toBean(map, OrderItem.class);
			Book book  = CommonUtils.toBean(map, Book.class);
			orderItem.setBook(book);
			return orderItem;
		}

		//按用户查询
		public PageBean<Order> findByUser(String uid, int pc) throws SQLException{
			List<Expression> exprList = new ArrayList<Expression>();
			exprList.add(new Expression("uid","=",uid));
			return findByCriteria(exprList, pc);
		}
}
