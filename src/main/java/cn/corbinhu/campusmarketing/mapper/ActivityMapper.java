package cn.corbinhu.campusmarketing.mapper;

import cn.corbinhu.campusmarketing.entity.Activity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: Corbinhu
 * @description:
 */
@Mapper
public interface ActivityMapper {
    List<Activity> selectActivityList(Activity activity);

    Activity selectActivityById(int id);

    Activity selectByActivityName(String activityName);

    int insertActivity(Activity activity);

    int selectActivityCount();

    int updateActivity(Activity activity);

    int deleteActivityById(int id);
}
