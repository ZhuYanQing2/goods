package zhu.goods.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.New;
import javax.resource.spi.RetryableUnavailableException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.jdbc.TxQueryRunner;
import sun.net.www.content.text.plain;
import zhu.goods.book.entity.Book;
import zhu.goods.cart.entity.CartItem;
import zhu.goods.user.entity.User;

public class CartItemDao {

	private QueryRunner qr = new TxQueryRunner();
	
	public CartItem findByCartItemId(String cartItemId) throws SQLException{
		String sql = "select * from t_cartitem c,t_book b where c.bid = b.bid and c.cartItemId = ?";
		Map<String , Object> map = qr.query(sql, new MapHandler(),cartItemId);
		return toCartItem(map);
	}
	
	//生成where语句
	private String  toWhereSql(int len){
		StringBuilder sb = new StringBuilder("cartItemId in(");
		for(int i = 0 ; i < len ; i++){
			sb.append("?");
			if(i < len - 1){
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	//加载多个cartitem
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException{
		//1.把cartitems转换成数组
		Object[] cartItemIdArray = cartItemIds.split(",");
		//2.生成where子句
		String whereSql = toWhereSql(cartItemIdArray.length);
		//3.生成sql语句
		String sql = "select * from t_cartitem c , t_book b where c.bid = b.bid and " + whereSql;
		return toCartItemList(qr.query(sql, new MapListHandler(),cartItemIdArray));
	}
	
	//批量删除
	public  void batchDelete(String cartItemIds) throws SQLException{
		//1.需要先把转换成where字句
		Object[] cartItemIdArray = cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "delete from  t_cartitem where "+whereSql;
		qr.update(sql,cartItemIdArray);
	}
	
	/**
	 * 查询某个用户的某个图书的购物条目是否存在
	 * @throws SQLException 
	 */
	public CartItem findByUidAndBid(String uid,String bid) throws SQLException{
		String sql = "select * from t_cartitem where uid = ? and bid = ? ";
		Map<String , Object> map = qr.query(sql, new MapHandler(),uid,bid);
		CartItem cartItem = toCartItem(map);
		return cartItem;
	}
	
	/**
	 * 修改指定购物条目的数量
	 * @throws SQLException 
	 */
	public void updateQuantity(String cartItemId ,int quantity) throws SQLException{
		String sql = "update t_cartitem set quantity = ? where cartItemId = ?";
		qr.update(sql,quantity,cartItemId);
	}
	
	/**
	 * 添加购物条目
	 * @throws SQLException 
	 */
	public void addCartItem(CartItem cartItem) throws SQLException{
		String sql = "insert into t_cartitem (cartItemId,quantity,bid,uid) "
				+ " values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(),cartItem.getQuantity()
				,cartItem.getBook().getBid(),cartItem.getUser().getUid()};
		qr.update(sql,params);
	}
	
	/**
	 * 比一个map映射城一个CartItem
	 * @param map
	 * @return
	 */
	private CartItem toCartItem(Map<String, Object> map){
		if (map == null) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	
	/**
	 * 将多个mapList<Map>映射成多个cartItem（List<CartItem>）
	 */
	private List<CartItem> toCartItemList (List<Map<String, Object>> mapList){
		List<CartItem> cartItems = new ArrayList<CartItem>();
		for(Map<String, Object> map : mapList){
			CartItem cartItem = toCartItem(map);
			cartItems.add(cartItem);
		}
		return cartItems;
	}
	
	
	/**
	 * 通过用户查询购物车条目
	 * @param uid
	 * @return
	 * @throws SQLException 
	 */
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from t_cartitem c, t_book b where c.bid = b.bid and uid=? order by c.orderby";
		List<Map<String ,Object>> mapList = qr.query(sql, new MapListHandler(),uid);
		return toCartItemList(mapList);
	}
}
