package com.ruixi.bigevent.service;

import com.ruixi.bigevent.pojo.Category;

import java.util.List;

public interface CategoryService {

    //新增分类
    void add(Category category);

    //查询所有分类
    List<Category> list();

    //根据ID查询分类
    Category findById(Integer id);

    //修改分类
    void update(Category category);

    //删除分类
    void delete(Integer id);
}
