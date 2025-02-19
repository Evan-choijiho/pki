package com.peloton.boilerplate.service;

import com.peloton.boilerplate.db.dao.UserRepository;
import com.peloton.boilerplate.model.dto.response.UserDto;
import com.peloton.boilerplate.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AccessService {

    @Autowired
    UserRepository userRepository;

    public UserDto getUserBySid(Long userSid) {
        User user = userRepository	.findBySidAndDeleteTimeIsNull(userSid);
        UserDto userDto = new UserDto(user);
        return userDto;
    }
}
