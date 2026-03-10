package gift.option;

import gift.global.common.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Each product must have at least one option at all times.
 * Option names are validated against allowed characters and length constraints.
 */
@RestController
@RequestMapping(path = "/api/v1/products/{productId}/options")
public class OptionController {
    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OptionResponse>>> getOptions(
        @PathVariable Long productId
    ) {
        List<OptionResponse> responses = optionService.findByProductId(productId);
        return ResponseEntity.ok(ApiResponse.success(responses));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OptionResponse>> createOption(
        @PathVariable Long productId,
        @Valid @RequestBody OptionRequest request
    ) {
        OptionResponse response = optionService.createOption(productId, request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(response.id())
            .toUri();
        return ResponseEntity.created(location)
            .body(ApiResponse.created(response));
    }

    @DeleteMapping(path = "/{optionId}")
    public ResponseEntity<ApiResponse<Void>> deleteOption(
        @PathVariable Long productId,
        @PathVariable Long optionId
    ) {
        optionService.deleteOption(productId, optionId);
        return ResponseEntity.ok(ApiResponse.noContent());
    }

}
