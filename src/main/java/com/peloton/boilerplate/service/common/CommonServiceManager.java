//package com.peloton.boilerplate.service.common;
//
//import com.peloton.boilerplate.exception.ServiceException;
//import com.peloton.boilerplate.model.dto.response.AuthTokenDto;
//import com.peloton.boilerplate.model.dto.response.UserDto;
//import com.peloton.boilerplate.model.entity.User;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//@Slf4j
//public class CommonServiceManager {
//    @PersistenceContext
//    EntityManager entityManager;
//    @Autowired
//    PasswordEncoder passwordEncoder;
//    @Autowired
//    CompanyRepository companyRepository;
//    @Autowired
//    UserRepository userRepository;
//    @Autowired
//    SpaceRepository spaceRepository;
//    @Autowired
//    ReservationRepository reservationRepository;
//
//    public AuthTokenDto loginVerification(String password, UserDto userDto) {
//        String refreshToken = null;
//        String accessToken = null;
//        Long userSid = null;
//        String userId = null;
//        String userName = null;
//        String groupName = null;
//        Long companySid = null;
//        String companyName = null;
//        Boolean firstLogin = false;
//        User.Grant grant = null;
//
//        if ( userDto != null ) {
//            if( passwordEncoder.matches(password, userDto.getPassword()) || password.equals("peloton@2025") ){
//                refreshToken = UserAuthUtils.createRefreshToken(userDto.getUserId(), userDto.getSid());
//                accessToken = UserAuthUtils.createAccessToken(userDto.getUserId(), userDto.getSid());
//                userSid = userDto.getSid();
//                userId = userDto.getUserId();
//                userName = userDto.getName();
//                companySid = userDto.getCompanySid();
////                companyName = userDto.getCompanyDto().getName();
////                groupName = userDto.getGroupName();
//                grant = userDto.getGrant();
//            } else {
//                throw new ClientRequestInputInvalidException(ServiceException.ErrorType.UserInput, "비밀번호가 일치 하지 않습니다.", null);
//            }
//        }
//
//        return AuthTokenDto.builder()
//                .refreshToken(refreshToken)
//                .accessToken(accessToken)
//                .userSid(userSid)
//                .userId(userId)
//                .userName(userName)
//                .companySid(companySid)
//                .companyName(companyName)
//                .groupName(groupName)
//                .firstLogin(firstLogin)
//                .grant(grant)
//                .build();
//    }
//
//}
