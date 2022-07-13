package ccc.keeweapi.api.user;

import ccc.keeweapi.config.security.jwt.JwtUtils;
import ccc.keeweapi.dto.ApiResponse;
import ccc.keeweapi.service.user.UserApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserApiService userService;
    private final JwtUtils jwtUtils;

    @GetMapping("/kakao")
    public ApiResponse<?> signUpWithKakao(@RequestParam String code) {
        log.info("[Kakao Signup] code {}", code);
        return ApiResponse.ok(userService.signUpWithKakao(code));
    }

    @GetMapping
    public String tt(@RequestParam String email) {
        return jwtUtils.createToken(email, null);
    }
}