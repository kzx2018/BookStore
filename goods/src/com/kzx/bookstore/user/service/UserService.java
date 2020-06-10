package com.kzx.bookstore.user.service;

import java.io.IOException;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;

import com.kzx.bookstore.user.dao.UserDao;
import com.kzx.bookstore.user.domain.User;
import com.kzx.bookstore.user.service.exception.UserException;

import cn.itcast.commons.CommonUtils;
import cn.itcast.mail.Mail;
import cn.itcast.mail.MailUtils;

/**
 * @author kangzhaoxiang
 *
 */
public class UserService {
	private UserDao userDao = new UserDao();
	
	public void updatePassword(String uid,String newPass,String oldPass) throws UserException {
		try {
			boolean bool = userDao.findByUidAndPassword(uid, oldPass);
			if(!bool) {
				throw new UserException("老密码错误！");
			}
			userDao.updatePassword(uid, newPass);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public User login(User user) {
		try {
			return userDao.findByLoginnameAndLoginPass(user.getLoginname(), user.getLoginpass());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void activation(String code) throws UserException {
		try {
			User user = userDao.findByCode(code);
			if(user==null) {
				throw new UserException("无效的激活码！");
			}
			if(user.isStatus()) {
				throw new UserException("您已经激活过了，不要二次激活！");
			}
			userDao.updateStatus(user.getUid(), true);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean ajaxValidateLoginname(String loginname) {
		try {
			return userDao.ajaxValidateLoginname(loginname);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public boolean ajaxValidateEmail(String email) {
		try {
			return userDao.ajaxValidateEmail(email);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void regist(User user) {
		//1. 数据的补齐
		user.setUid(CommonUtils.uuid());
		user.setStatus(false);
		user.setActivationCode(CommonUtils.uuid()+CommonUtils.uuid());
		
		//2. 向数据库插入
		try {
			userDao.add(user);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
		/*
		 * 3. 发邮件
		 */
		/*
		 * 把配置文件内容加载到prop中
		 */
		Properties prop = new Properties();
		try {
			prop.load(this.getClass().getClassLoader().getResourceAsStream("email_template.properties"));
		} catch (IOException e1) {
			throw new RuntimeException(e1);
		}
		/*
		 * 登录邮件服务器，得到session
		 */
		String host=prop.getProperty("host");
		String name=prop.getProperty("username");
		String pass=prop.getProperty("password");
		Session session = MailUtils.createSession(host,name,pass);
		/*
		 * 创建Mail对象
		 */
		String from =prop.getProperty("from");
		String to =user.getEmail();
		String subject = prop.getProperty("subject");
		String content = MessageFormat.format(prop.getProperty("content"),user.getActivationCode());
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
	
}
