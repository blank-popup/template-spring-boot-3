package com.example.template.setting.security;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MapperSecurity {
    Optional<AhaUserDetails> selectUserDetailsByUsername(String username);
    List<AhaAuth.Role> selectAllRole();
    List<AhaAuth.Role> selectRolesByUsername(String username);
    int createUser(AhaAuth.SigningUp signingUp);
}
