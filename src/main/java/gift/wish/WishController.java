package gift.wish;

import gift.global.auth.LoginMember;
import gift.global.common.ApiResponse;
import gift.member.Member;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wishes")
public class WishController {
    private final WishService wishService;

    public WishController(WishService wishService) {
        this.wishService = wishService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<WishResponse>>> getWishes(
        @LoginMember Member member,
        Pageable pageable
    ) {
        var wishes = wishService.findByMemberId(member.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success(wishes));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<WishResponse>> addWish(
        @LoginMember Member member,
        @Valid @RequestBody WishRequest request
    ) {
        var result = wishService.addWish(member.getId(), request.productId());
        if (!result.created()) {
            return ResponseEntity.ok(ApiResponse.success(result.response()));
        }
        return ResponseEntity.created(URI.create("/api/v1/wishes/" + result.response().id()))
            .body(ApiResponse.created(result.response()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> removeWish(
        @LoginMember Member member, @PathVariable Long id
    ) {
        wishService.deleteByIdAndMemberId(id, member.getId());
        return ResponseEntity.ok(ApiResponse.noContent());
    }
}
