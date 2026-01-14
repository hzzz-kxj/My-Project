package cn.edu.zut.collect_school.mapper;

import cn.edu.zut.collect_school.pojo.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {

    // 插入用户
    @Insert("INSERT INTO user (username, password) VALUES (#{username}, #{password})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);

    // 根据用户名查询用户
    @Select("SELECT id, username, password FROM user WHERE username = #{username}")
    Optional<User> selectByUsername(@Param("username") String username);

    // 根据ID查询用户
    @Select("SELECT id, username, password FROM user WHERE id = #{id}")
    Optional<User> selectById(@Param("id") Long id);

}