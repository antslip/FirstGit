package com.ruixi.bigevent.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.ruixi.bigevent.mapper.ArticleMapper;
import com.ruixi.bigevent.pojo.Article;
import com.ruixi.bigevent.pojo.PageBean;
import com.ruixi.bigevent.service.ArticleService;
import com.ruixi.bigevent.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Override
    public PageBean<Article> list(Integer pageNum, Integer pageSize, Integer categoryId, String state) {
        //1.建PageBean对象
        PageBean<Article> pb = new PageBean<>();

        //2.开启分页查询 PageHelper
        PageHelper.startPage(pageNum,pageSize);

        //3.调用持久层方法
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");
        List<Article> as = articleMapper.list(userId,categoryId,state);
        Page<Article> p = (Page<Article>) as;
        pb.setTotal(p.getTotal());
        pb.setItems(p.getResult());
        return pb;
    }

    @Override
    public Article detail(Integer id) {
        Article a = articleMapper.detail(id);
        return a;
    }

    @Override
    public void update(Article article) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");
        Article a = articleMapper.detail(article.getId());
        if (a == null){
            throw new RuntimeException("文章不存在");
        }
        if (!loginId.equals(article.getCreateUser())){
            throw new RuntimeException("没有权限");
        }
        article.setUpdateTime(LocalDateTime.now());
        articleMapper.update(article);
    }

    @Override
    public void delete(Integer id) {
        Map<String, Object> map = ThreadLocalUtil.get();
        Integer loginId = (Integer) map.get("id");
        Article article = articleMapper.detail(id);

        if (article == null){
            throw new RuntimeException("文章不存在");
        }
        if (!loginId.equals(article.getCreateUser())){
            throw new RuntimeException("没有权限");
        }
        articleMapper.delete(id);
    }

    @Override
    public void add(Article article) {
        //补充属性值
        article.setCreateTime(LocalDateTime.now());
        article.setUpdateTime(LocalDateTime.now());

        Map<String, Object> map = ThreadLocalUtil.get();
        Integer userId = (Integer) map.get("id");

        article.setCreateUser(userId);


        articleMapper.add(article);
    }
}
