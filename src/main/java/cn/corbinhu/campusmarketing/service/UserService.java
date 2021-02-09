package cn.corbinhu.campusmarketing.service;

import cn.corbinhu.campusmarketing.entity.LoginTicket;
import cn.corbinhu.campusmarketing.entity.User;
import cn.corbinhu.campusmarketing.mapper.UserMapper;
import cn.corbinhu.campusmarketing.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author: Corbinhu
 * @description:
 */

@Service
public class UserService implements CMarketingConstant {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private HostHolder hostHolder;

    public List<User> findUserList(User user) {
        return userMapper.selectUserList(user);
    }

    public int findUserCount() {
        return userMapper.selectUserCount();
    }

    public User findUserById(int id) {
        User user = getUserByCache(id);
        if (user == null) {
            user = initUserCache(id);
        }
        return user;
    }

    /**
     * 插入用户
     *
     * @param user
     * @return
     */
    public int insertUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        // 添加用户
        user.setSalt(CMarketingUtil.generateUUID().substring(0, 5));
        user.setPassword(CMarketingUtil.md5(user.getTelephone() + user.getSalt()));
        user.setCreateTime(new Date());
        user.setCreateBy(hostHolder.getUser().getUserName());
        user.setUpdateTime(user.getCreateTime());
        user.setUpdateBy(hostHolder.getUser().getUserName());
        int rows = userMapper.insertUser(user);
        return rows;
    }

    public String checkLoginNameUnique(User user) {
        User u = userMapper.selectByLoginName(user.getLoginName());
        if (u != null && u.getId() != user.getId()) {
            return LOGIN_NAME_EXIST;
        }
        return LOGIN_NAME_NOT_EXIST;
    }

    public String checkPhoneUnique(User user) {
        User u = userMapper.selectByTelephone(user.getTelephone());
        if (u != null && u.getId() != user.getId()) {
            return USER_PHONE_EXIST;
        }
        return USER_PHONE_NOT_EXIST;
    }

    /**
     * 登录
     *
     * @param loginName
     * @param password
     * @return
     */
    public Map<String, Object> login(String loginName, String password, int expiredSeconds) {
        Map<String, Object> map = new HashMap<>();

        // 空值处理
        if (StringUtils.isBlank(loginName)) {
            map.put("loginMsg", "账号不能为空!");
            return map;
        }
        if (StringUtils.isBlank(password)) {
            map.put("passwordMsg", "密码不能为空!");
            return map;
        }
        // 验证账号
        User user = userMapper.selectByLoginName(loginName);
        if (user == null) {
            map.put("oaMsg", "该账号不存在!");
            return map;
        }

        // 验证状态
        if (user.getStatus() == USER_INACTIVE) {
            map.put("oaMsg", "该账号已弃用!");
            return map;
        }

        // 验证密码
        password = CMarketingUtil.md5(password + user.getSalt());
        if (!user.getPassword().equals(password)) {
            map.put("passwordMsg", "密码不正确!");
            return map;
        }

        // 生成登录凭证
        LoginTicket loginTicket = new LoginTicket();
        loginTicket.setUserId(user.getId());
        loginTicket.setTicket(CMarketingUtil.generateUUID());
        loginTicket.setStatus(TICKET_VALID_STATUS);
        loginTicket.setExpired(new Date(System.currentTimeMillis() + expiredSeconds * 1000L));
        String loginTicketKey = RedisKeyUtil.getTicketKey(loginTicket.getTicket());
        redisTemplate.opsForValue().set(loginTicketKey, loginTicket);
        map.put("ticket", loginTicket.getTicket());
        return map;
    }

    public boolean checkUserIsFirstLogin(User user){
        // 验证是否是第一次登录
        String defaultPassword = CMarketingUtil.md5(user.getTelephone() + user.getSalt());
        return user.getPassword().equals(defaultPassword);
    }

    public int removeUserById(int id) {
        int rows = userMapper.deleteUserById(id);
        clearCache(id);
        return rows;
    }

    /**
     * 校验用户是否允许操作
     *
     * @param user 用户信息
     */
    public void checkUserAllowed(User user) {
        if (user != null && user.getRoleId() == 1) {
            throw new RuntimeException("不允许操作超级管理员用户");
        }
    }

    public int updateUser(User user) {
        if (user == null) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        user.setUpdateBy(hostHolder.getUser().getLoginName());
        int rows = userMapper.updateUser(user);
        clearCache(user.getId());
        return rows;
    }

    /**
     * 用户状态修改
     *
     * @param user 用户信息
     * @return 结果
     */
    public int changeStatus(User user) {
        user.setUpdateBy(hostHolder.getUser().getLoginName());
        int rows = userMapper.updateUser(user);
        clearCache(user.getId());
        return rows;
    }

    // 注销
    public void logout(String ticket) {
        String loginTicketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(loginTicketKey);
        loginTicket.setStatus(TICKET_INVALID_STATUS);
        redisTemplate.opsForValue().set(loginTicketKey, loginTicket);
    }

    // 根据ticket查询登录凭证
    public LoginTicket findLoginTicket(String ticket) {
        String loginTicketKey = RedisKeyUtil.getTicketKey(ticket);
        LoginTicket loginTicket = (LoginTicket) redisTemplate.opsForValue().get(loginTicketKey);
        return loginTicket;
    }

    // 优先从缓存中取值
    private User getUserByCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        return (User) redisTemplate.opsForValue().get(userKey);
    }

    // 取不到时初始化缓存数据
    private User initUserCache(int userId) {
        User user = userMapper.selectById(userId);
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.opsForValue().set(userKey, user, 3600, TimeUnit.SECONDS);
        return user;
    }

    // 数据变更时清除缓存数据
    private void clearCache(int userId) {
        String userKey = RedisKeyUtil.getUserKey(userId);
        redisTemplate.delete(userKey);
    }

}
