package com.inn.cafe.com.inn.cafe.serviceImpl;

import com.google.common.base.Strings;
import com.inn.cafe.com.inn.cafe.JWT.JwtAuthFilter;
import com.inn.cafe.com.inn.cafe.JWT.JwtUtil;
import com.inn.cafe.com.inn.cafe.constants.CafeConstants;
import com.inn.cafe.com.inn.cafe.dao.UserDAO;
import com.inn.cafe.com.inn.cafe.model.Role;
import com.inn.cafe.com.inn.cafe.model.User;
import com.inn.cafe.com.inn.cafe.service.UserService;
import com.inn.cafe.com.inn.cafe.utils.CafeUtils;
import com.inn.cafe.com.inn.cafe.utils.EmailUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {


    private final UserDAO userDAO;


    private final AuthenticationManager authenticationManager;

    private final EmailUtils emailUtils;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    private final JwtAuthFilter jwtAuthFilter;


    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside sign up {}", requestMap);
        try {
            //check xem co null hay khong
            if (validateSignUpMap(requestMap)) {
                //check email co hay khong
                if (!userDAO.findByEmail(requestMap.get("email")).isPresent()) {
                    userDAO.save(getUserFromMap(requestMap));
                    return CafeUtils.getResponseEntity("Successfully register", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Email already exist", HttpStatus.BAD_REQUEST);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        User user = userDAO.findByEmail(requestMap.get("email")).get();
        log.info("Inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email")
                            , requestMap.get("password"))
            );
            //check if login in or not
            if (auth.isAuthenticated()) {
                //check status
                if (user.isStatus()) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(user.getUsername(), user.getRole()) + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("Wait for admin approval", HttpStatus.BAD_REQUEST);
                }
            }
        } catch (Exception ex) {
            log.error("{}", ex);
        }
        return new ResponseEntity<String>("{\"message\":\"" + "Check Your Password" + "\"}"
                , HttpStatus.BAD_REQUEST);
    }

    @Override
    public ResponseEntity<List<User>> getAllUser() {
        log.info("Insite getALlUser");
        log.info(String.valueOf(jwtAuthFilter.isAdmin()));
        try {
            //check if person login is admin or not
            if (jwtAuthFilter.isAdmin()) {
                return new ResponseEntity<>(userDAO.findByRole(Role.USER), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> updateUser(Map<String, String> requestMap) {
        try {
            //check if admin
            if (jwtAuthFilter.isAdmin()) {
                Optional<User> user = userDAO.findById(Integer.parseInt(requestMap.get("id")));
                //check user is existed
                if (!user.isEmpty()) {
                    userDAO.updateStatus(Boolean.parseBoolean(requestMap.get("status")), Integer.parseInt(requestMap.get("id")));
                    sendMailToAllAdmin(Boolean.parseBoolean(requestMap.get("status")), user.get().getEmail(), userDAO.findAllAdminEmail());
                    return CafeUtils.getResponseEntity("User Status Updated Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity(CafeConstants.USER_NOT_FOUND, HttpStatus.UNAUTHORIZED);
                }

            } else {
                return CafeUtils.getResponseEntity(CafeConstants.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtils.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User user = userDAO.findByEmail(jwtAuthFilter.getCurrentUser()).get();
            //check user null or not
            if (!user.equals(null)) {
                //check old password
                if (passwordEncoder.matches(requestMap.get("password"), user.getPassword())) {
                    user.setPassword(requestMap.get("newPassword"));
                    userDAO.save(user);
                    return CafeUtils.getResponseEntity("Password Update Successfully", HttpStatus.OK);
                } else {
                    return CafeUtils.getResponseEntity("Incorrect old password", HttpStatus.BAD_GATEWAY);
                }
            } else {
                return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> forgetPassword(Map<String, String> requestMap) {
        try {
            User user = userDAO.findByEmail(requestMap.get("email")).get();
            //check if user is existed
            if (!user.equals(null) && !Strings.isNullOrEmpty(requestMap.get("email"))) {
                emailUtils.forgetPasswordMail(requestMap.get("email"), "Password by me", user.getPassword());
            }
            return CafeUtils.getResponseEntity("Check your email for password", HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return CafeUtils.getResponseEntity(CafeConstants.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(boolean status, String email, List<String> allAdminEmail) {
        allAdminEmail.remove(jwtAuthFilter.getCurrentUser());
        if (status == true) {
            emailUtils.sendSimpleMessage(jwtAuthFilter.getCurrentUser()
                    , "Account Approved"
                    , "USER:- " + email + "\n is approved by\n ADMIN:-" + jwtAuthFilter.getCurrentUser()
                    , allAdminEmail);
        } else {
            emailUtils.sendSimpleMessage(jwtAuthFilter.getCurrentUser()
                    , "Account Disable"
                    , "USER:- " + email + "\n is Disabled by\n ADMIN:-" + jwtAuthFilter.getCurrentUser()
                    , allAdminEmail);
        }
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("contactNumber")
                && requestMap.containsKey("email") && requestMap.containsKey("password")) {
            return true;
        } else {
            return false;
        }
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        return User.builder()
                .name(requestMap.get("name"))
                .contactNumber(requestMap.get("contactNumber"))
                .email(requestMap.get("email"))
                .password(passwordEncoder.encode(requestMap.get("password")))
                .status(false)
                .role(Role.USER)
                .build();
    }


}
