package cn.edu.zut.collect_school.service;

import cn.edu.zut.collect_school.mapper.UserMapper;
import cn.edu.zut.collect_school.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册用户
     */
    public User register(User user) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userMapper.selectByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("用户名已存在");
        }

        // 插入用户
        int result = userMapper.insert(user);
        if (result == 0) {
            throw new RuntimeException("注册失败");
        }

        // 返回注册的用户信息
        return userMapper.selectByUsername(user.getUsername())
                .orElseThrow(() -> new RuntimeException("注册失败"));
    }

    /**
     * 用户登录
     */
    public User login(String username, String password) {
        Optional<User> user = userMapper.selectByUsername(username);
        if (user.isPresent() && user.get().getPassword().equals(password)) {
            return user.get();
        }
        throw new RuntimeException("用户名或密码错误");
    }

    /**
     * 检查用户名是否存在
     */
    public boolean checkUsernameExists(String username) {
        return userMapper.selectByUsername(username).isPresent();
    }

    /**
     * 根据ID获取用户
     */
    public Optional<User> getUserById(Long id) {
        return userMapper.selectById(id);
    }
}