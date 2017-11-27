package zhu.goods.cart.service;

import java.sql.SQLException;
import java.util.List;

import cn.itcast.commons.CommonUtils;
import zhu.goods.cart.dao.CartItemDao;
import zhu.goods.cart.entity.CartItem;

public class CartItemService {

	private CartItemDao cartItemDao = new CartItemDao();
	
	//加载多个cartitem
	public List<CartItem> loadCartItems(String cartItemIds){
		try {
			return cartItemDao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	//修改条目数量
	public CartItem updateQuantity(String cartItemId,int quantity){
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
			return cartItemDao.findByCartItemId(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//批量删除
	public void batchDelete(String cartItemIds){
		try {
			cartItemDao.batchDelete(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 添加条目
	 * @param uid
	 * @return
	 */
	public void add(CartItem cartItem){
		try {
			//1.使用uid和bid 查询条目是否存在
			CartItem _cartItem = cartItemDao.findByUidAndBid
					(cartItem.getUser().getUid(), cartItem.getBook().getBid());
			if(_cartItem == null){//如果没有，添加新条目
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.addCartItem(cartItem);
			}
			else{//如果有，修改数量
				int quantity = cartItem.getQuantity() + _cartItem.getQuantity();
				//修改老条目的数量
				cartItemDao.updateQuantity(_cartItem.getCartItemId(), quantity);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	//显示我的购物车
	public List<CartItem> myCart(String uid){
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
