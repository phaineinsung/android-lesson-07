package kr.easw.lesson07.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.easw.lesson07.model.dto.ExceptionalResultDto;
import kr.easw.lesson07.model.dto.UserDataEntity;
import kr.easw.lesson07.service.UserDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class UserAuthEndpoint {
    private final UserDataService userDataService;
    private HttpServletRequest request;
    private HttpServletResponse response;


    // JWT 인증을 위해 사용되는 엔드포인트입니다.
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody UserDataEntity entity) {
        try {
            // 로그인을 시도합니다.
            return ResponseEntity.ok(userDataService.createTokenWith(entity));
        } catch (Exception ex) {
            // 만약 로그인에 실패했다면, 400 Bad Request를 반환합니다.
            return ResponseEntity.badRequest().body(new ExceptionalResultDto(ex.getMessage()));
        }
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@ModelAttribute UserDataEntity entity) {
        try {
            // 만약 이미 존재하는 사용자인 경우 예외 처리합니다.
            if (userDataService.isUserExists(entity.getUserId())) {
                return ResponseEntity.badRequest().body("이미 존재하는 사용자입니다.");
            }
            // 새로운 사용자를 등록합니다.
            userDataService.createUser(entity);
            return ResponseEntity.ok("회원가입 성공");
        } catch (Exception e) {
            // 예외가 발생할 경우 실패 상태를 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원가입 실패");
        }
    }


    @PostMapping("/logout")
    public ResponseEntity<Object> logout() {
        try {
            // 현재 사용자의 인증 정보를 가져옵니다.
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                // 로그아웃 처리합니다.
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            // 로그아웃 성공 시 200 OK 응답을 반환합니다.
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            // 로그아웃에 실패한 경우 500 Internal Server Error 응답을 반환합니다.
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    }



