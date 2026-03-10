package gift.global.auth;

import gift.member.Member;
import gift.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@Service
public class KakaoAuthService {
    private static final String RESPONSE_TYPE = "response_type";
    private static final String CODE = "code";
    private static final String CLIENT_ID = "client_id";
    private static final String REDIRECT_URI = "redirect_uri";
    private static final String SCOPE = "scope";

    private final KakaoLoginProperties properties;
    private final KakaoLoginClient kakaoLoginClient;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public String buildAuthorizationUrl() {
        return UriComponentsBuilder.fromUriString(properties.authorizeUrl())
            .queryParam(RESPONSE_TYPE, CODE)
            .queryParam(CLIENT_ID, properties.clientId())
            .queryParam(REDIRECT_URI, properties.redirectUri())
            .queryParam(SCOPE, properties.scope())
            .build()
            .toUriString();
    }

    @Transactional
    public String loginWithKakao(String authorizationCode) {
        KakaoLoginClient.KakaoTokenResponse kakaoToken =
            kakaoLoginClient.requestAccessToken(authorizationCode);
        KakaoLoginClient.KakaoUserResponse kakaoUser =
            kakaoLoginClient.requestUserInfo(kakaoToken.accessToken());
        String email = kakaoUser.email();

        Member member = memberRepository.findByEmail(email)
            .orElseGet(() -> new Member(email));
        member.updateKakaoAccessToken(kakaoToken.accessToken());
        memberRepository.save(member);

        return jwtProvider.createToken(member.getEmail());
    }
}
