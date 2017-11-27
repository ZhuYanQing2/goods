 package zhu.goods.user.web.servlet;

import static org.hamcrest.CoreMatchers.nullValue;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jms.Session;
import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import zhu.goods.user.entity.User;
import zhu.goods.user.service.UserService;
import zhu.goods.user.service.exception.UserException;
import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

/**
 * 用户模块的web层
 *
 */
public class UserServlet extends BaseServlet {

	private UserService userService = new UserService();
	
	/**
	 * ajax用户名是否注册校验
	 */
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取用户名
		String loginname = req.getParameter("loginname");
		//2.通过service得到校验结果
		boolean bool = userService.ajaxValidateLoginname(loginname);
		//3.发给客户端
		resp.getWriter().print(bool);
		return null;
	}
	/**
	 * ajaxEmail是否注册校验
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取Email
		String email = req.getParameter("eamil");
		//2.通过service得到校验结果
		boolean bool = userService.ajaxValidateEmail(email);
		//3.发给客户端
		resp.getWriter().print(bool);
		return null;
	}
	
	/**
	 * ajax 验证码是否正确校验
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取输入框的验证码
		String verifyCode = req.getParameter("verifyCode");
		//2，获取图片上的正式验证码
		String vCode =  (String) req.getSession().getAttribute("vCode");
		//3.进行图略大小写比较，得到结果
		boolean  bool = verifyCode.equalsIgnoreCase(vCode);
		resp.getWriter().print(bool);
		return null;
	}
	
	
	/**
	 * @param req
	 * @param resp
	 * @return
	 * @throws ServletException
	 * @throws IOException
	 */
	public String regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.封装表单数据到User对象
		User fromUser = CommonUtils.toBean(req.getParameterMap(),User.class);
		//2。校验 如果校验失败，保持错误信息，返回到regist.jsp显示
		Map<String, String> errors = validateRegist(fromUser, req.getSession());
		if(errors.size() > 0){
			req.setAttribute("from", fromUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		//3.使用service完成业务
		userService.regist(fromUser);
		//4.保持成功信息，转发到msg.jsp
		req.setAttribute("code","success"); 
		req.setAttribute("msg","注册成功，请马上到邮箱激活");
		return "f:/jsps/msg.jsp";
	}
	
	//注册校验 对表单字段逐个校验 如有错误，字段名称为key 错误信息为value
	private Map<String, String> validateRegist(User fromUser,HttpSession sessin){
		Map<String, String> errors = new HashMap<String,String>();
		//1.用户名校验
		String loginname = fromUser.getLoginname();
		if(loginname == null || loginname.trim().isEmpty()){
			errors.put("loginname", "用户名不能为空");
		}else if(loginname.length() < 3 || loginname.length() >20) {
			errors.put("loginname", "用户名长度必须在3-20之间");
		}else if (!userService.ajaxValidateLoginname(loginname)) {
			errors.put("loginname", "用户名已被注册");
		}
		//2.密码校验
		String loginpass = fromUser.getLoginpass();
		if(loginpass == null || loginpass.trim().isEmpty()){
			errors.put("loginpass", "密码不能为空");
		}else if (loginpass.length() < 3 || loginpass.length() > 20 ) {
			errors.put("loginpass", "密码长度必须在3-20之间");
		}
		
		//3.确认密码校验
		String reloginpass = fromUser.getReloginpass();
		if(reloginpass == null || reloginpass.trim().isEmpty()){
			errors.put("reloginpass", "密码不能为空");
		}else if (!reloginpass.equals(loginpass)) {
			errors.put("reloginpass", "两次密码输入不一致");
		}
		
		//4.Email校验
		String email = fromUser.getEmail();
		if(email == null || email.trim().isEmpty()){
			errors.put("email", "email不能为空");
		}else if (!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")) {
			errors.put("email", "email格式不对");
		}else if (!userService.ajaxValidateEmail(email)) {
			errors.put("email", "email已被注册");
		}
		
		//5.验证码校验
		String verifyCode = fromUser.getVerifyCode();
		String vCode = (String) sessin.getAttribute("vCode");
		if(verifyCode == null || verifyCode.trim().isEmpty()){
			errors.put("verifyCode", "验证码不能为空");
		}else if (!verifyCode.equalsIgnoreCase(vCode)) {
			errors.put("verifyCode", "验证码错误");
		}
		return errors;
	}
	
	/**
	 * 激活功能
	 */
	public String activation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.获取激活码
		String code = req.getParameter("activationCode");
		//2.用激活码调用service完成激活
		try {
			userService.activation(code);
			req.setAttribute("code", "success");
			req.setAttribute("msg", "恭喜，激活成功，您可以登录");
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("code", "error");
		}
		//service抛出异常，吧异常消息拿出来，保存到request盅，转发到msg.jsp
		//3.保存成功信息到request，转发到msg.jsp显示
		return "f:/jsps/msg.jsp";
	}
	
	public String login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		//1.封装表单参数到User
		User fromUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		//2.校验表单数据
		Map<String, String> errors = validateLogin(fromUser, req.getSession());
		if(errors.size()>0){
			req.setAttribute("user", fromUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		}
		//3.使用service查询得到user
		User user = userService.login(fromUser);
		//4.查看用户是否存在
		if(user == null){
			//	保存错误信息，用户名或者密码错误
			req.setAttribute("msg", "用户名或密码错误");
			//	保存表单数据，回显
			req.setAttribute("user", fromUser);
			//	转发到login.jsp
			return "f:/jsps/user/login.jsp" ;
			}else{
				//5.如果存在，查看状态，如果为false
			if(!user.isStatus()){
				// 	保存错误信息，您没有激活
				req.setAttribute("msg", "您还未激活");
				//	保存表单信息，回显
				req.setAttribute("user", fromUser);
				//	转发到login.jsp
				return "f:/jsps/user/login.jsp" ;
			}else{
				//6、登录成功
				//	保存当钱查询的用户到session盅
				req.getSession().setAttribute("sessionUser", user);
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname,"utf-8");
				//	保存用户名称到cokic，注意中文编码处理
				Cookie cookie = new Cookie("loginname",loginname );
				cookie.setMaxAge(60*60*24*10);//保存十天
				resp.addCookie(cookie);
				return "r:/index.jsp";
				}
		}
	}
	
	//登录校验 对表单字段逐个校验 如有错误，字段名称为key 错误信息为value
		private Map<String, String> validateLogin(User fromUser,HttpSession sessin){
			Map<String, String> errors = new HashMap<String,String>();
			//1.用户名校验
			String loginname = fromUser.getLoginname();
			if(loginname == null || loginname.trim().isEmpty()){
				errors.put("loginname", "用户名不能为空");
			}
			//2.密码校验
			String loginpass = fromUser.getLoginpass();
			if(loginpass == null || loginpass.trim().isEmpty()){
				errors.put("loginpass", "密码不能为空");
			}else if (loginpass.length() < 3 || loginpass.length() > 20 ) {
				errors.put("loginpass", "密码长度必须在3-20之间");
			}
			//5.验证码校验
			String verifyCode = fromUser.getVerifyCode();
			String vCode = (String) sessin.getAttribute("vCode");
			if(verifyCode == null || verifyCode.trim().isEmpty()){
				errors.put("verifyCode", "验证码不能为空");
			}else if (!verifyCode.equalsIgnoreCase(vCode)) {
				errors.put("verifyCode", "验证码错误");
			}
			return errors;
		}
		
		//修改密码
		public String updatePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			//1.封装表单数据到user中
			User fromUser = CommonUtils.toBean(req.getParameterMap(), User.class);
			//2.从session中获取uid
			User user = (User) req.getSession().getAttribute("sessionUser");
			if(user == null ){
				req.setAttribute("msg", "您还没有登录");
				return "f:/jsps/user/login.jsp" ;
			}
			//3.使用uid和表单中的老/新密码调用service方法
			try {
				userService.updatePassword(user.getUid(),fromUser.getNewpass(),fromUser.getLoginpass());
				req.setAttribute("msg", "修改密码成功");
				req.setAttribute("code","success");
				//5.转发到msg.jsp
				return "f:/jsps/msg.jsp";
			} catch (UserException e) {
				//4.保存成功信息到request
				req.setAttribute("msg", e.getMessage());//保存异常信息到request
				req.setAttribute("user",fromUser);
				//  如果抛出异常，保存异常信息到request中，转发到pwd.jsp
				return "f:/jsps/user/pwd.jsp";
			}
		}
		
		public String Exit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
			req.getSession().invalidate();
			return "r:/jsps/user/login.jsp" ;
		}
}







