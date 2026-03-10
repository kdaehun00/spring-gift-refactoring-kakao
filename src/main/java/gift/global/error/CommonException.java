package gift.global.error;

public class CommonException extends BusinessException {
    public CommonException(final CommonErrorCode errorCode) {
        super(errorCode);
    }
}
