package com.example.tilehelper.servie.impl;

import com.example.tilehelper.entity.SysUser;
import com.example.tilehelper.mapper.SysUserMapper;
import com.example.tilehelper.servie.SysUserService;
import com.example.tilehelper.util.JwtUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class SysUserServiceImpl implements SysUserService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 1;

    private final SysUserMapper sysUserMapper;
    private final JwtUtil jwtUtil;

    public SysUserServiceImpl(SysUserMapper sysUserMapper, JwtUtil jwtUtil) {
        this.sysUserMapper = sysUserMapper;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String login(String username, String password) {
        SysUser user = sysUserMapper.getByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (user.getLockedUntil() != null && user.getLockedUntil().isAfter(LocalDateTime.now())) {
            long remainingSeconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), user.getLockedUntil());
            throw new RuntimeException("账户已被锁定，请" + remainingSeconds + "秒后再试");
        }

        if (!password.equals(user.getPassword())) {
            int newFailedAttempts = (user.getFailedAttempts() == null ? 0 : user.getFailedAttempts()) + 1;
            if (newFailedAttempts >= MAX_FAILED_ATTEMPTS) {
                sysUserMapper.updateLoginFailure(username, 0,
                        LocalDateTime.now().plusMinutes(LOCK_MINUTES));
                throw new RuntimeException("密码错误次数过多，账户已被锁定" + LOCK_MINUTES + "分钟");
            } else {
                sysUserMapper.updateLoginFailure(username, newFailedAttempts, null);
                throw new RuntimeException("用户名或密码错误");
            }
        }

        sysUserMapper.resetLoginFailure(username);
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }

    @Override
    public SysUser getByUsername(String username) {
        return sysUserMapper.getByUsername(username);
    }

    @Override
    public SysUser getById(Long id) {
        return sysUserMapper.getById(id);
    }
}
