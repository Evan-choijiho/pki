package com.peloton.boilerplate.service;

import com.peloton.boilerplate.db.dao.UserRepository;
import com.peloton.boilerplate.model.dto.request.SignUpDto;
import com.peloton.boilerplate.model.dto.response.AuthTokenDto;
import com.peloton.boilerplate.model.dto.response.UserDto;
import com.peloton.boilerplate.model.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccessService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    UserRepository userRepository;

    public UserDto getUserBySid(Long userSid) {
        if (userRepository == null) {
            throw new UnsupportedOperationException("DB is not configured. Enable DataSource/JPA to use this API.");
        }
        User user = userRepository	.findBySidAndDeleteTimeIsNull(userSid);
        UserDto userDto = new UserDto(user);
        return userDto;
    }

    @Transactional
    public AuthTokenDto registUser(SignUpDto signUpDto) {
        if (userRepository == null) {
            throw new UnsupportedOperationException("DB is not configured. Enable DataSource/JPA to use this API.");
        }
        // 비밀번호 암호화
        String password = passwordEncoder.encode(signUpDto.getPassword());

        // 사용자 등록
        User regUser = new User();
        regUser.regEntity(signUpDto, password);
        User user = userRepository.save(regUser);

        return AuthTokenDto.builder()
                .refreshToken("ABCDEFG")
                .accessToken("HIJKLMNOP")
                .build();
    }

}
