package gift.wish;

import gift.global.error.BusinessException;

public class WishException extends BusinessException {
    public WishException(final WishErrorCode errorCode) {
        super(errorCode);
    }
}
