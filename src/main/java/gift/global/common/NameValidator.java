package gift.global.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class NameValidator {
    private static final Pattern ALLOWED_PATTERN =
        Pattern.compile("^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ ()\\[\\]+\\-&/_]*$");

    static final String ERROR_NAME_REQUIRED = "이름은 필수입니다.";
    static final String ERROR_NAME_TOO_LONG = "이름은 공백을 포함하여 최대 %d자까지 입력할 수 있습니다.";
    static final String ERROR_NAME_INVALID_CHARS =
        "이름에 허용되지 않는 특수 문자가 포함되어 있습니다. 사용 가능: ( ), [ ], +, -, &, /, _";
    static final String ERROR_KAKAO_RESTRICTED =
        "\"카카오\"가 포함된 이름은 담당 MD와 협의한 경우에만 사용할 수 있습니다.";

    private NameValidator() {
    }

    public static List<String> validate(String name, int maxLength) {
        return validate(name, maxLength, false);
    }

    public static List<String> validate(String name, int maxLength, boolean allowKakao) {
        List<String> errors = new ArrayList<>();

        if (name == null || name.isBlank()) {
            errors.add(ERROR_NAME_REQUIRED);
            return errors;
        }

        if (name.length() > maxLength) {
            errors.add(String.format(ERROR_NAME_TOO_LONG, maxLength));
        }

        if (!ALLOWED_PATTERN.matcher(name).matches()) {
            errors.add(ERROR_NAME_INVALID_CHARS);
        }

        if (!allowKakao && name.contains("카카오")) {
            errors.add(ERROR_KAKAO_RESTRICTED);
        }

        return errors;
    }
}
