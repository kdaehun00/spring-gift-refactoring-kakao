package gift.member;

import gift.global.error.BusinessException;

public class MemberException extends BusinessException {
    public MemberException(final MemberErrorCode errorCode) {
        super(errorCode);
    }
}
