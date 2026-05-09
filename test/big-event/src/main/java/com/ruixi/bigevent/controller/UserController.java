package com.ruixi.bigevent.controller;


import com.ruixi.bigevent.pojo.Result;
import com.ruixi.bigevent.pojo.User;
import com.ruixi.bigevent.service.UserService;
import com.ruixi.bigevent.utils.JwtUtil;
import com.ruixi.bigevent.utils.Md5Util;
import com.ruixi.bigevent.utils.ThreadLocalUtil;
import jakarta.validation.constraints.Pattern;
import jakarta.websocket.server.PathParam;
import org.apache.tomcat.util.http.parser.Authorization;
import org.hibernate.validator.constraints.URL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //注册
    @PostMapping("/register")
    public Result register(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){

        //查询用户
        User u = userService.findByUserName(username);
        if(u == null){
            //没有占用
            //注册
            userService.register(username,password);
            return Result.success();
        }else {
            //被占用
            return Result.error("用户名被占用");
        }

    }

    //登录
    @PostMapping("/login")
    public Result<String> login(@Pattern(regexp = "^\\S{5,16}$") String username, @Pattern(regexp = "^\\S{5,16}$") String password){
        //根据用户名查询用户
        User loginUser = userService.findByUserName(username);

        //判断用户是否存在
        if(loginUser == null){
            return Result.error("用户名不存在");
        }
        //判断密码是否正确 loginUser对象的password是密文的
        if (Md5Util.getMD5String(password).equals(loginUser.getPassword())){
            //登录成功
            Map<String, Object> claims = new HashMap<>();

            /*将业务数据id和username装入令牌*/
            claims.put("id",loginUser.getId());
            claims.put("username",loginUser.getUsername());
            String token = JwtUtil.genToken(claims);

            /*把token存储到Redis中*/
            ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
            operations.set(token,"valid",12, TimeUnit.HOURS);

            return Result.success(token);
        }
        return Result.error("密码错误");
    }

    //获取用户信息
    @GetMapping("/userInfo")
    public Result<User> userInfo(/*@RequestHeader(name = "Authorization") String token*/){
        /*Map<String, Object> map = JwtUtil.parseToken(token);
        String username = (String) map.get("username");*/

        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User user = userService.findByUserName(username);
        return Result.success(user);
    }

    //更新用户信息
    @PutMapping("/update")
    public Result update(@RequestBody @Validated User user){
        userService.update(user);
        return Result.success();
    }

    //更新用户头像
    @PatchMapping("/updateAvatar")
    public Result updateAvatar(@RequestParam @URL String avatarUrl){
        userService.updateAvatar(avatarUrl);
        return Result.success();
    }

    //更新用户密码
    @PatchMapping("/updatePwd")
    public Result updatePwd(@RequestBody Map<String, String> params,@RequestHeader("Authorization") String token){
        //校验参数
        String oldPwd = params.get("old_pwd");
        String newPwd = params.get("new_pwd");
        String rePwd = params.get("re_pwd");

        if(!StringUtils.hasLength(oldPwd) || !StringUtils.hasLength(newPwd) || !StringUtils.hasLength(rePwd)){

            return Result.error("缺少必要参数");

        }

        //校验密码格式（5-16位非空白字符）
        if(!oldPwd.matches("^\\S{5,16}$")){
            return Result.error("原密码格式不正确，应为5-16位非空白字符");
        }
        if(!newPwd.matches("^\\S{5,16}$")){
            return Result.error("新密码格式不正确，应为5-16位非空白字符");
        }
        if(!rePwd.matches("^\\S{5,16}$")){
            return Result.error("确认密码格式不正确，应为5-16位非空白字符");
        }

        //原密码是否正确
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        User loginUser = userService.findByUserName(username);
        if(!(loginUser.getPassword().equals(Md5Util.getMD5String(oldPwd)))) {
            return Result.error("原密码错误");
        }
        //新密码是否一致
        if(!newPwd.equals(rePwd)){
            return Result.error("两次密码不一致");
        }
        //修改密码
        userService.updatePwd(newPwd);

        //删除Redis缓存的令牌
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);

        return Result.success();

    }
}
