package zhu.goods.order.entity;

import java.util.List;

import zhu.goods.user.entity.User;

public class Order {

	private String oid;//主键
	private String ordertime;//下单时间
	private double total;//总计
	private int status;//订单状态，1.未付款，2，已付款但未发货，3、已发货未确认，4.确认收货，交易成功5.已经取消
	private String address;//收货地址 
	private User owner;//订单所有者
	private List<OrderItem> OrderItemList;
	
	public List<OrderItem> getOrderItemList() {
		return OrderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		OrderItemList = orderItemList;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	
}
