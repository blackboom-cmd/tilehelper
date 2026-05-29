package com.example.tilehelper.mapper;

import com.example.tilehelper.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDateTime;

@Mapper
public interface SysUserMapper {

    @Select({
            "SELECT",
            "id,",
            "username,",
            "password,",
            "failed_attempts,",
            "locked_until",
            "FROM sys_user WHERE username = #{username} LIMIT 1"
    })
    @Results(id = "sysUserResultMap", value = {
            @Result(property = "id", column = "id"),
            @Result(property = "username", column = "username"),
            @Result(property = "password", column = "password"),
            @Result(property = "failedAttempts", column = "failed_attempts"),
            @Result(property = "lockedUntil", column = "locked_until")
    })
    SysUser getByUsername(@Param("username") String username);

    @Select({
            "SELECT",
            "id,",
            "username,",
            "password,",
            "failed_attempts,",
            "locked_until",
            "FROM sys_user WHERE id = #{id} LIMIT 1"
    })
    @ResultMap("sysUserResultMap")
    SysUser getById(@Param("id") Long id);

    @Update({
            "UPDATE sys_user",
            "SET failed_attempts = #{failedAttempts},",
            "locked_until = #{lockedUntil}",
            "WHERE username = #{username}"
    })
    int updateLoginFailure(@Param("username") String username,
                           @Param("failedAttempts") int failedAttempts,
                           @Param("lockedUntil") LocalDateTime lockedUntil);

    @Update({
            "UPDATE sys_user",
            "SET failed_attempts = 0,",
            "locked_until = NULL",
            "WHERE username = #{username}"
    })
    int resetLoginFailure(@Param("username") String username);
}
