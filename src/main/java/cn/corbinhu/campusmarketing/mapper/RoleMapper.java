package cn.corbinhu.campusmarketing.mapper;

import cn.corbinhu.campusmarketing.entity.Role;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author: Corbinhu
 * @description:
 */

@Mapper
public interface RoleMapper {

    Role selectRoleByName(String name);

    Role selectRoleById(int id);

    List<Role> selectRoleList();
}
