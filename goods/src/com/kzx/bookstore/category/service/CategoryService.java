package com.kzx.bookstore.category.service;

import java.sql.SQLException;
import java.util.List;

import javax.management.RuntimeErrorException;

import com.kzx.bookstore.category.dao.CategoryDao;
import com.kzx.bookstore.category.domain.Category;

public class CategoryService {
	private CategoryDao categoryDao = new CategoryDao();
	
	/**
	 * 查询指定父分类下子分类的个数
	 * @param pid
	 * @return
	 */
	public List<Category> findChildren(String pid){
		try {
			return categoryDao.findByParent(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public Category load(String cid) {
		try {
			return categoryDao.load(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void add(Category category) {
		try {
			categoryDao.add(category);;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void edit(Category category) {
		try {
			categoryDao.edit(category);;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int findChildCountByParent(String pid) {
		try {
			return categoryDao.findChildCountByParent(pid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void delete(String cid) {
		try {
			categoryDao.delete(cid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Category> findAll() {
		try {
			return categoryDao.findAll();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Category> findParents() {
		try {
			return categoryDao.findParents();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
