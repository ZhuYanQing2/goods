package zhu.goods.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;


import zhu.goods.user.dao.UserDao;
import zhu.goods.user.entity.User;
import zhu.goods.user.service.exception.UserException;
import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.MailUtils;
import cn.itcast.mail.Mail;
/**
 * 用户模块的业务层
 * @author 84294
 *
 */
public class UserService {

	private UserDao userDao = new UserDao();
	
	/**
	 * 用户名校验
	 * @param loginname
	 * @return
	 */
	public boolean ajaxValidateLoginname(String loginname) {
			try {
				return  userDao.ajaxValidateLoginname(loginname);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
	}
	
	/**
	 * Email校验
	 * @param loginname
	 * @return
	 */
	public boolean ajaxValidateEmail(String email) {
		try {
			return  userDao.ajaxValidateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 注册功能
	 * @throws IOException 
	 * @throws MessagingException 
	 */
	public void regist(User user) {
		//1.数据补齐
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		//2.向数据库插入数据
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		//3.发邮件
		//把配置文件加载到pro盅
		Properties pro = new Properties();
		try {
			pro.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		//登陆邮件服务器，得到session
		String host = pro.getProperty("host");//服务器主机名
		String name = pro.getProperty("username");//登陆名
		String pass = pro.getProperty("password");//登陆密码
		
		Session session = MailUtils.createSession(host, name, pass);
		//创建Mail对象
		String from  = pro.getProperty("from");
		String to = user.getEmail();
		String subject = pro.getProperty("subject");
		//MessageFormat.format方法会把第一个参数中的{0}使用第二个参数来替换
		//例如MessageFormat.format("你好{0}"，"你{1}","张三","去死吧") 你好张三，你去死吧 
		String content = MessageFormat.format( pro.getProperty("content"), user.getActivationCode());
		
		Mail mail = new Mail(from,to,subject,content);
		/*
		 * 发送邮件
		 */
		try {
			MailUtils.send(session, mail);
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	//激活逻辑
	public void activation(String code) throws UserException{
		//1.通过激活码查询用户
		try {
			User user = userDao.findByCode(code);
			//2.如果User为null，说明激活码无效，跑出异常，给出异常信息
			if(user == null) throw new UserException("无效激活码");
			//3.查看用户状态是否为true，如果为true，跑出异常，给我异常信息（请不要二次激活）
			if(user.isStatus())throw new UserException("您已经激活了，请不要二次激活");
			//4.修改status为true
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public User login(User user){
		try {
			return userDao.findByLoginnameAndLoginpass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new  RuntimeException();
		}
	}
	
	public void updatePassword(String uid,String newPass, String oldPass) throws UserException{
		try {
			//1,校验老密码
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			if(!bool){//如果老密码错误
				throw new UserException("老密码错误");
			}
			//2、修改密码
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}





