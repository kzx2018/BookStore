package com.kzx.bookstore.user.web.servlet;

import cn.itcast.commons.CommonUtils;
import cn.itcast.servlet.BaseServlet;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.kzx.bookstore.user.domain.User;
import com.kzx.bookstore.user.service.UserService;
import com.kzx.bookstore.user.service.exception.UserException;

@WebServlet("/UserServlet")
public class UserServlet extends BaseServlet {
	private static final long serialVersionUID = 1L;

	private UserService userService = new UserService();
	
	
	public String ajaxValidateLoginname(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String loginname = req.getParameter("loginname");
		boolean b = userService.ajaxValidateLoginname(loginname);
		resp.getWriter().print(b);
		return null;
	}
	
	public String ajaxValidateEmail(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String email = req.getParameter("email");
		boolean b = userService.ajaxValidateEmail(email);
		resp.getWriter().print(b);
		return null;
	}
	
	public String ajaxValidateVerifyCode(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String verifyCode = req.getParameter("verifyCode");
		String vCode = (String)req.getSession().getAttribute("vCode");
		boolean b = verifyCode.equalsIgnoreCase(vCode);
		resp.getWriter().print(b);
		return null;
	}
	
	public String regist(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		/*
		 * 1. 封装表单数据到User对象
		 */
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		/*
		 * 2. 校验之, 如果校验失败，保存错误信息，返回到regist.jsp显示
		 */
		Map<String ,String> errors = validateRegist(formUser,req.getSession());
		if(errors.size()>0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/regist.jsp";
		}
		/*
		 * 3. 使用service完成业务
		 */
		userService.regist(formUser);
		/*
		 * 4. 保存成功信息，转发到msg.jsp显示！
		 */
		
		req.setAttribute("code", "success");
		req.setAttribute("msg", "注册功能，请马上到邮箱激活！");
		return "f:/jsps/msg.jsp";
	}
	
	private Map<String,String> validateRegist(User formUser,HttpSession session){
		Map<String,String> errors = new HashMap<String,String>();
		String loginname = formUser.getLoginname();
		if(loginname==null||loginname.trim().isEmpty()) {
			errors.put("loginname","用户名不能为空！");
		}
		else if(loginname.length()<3||loginname.length()>20){
			errors.put("loginname","用户名长度必须在3~20之间！");
		}
		else if(!userService.ajaxValidateLoginname(loginname))	{
			errors.put("loginname","用户名已被注册！");
		}
			
		String loginpass = formUser.getLoginpass();
		if(loginpass==null||loginname.trim().isEmpty()) {
			errors.put("loginpass","密码不能为空！");
		}
		else if(loginpass.length()<3||loginpass.length()>20){
			errors.put("loginpass","密码长度必须在3~20之间！");
		}
		
		String reloginpass = formUser.getReloginpass();
		if(reloginpass==null||reloginpass.trim().isEmpty()) {
			errors.put("reloginpass","确认密码不能为空！");
		}
		else if(!reloginpass.equals(loginpass)){
			errors.put("reloginpass","两次输入不一致！");
		}
		
		String email = formUser.getEmail();
		if(email==null||email.trim().isEmpty()) {
			errors.put("email","Email不能为空！");
		}
		else if(!email.matches("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$")){
			errors.put("email","Email格式错误！");
		}
		else if(!userService.ajaxValidateEmail(email))	{
			errors.put("email","Email已被注册！");
		}
		
		String verifyCode = formUser.getVerifyCode();
		String vcode = (String)session.getAttribute("vCode");
		if(reloginpass==null||verifyCode.trim().isEmpty()) {
			errors.put("verifyCode","验证码不能为空！");
		}
		else if(!verifyCode.equalsIgnoreCase(vcode)){
			errors.put("verifyCode","验证码错误！");
		}
		
		return errors;
	}
	
	
	public String activation(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String code = req.getParameter("activationCode");
		try {
			userService.activation(code);
			req.setAttribute("code", "success");
			req.setAttribute("msg", "恭喜，激活成功，请马上登录！");
		} catch (UserException e) {
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("code", "error");
		}
		return "f:/jsps/msg.jsp";
	}
	
	public String login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		Map<String ,String> errors = validateLogin(formUser,req.getSession());
		if(errors.size()>0) {
			req.setAttribute("form", formUser);
			req.setAttribute("errors", errors);
			return "f:/jsps/user/login.jsp";
		} 
		User user = userService.login(formUser);
		if(user==null) {
			req.setAttribute("msg","用户名或密码错误！");
			req.setAttribute("user", formUser);
			return "f:/jsps/user/login.jsp";
		}
		else {
			if(!user.isStatus()) {
				req.setAttribute("msg","您还没有激活！");
				req.setAttribute("user", formUser);
				return "f:/jsps/user/login.jsp";
			}
			else {
				req.getSession().setAttribute("sessionUser",user);
				String loginname = user.getLoginname();
				loginname = URLEncoder.encode(loginname,"utf-8");
				Cookie cookie = new Cookie("loginname",loginname);
				cookie.setMaxAge(60*60*24*10);
				resp.addCookie(cookie);
				return "r:/index.jsp";
			}
		}
	}
 
	
	private Map<String,String> validateLogin(User formUser,HttpSession session){
		Map<String,String> errors = new HashMap<String,String>();
		
		return errors;
	}
	
	
	public String updatePassword(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		User formUser = CommonUtils.toBean(req.getParameterMap(), User.class);
		User user = (User)req.getSession().getAttribute("sessionUser");
		if(user ==null) {
			req.setAttribute("msg", "您还没有登录！");
			return "f:/jsps/user/login.jsp";
		}
		try {
			userService.updatePassword(user.getUid(), formUser.getNewpass(), formUser.getLoginpass());
			req.setAttribute("msg", "修改密码成功");
			req.setAttribute("code", "success");
			return "f:/jsps/msg.jsp";
		} catch (UserException e) {
			// TODO Auto-generated catch block
			req.setAttribute("msg", e.getMessage());
			req.setAttribute("user", formUser);
			return "f:/jsps/user/pwd.jsp";
		}
	}
	
	public String quit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException{
		req.getSession().invalidate();
		return "r:/jsps/user/login.jsp";
	}
}
