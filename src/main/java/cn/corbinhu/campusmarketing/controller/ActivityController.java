package cn.corbinhu.campusmarketing.controller;

import cn.corbinhu.campusmarketing.controller.annotation.LoginRequired;
import cn.corbinhu.campusmarketing.entity.Activity;
import cn.corbinhu.campusmarketing.entity.User;
import cn.corbinhu.campusmarketing.service.ActivityService;
import cn.corbinhu.campusmarketing.utils.AjaxResult;
import cn.corbinhu.campusmarketing.utils.CMarketingConstant;
import cn.corbinhu.campusmarketing.utils.HostHolder;
import cn.corbinhu.campusmarketing.utils.page.Page;
import cn.corbinhu.campusmarketing.utils.page.TableDataInfo;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: Corbinhu
 * @description:
 */
@Controller
@RequestMapping("/activity")
public class ActivityController implements CMarketingConstant {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private HostHolder hostHolder;

    @LoginRequired
    @GetMapping()
    public String getActivityPage(ModelMap modelMap) {
        User user = hostHolder.getUser();
        modelMap.put("user", user);
        return "activity/activities";
    }

    @LoginRequired
    @PostMapping("/list")
    @ResponseBody
    public TableDataInfo activityList(Activity activity) {
        Page page = new Page();
        PageHelper.startPage(page.getCurrent(), page.getLimit());
        List<Activity> list = activityService.findActivityList(activity);
        TableDataInfo dataInfo = new TableDataInfo();
        dataInfo.setCode(0);
        dataInfo.setTotal(new PageInfo(list).getTotal());
        dataInfo.setRows(list);
        return dataInfo;
    }

    @LoginRequired
    @GetMapping("/add")
    public String getAddPage() {
        return "activity/add";
    }

    @LoginRequired
    @PostMapping("/add")
    @ResponseBody
    public AjaxResult addSave(@Validated Activity activity) {
        int rows = activityService.insertActivity(activity);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @LoginRequired
    @PostMapping("/remove")
    @ResponseBody
    public AjaxResult remove(String ids) {
        try {
            int rows = activityService.removeActivity(Integer.parseInt(ids));
            return rows > 0 ? AjaxResult.success() : AjaxResult.error();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @LoginRequired
    @PostMapping("/acceptActivity")
    @ResponseBody
    public AjaxResult accept(String ids) {
        try {
            int rows = activityService.acceptActivity(Integer.parseInt(ids));
            return rows > 0 ? AjaxResult.success() : AjaxResult.error();
        } catch (Exception e) {
            return AjaxResult.error(e.getMessage());
        }
    }

    @PostMapping("/checkActivityNameUnique")
    @ResponseBody
    @LoginRequired
    public String checkActivityNameUnique(Activity activity) {
        return activityService.checkActivityNameUnique(activity.getName());
    }

    @LoginRequired
    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, ModelMap modelMap) {
        Activity activity = activityService.findActivityById(id);
        modelMap.put("activity", activity);
        return "activity/edit";
    }

    @LoginRequired
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@Validated Activity activity) {
        Activity activityByName = activityService.findActivityByName(activity.getName());
        if (activityByName != null && activityByName.getId() != activity.getId() && activityByName.getName().equals(activity.getName())) {
            return AjaxResult.error("修改活动'" + activity.getName() + "'失败，该活动已存在");
        }
        int rows = activityService.updateActivity(activity);
        return rows > 0 ? AjaxResult.success() : AjaxResult.error();
    }

    @LoginRequired
    @GetMapping("/details/{id}")
    public String getActivityDetails(@PathVariable int id, ModelMap modelMap){
        Activity activity = activityService.findActivityById(id);
        modelMap.put("activity",activity);
        return "activity/activity-details";
    }




}
