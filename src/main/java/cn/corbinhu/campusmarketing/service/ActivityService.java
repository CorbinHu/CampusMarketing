package cn.corbinhu.campusmarketing.service;

import cn.corbinhu.campusmarketing.entity.Activity;
import cn.corbinhu.campusmarketing.mapper.ActivityMapper;
import cn.corbinhu.campusmarketing.utils.CMarketingConstant;
import cn.corbinhu.campusmarketing.utils.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author: Corbinhu
 * @description:
 */
@Service
public class ActivityService implements CMarketingConstant {

    @Autowired
    private ActivityMapper activityMapper;

    @Autowired
    private HostHolder hostHolder;

    public List<Activity> findActivityList(Activity activity){
        List<Activity> activities = activityMapper.selectActivityList(activity);
        for (Activity act : activities) {
            act = addStatusName(act);
        }
        return activities;
    }

    public Activity findActivityById(int id){
        Activity activity = activityMapper.selectActivityById(id);
        if (activity != null) {
           return addStatusName(activity);
        }
        return null;
    }

    public Activity findActivityByName(String name){
        return activityMapper.selectByActivityName(name);
    }

    public int insertActivity(Activity activity){
        if (activity == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        activity.setStatus(ACTIVITY_NOT_ACCEPT);
        activity.setCreateBy(hostHolder.getUser().getLoginName());
        activity.setStatusName("未接受");
        activity.setCreateTime(new Date());
        activity.setUpdateBy(activity.getCreateBy());
        activity.setUpdateTime(activity.getCreateTime());
        return activityMapper.insertActivity(activity);
    }

    public int updateActivity(Activity activity){
        if (activity == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        activity.setUpdateBy(hostHolder.getUser().getLoginName());
        return activityMapper.updateActivity(activity);
    }

    public int acceptActivity(int id){
        Activity activity = findActivityById(id);
        activity.setStatus(ACTIVITY_ACCEPT);
        activity.setAcceptName(hostHolder.getUser().getLoginName());
        activity.setAcceptTime(new Date());
        return activityMapper.updateActivity(activity);
    }

    public int removeActivity(int id){
        return activityMapper.deleteActivityById(id);
    }

    public String checkActivityNameUnique(String name) {
        Activity activity = activityMapper.selectByActivityName(name);
        if (activity != null){
            return ACTIVITY_NAME_EXIST;
        }
        return ACTIVITY_NAME_NOT_EXIST;
    }

    private Activity addStatusName(Activity activity){
        if (activity.getStatus() == ACTIVITY_NOT_ACCEPT) {
            if (activity.getEndTime().before(new Date())) {
                activity.setStatus(ACTIVITY_EXPIRED);
                changeActivityStatus(activity);
            } else {
                activity.setStatusName("未接受");
            }
        } else if (activity.getStatus() == ACTIVITY_ACCEPT) {
            activity.setStatusName("已接收");
        } else if (activity.getStatus() == ACTIVITY_DELETED) {
            activity.setStatusName("已删除");
        }else if (activity.getStatus() == ACTIVITY_COMPLETED){
            activity.setStatusName("已完成");
        }else if (activity.getStatus()==ACTIVITY_EXPIRED){
            activity.setStatusName("已过期");
        }

        return activity;
    }

    private int changeActivityStatus(Activity activity){
        if (activity == null){
            throw new IllegalArgumentException("参数不能为空！");
        }
        return activityMapper.updateActivity(activity);
    }
}
