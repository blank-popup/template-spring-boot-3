package com.example.template.setting.security;

import com.example.template.setting.common.user.EntityUser;
import com.example.template.setting.common.user.EntityRole;
import com.example.template.setting.common.user.EntityUserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RepositorySecurity {
    List<EntityUser> selectUserDetailsByUsername(String username);
    List<EntityUser> selectUserDetailsById(Long id);
    List<EntityRole> selectAllRole();
    List<EntityRole> selectRolesByUsername(String username);
    List<EntityRole> selectRolesById(Long id);
    int createUser(AhaAuth.SigningUp signingUp);
    int createUserRole(List<EntityUserRole> entityUserRole);
}
