package gift.wish.service;

import gift.wish.api.WishResponse;

public record WishAddResult(WishResponse response, boolean created) {
}
