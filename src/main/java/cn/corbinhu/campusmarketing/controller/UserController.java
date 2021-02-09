package cn.corbinhu.campusmarketing.controller;

import cn.corbinhu.campusmarketing.controller.annotation.LoginRequired;
import cn.corbinhu.campusmarketing.entity.Role;
import cn.corbinhu.campusmarketing.entity.User;
import cn.corbinhu.campusmarketing.service.RoleService;
import cn.corbinhu.campusmarketing.service.UserService;
import cn.corbinhu.campusmarketing.utils.*;
import cn.corbinhu.campusmarketing.utils.page.Page;
import cn.corbinhu.campusmarketing.utils.page.TableDataInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author: Corbinhu
 * @description:
 */
@Controller
@RequestMapping("/user")
public class UserController implements CMarketingConstant {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private HostHolder hostHolder;

    /**
     * 个人信息
     */
    @LoginRequired
    @GetMapping("/profile")
    public String profile(ModelMap mmap) {
        User user = hostHolder.getUser();
        mmap.put("user", user);
        return "user/profile";
    }

    /**
     * 修改用户
     */
    @LoginRequired
    @PostMapping("/profile/update")
    @ResponseBody
    public AjaxResult update(User user) {
        User currentUser = hostHolder.getUser();
        currentUser.setUserName(user.getUserName());
        currentUser.setTelephone(user.getTelephone());
        currentUser.setCity(user.getCity());
        currentUser.setProvince(user.getProvince());
        int rows = userService.updateUser(currentUser);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @PostMapping("/profile/checkOldPassword")
    @ResponseBody
    @LoginRequired
    public boolean checkOldPassword(String oldPassword) {
        if (StringUtils.isNull(oldPassword)) {
            throw new IllegalArgumentException("旧密码不能为空!");
        }
        User user = hostHolder.getUser();
        oldPassword = CMarketingUtil.md5(oldPassword + user.getSalt());
        if (oldPassword.equals(user.getPassword())) {
            return true;
        }
        return false;
    }

    @PostMapping("/profile/checkNewPassword")
    @ResponseBody
    @LoginRequired
    public boolean checkNewPassword(String newPassword) {
        if (StringUtils.isNull(newPassword)) {
            throw new IllegalArgumentException("新密码不能为空!");
        }
        return CheckPassword.checkPasswordRule(newPassword);
    }

    @LoginRequired
    @GetMapping("/profile/resetPwd")
    public String resetPwd(ModelMap mmap) {
        User user = hostHolder.getUser();
        mmap.put("user", userService.findUserById(user.getId()));
        return "user/resetPwd";
    }
    @PostMapping("/profile/resetPwd")
    @ResponseBody
    @LoginRequired
    public AjaxResult resetPwd(String oldPassword, String newPassword) {
        if (StringUtils.isNull(oldPassword) || StringUtils.isNull(newPassword)) {
            throw new IllegalArgumentException("参数不能为空！");
        }
        User user = hostHolder.getUser();
        oldPassword = CMarketingUtil.md5(oldPassword + user.getSalt());
        if (oldPassword.equals(user.getPassword())) {
            user.setPassword(CMarketingUtil.md5(newPassword + user.getSalt()));
            int rows = userService.updateUser(user);
            return rows > 0 ? AjaxResult.success() : AjaxResult.error();
        }
        return AjaxResult.error();
    }

    @LoginRequired
    @GetMapping()
    public String user(Model model) {
        model.addAttribute("user", hostHolder.getUser());
        return "user/user";
    }

    @LoginRequired
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo list(User user) {
        Page page = new Page();
        PageHelper.startPage(page.getCurrent(), page.getLimit());
        List<User> list = userService.findUserList(user);
        //long userTotal = userService.findUserCount();
        for (User u : list) {
            Role role = roleService.findRoleById(u.getRoleId());
            u.setRoleName(role.getName());
        }
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(0);
        dataInfo.setTotal(new PageInfo(list).getTotal());
        dataInfo.setRows(list);
        return dataInfo;
    }

    @LoginRequired
    @GetMapping("/add")
    public String add(ModelMap modelMap) {
        modelMap.put("loginUser", hostHolder.getUser());
        return "user/add";
    }

    @LoginRequired
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated User user) {
        if (LOGIN_NAME_EXIST.equals(userService.checkLoginNameUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getLoginName() + "'失败，登录账号已存在");
        } else if (USER_PHONE_EXIST.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("新增用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        int rows = userService.insertUser(user);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @LoginRequired
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        try {
            User user = userService.findUserById(Integer.parseInt(ids));
            userService.checkUserAllowed(user);
            int rows = userService.removeUserById(Integer.parseInt(ids));
            return rows > 0 ? AjaxResult.success() : AjaxResult.error();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("/changeStatus")
    @ResponseBody
    public AjaxResult changeStatus(User user) {
        userService.checkUserAllowed(user);
        user.setUpdateBy(hostHolder.getUser().getLoginName());
        int rows = userService.changeStatus(user);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 修改用户
     */
    @LoginRequired
    @GetMapping("/edit/{userId}")
    public String edit(@PathVariable("userId") int userId, ModelMap mmap) {
        User user = userService.findUserById(userId);
        User loginUser = hostHolder.getUser();
        Map<String, Boolean> editRoleFlag = new HashMap<>();
        if (loginUser.getRoleId() == 1){
            if (user.getRoleId() == 1)
                editRoleFlag.put("adminEdit", false);
            else {
                editRoleFlag.put("adminEdit", true);
                editRoleFlag.put("ProvinceEdit", true);
            }
        }else if (loginUser.getRoleId() == 2){
            editRoleFlag.put("adminEdit", true);
            if (user.getRoleId() == 1){
                editRoleFlag.put("ProvinceEdit", false);
            }else {
                editRoleFlag.put("ProvinceEdit", true);
            }
        }
        mmap.put("editRoleFlag",editRoleFlag);
        mmap.put("user", user);
        return "user/edit";
    }

    /**
     * 修改保存用户
     */
    @LoginRequired
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated User user) {
        try {
            userService.checkUserAllowed(user);
        }catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
        if (LOGIN_NAME_EXIST.equals(userService.checkLoginNameUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getLoginName() + "'失败，OA帐号已存在");
        } else if (USER_PHONE_EXIST.equals(userService.checkPhoneUnique(user))) {
            return AjaxResult.error("修改用户'" + user.getLoginName() + "'失败，手机号码已存在");
        }
        int rows = userService.updateUser(user);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    /**
     * 校验用户名
     */
    @LoginRequired
    @PostMapping("/checkLoginNameUnique")
    @ResponseBody
    public String checkLoginNameUnique(User user) {
        return userService.checkLoginNameUnique(user);
    }

    /**
     * 校验手机号码
     */
    @LoginRequired
    @PostMapping("/checkPhoneUnique")
    @ResponseBody
    public String checkPhoneUnique(User user) {
        return userService.checkPhoneUnique(user);
    }

}
