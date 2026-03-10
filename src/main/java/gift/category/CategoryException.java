package gift.category;

import gift.global.error.BusinessException;

public class CategoryException extends BusinessException {
    public CategoryException(final CategoryErrorCode errorCode) {
        super(errorCode);
    }
}
