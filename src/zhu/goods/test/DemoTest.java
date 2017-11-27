package zhu.goods.test;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import org.junit.Test;

import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;


public class DemoTest {

	@Test
	public void Testproperties() throws IOException, MessagingException{
		Properties pro = new Properties();
		pro.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		//登陆邮件服务器，得到session
		String host = pro.getProperty("host");//服务器主机名
		String name = pro.getProperty("username");//登陆名
		String pass = pro.getProperty("password");//登陆密码
		
		Session session = MailUtils.createSession(host, name, pass);
		String from  = pro.getProperty("from");
		String to = "ZhuYanQing_z@126.com";
		String subject = pro.getProperty("subject");
		String content = pro.getProperty("content");
		
		Mail mail = new Mail(from,to,subject,content);
		/*
		 * 发送邮件
		 */
		MailUtils.send(session, mail);
	}
}
