package cn.edu.zut.collect_school.controller;

import cn.edu.zut.collect_school.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cn.edu.zut.collect_school.mapper.UserMapper;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserMapper userMapper;

    // 用户注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        try {
            // 检查用户名是否已存在
            Optional<User> existingUser = userMapper.selectByUsername(user.getUsername());
            if (existingUser.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("用户名已存在，请选择其他用户名");
            }

            // 插入新用户
            int result = userMapper.insert(user);
            if (result == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("注册失败");
            }

            // 获取新注册的用户信息
            User registeredUser = userMapper.selectByUsername(user.getUsername())
                    .orElseThrow(() -> new RuntimeException("注册失败"));

            return ResponseEntity.ok(registeredUser);

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("注册失败: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("服务器内部错误: " + e.getMessage());
        }
    }

    /**
     * 用户登录接口
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            // 查询用户
            Optional<User> existingUser = userMapper.selectByUsername(user.getUsername());

            if (existingUser.isPresent()) {
                User dbUser = existingUser.get();
                // 验证密码
                if (dbUser.getPassword().equals(user.getPassword())) {
                    return ResponseEntity.ok(dbUser);
                } else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                            .body("用户名或密码错误");
                }
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("用户名或密码错误");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("服务器内部错误: " + e.getMessage());
        }
    }

    // 根据ID获取用户
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            Optional<User> user = userMapper.selectById(id);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("用户不存在");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("获取用户失败: " + e.getMessage());
        }
    }
}