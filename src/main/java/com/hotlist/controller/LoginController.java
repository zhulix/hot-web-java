package com.hotlist.controller;

import com.hotlist.common.R;
import com.hotlist.common.dto.LoginDto;
import com.hotlist.common.vo.UserVo;
import com.hotlist.entity.UserEntity;
import com.hotlist.service.UserService;
import com.hotlist.utils.HotContext;
import com.hotlist.utils.HotCookie;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
public class LoginController {

    @Resource
    private HotCookie hotCookie;

    @Resource
    private UserService userService;

    @PostMapping("/login")
    public R login(@RequestBody LoginDto loginDto) {
        UserEntity userEntity = userService.selectByUserName(loginDto.getUsername());

        boolean matches = new BCryptPasswordEncoder().matches(loginDto.getPassword(), userEntity.getPassword());
        Map<String, String> map = new HashMap<>(1);
        if (matches) {
            map.put("token", hotCookie.createToken(userEntity));
        }

        return matches ? R.ok().put("data", map) : R.error();
    }

    @PostMapping("/reg")
    public R reg(@RequestBody LoginDto loginDto) {
        return userService.save(new UserEntity().setUserName(loginDto.getUsername()).setPassword(loginDto.getPassword())) ? R.ok() : R.error();
    }





}
