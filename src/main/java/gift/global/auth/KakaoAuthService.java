package gift.global.auth;

import gift.member.Member;
import gift.member.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class KakaoAuthService {
    private final KakaoLoginClient kakaoLoginClient;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    public KakaoAuthService(
        KakaoLoginClient kakaoLoginClient,
        MemberRepository memberRepository,
        JwtProvider jwtProvider
    ) {
        this.kakaoLoginClient = kakaoLoginClient;
        this.memberRepository = memberRepository;
        this.jwtProvider = jwtProvider;
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
