package gift.member.api;

import gift.global.auth.JwtProvider;
import gift.member.Member;
import gift.member.service.MemberService;
import gift.global.auth.api.TokenResponse;
import gift.global.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles member registration and login.
 *
 * @author brian.kim
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public MemberController(MemberService memberService, JwtProvider jwtProvider) {
        this.memberService = memberService;
        this.jwtProvider = jwtProvider;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<TokenResponse>> register(
        @Valid @RequestBody MemberRequest request
    ) {
        Member member = memberService.register(request.email(), request.password());
        String token = jwtProvider.createToken(member.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.created(new TokenResponse(token)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
        @Valid @RequestBody MemberRequest request
    ) {
        Member member = memberService.login(request.email(), request.password());
        String token = jwtProvider.createToken(member.getEmail());
        return ResponseEntity.ok(ApiResponse.ok(new TokenResponse(token)));
    }
}
