package gift.option;

import gift.global.error.BusinessException;

public class OptionException extends BusinessException {
    public OptionException(final OptionErrorCode errorCode) {
        super(errorCode);
    }
}
