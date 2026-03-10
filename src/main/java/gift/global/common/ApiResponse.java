package gift.global.common;

import gift.global.error.ErrorCode;

public record ApiResponse<T>(String code, String message, T data) {
    // 성공 응답 (200 OK, 데이터 있음)
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(SuccessCode.SUCCESS.getCode(),SuccessCode.SUCCESS.getMessage(), data);
    }

    // 성공 응답 (201 Created, 데이터 있음)
    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(SuccessCode.CREATED.getCode(), SuccessCode.CREATED.getMessage(), data);
    }

    // 성공 응답 (204 No Content, 데이터 없음)
    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(SuccessCode.NO_CONTENT.getCode(), SuccessCode.NO_CONTENT.getMessage(), null);
    }

    // 오류 응답 (ErrorCode 사용)
    public static <T> ApiResponse<T> error(ErrorCode errorCode) {
        return new ApiResponse<>(errorCode.getCode(), errorCode.getMessage(), null);
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
