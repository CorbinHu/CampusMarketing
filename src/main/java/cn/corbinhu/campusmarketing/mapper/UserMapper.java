package cn.corbinhu.campusmarketing.mapper;

import cn.corbinhu.campusmarketing.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: Corbinhu
 * @description:
 */

@Mapper
public interface UserMapper {
    List<User> selectUserList(User user);

    User selectById(int id);

    User selectByLoginName(String loginName);

    User selectByTelephone(String telephone);

    int insertUser(User user);

    int selectUserCount();

    int updateUser(User user);

    int updatePassword(int id, String password);

    int deleteUserById(int id);
}
