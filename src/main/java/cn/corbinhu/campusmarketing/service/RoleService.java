package cn.corbinhu.campusmarketing.service;

import cn.corbinhu.campusmarketing.entity.Role;
import cn.corbinhu.campusmarketing.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author: Corbinhu
 * @description:
 */
@Component
public class RoleService {

    @Autowired
    private RoleMapper roleMapper;

    public Role findRoleById(int id){
        return roleMapper.selectRoleById(id);
    }

    public List<Role> findRoleList(){
        return roleMapper.selectRoleList();
    }
}
