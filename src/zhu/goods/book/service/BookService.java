package zhu.goods.book.service;

import java.sql.SQLException;

import zhu.goods.book.dao.BookDao;
import zhu.goods.book.entity.Book;
import zhu.goods.pager.PageBean;

public class BookService {

	private BookDao bookDao = new BookDao();
	
	//加载图书
	public Book load(String bid){
		try {
			return bookDao.findByBid(bid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//按分类查询
	public PageBean<Book> findByCatetory(String cid , int pc){
		try {
			return bookDao.findByCaregory(cid, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//按书名查询 
	public PageBean<Book> findByBname(String bname , int pc){
		try {
			return bookDao.findByBname(bname, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//按作者查询
	public PageBean<Book> findByAuthor(String author , int pc){
		try {
			return bookDao.findByAuthor(author, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	//按出版社查询
	public PageBean<Book> findByPress(String press , int pc){
		try {
			return bookDao.findByPress(press, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	//多条件组合查询
	public PageBean<Book> findByCombination(Book book , int pc){
		try {
 			return bookDao.findByCombination(book, pc);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void main(String[] args) {
		BookService bookService = new BookService();
		PageBean<Book> book = new PageBean<Book>();
		book = bookService.findByBname("java", 1);
		System.out.println(book);
	}
}
