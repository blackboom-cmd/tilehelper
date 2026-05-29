package com.example.tilehelper.servie;

import com.example.tilehelper.entity.SysUser;

public interface SysUserService {

    String login(String username, String password);

    SysUser getByUsername(String username);

    SysUser getById(Long id);
}
