package zhu.goods.order.service;

import java.sql.SQLException;

import cn.itcast.jdbc.JdbcUtils;
import zhu.goods.order.dao.OrderDao;
import zhu.goods.order.entity.Order;
import zhu.goods.pager.PageBean;

public class OrderService {

	private OrderDao orderDao = new OrderDao();
	
	//我的订单
	public PageBean<Order> myOrders(String uid , int pc ){
		try {
			JdbcUtils.beginTransaction();
			PageBean<Order>  pb =orderDao.findByUser(uid, pc);
			JdbcUtils.commitTransaction();
			return pb;
		} catch (SQLException e) {
			try {
				JdbcUtils.rollbackTransaction();
			} catch (SQLException e1) {
			}
			throw new RuntimeException(e);
		}
	}
}
