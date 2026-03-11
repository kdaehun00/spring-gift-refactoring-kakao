package gift.order.service;

import gift.order.Order;
import gift.product.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;

@Component
public class KakaoMessageClient implements MessageClient {
    private static final String PARAM_TEMPLATE_OBJECT = "template_object";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String PRICE_FORMAT = "%,d";
    private static final String MESSAGE_PREFIX = "\\n\\n💌 ";
    private static final String MESSAGE_TEMPLATE = """
        {
            "object_type": "text",
            "text": "🎁 선물이 도착했어요!\\n\\n%s (%s)\\n수량: %d개\\n금액: %s원%s",
            "link": {},
            "button_title": "선물 확인하기"
        }
        """;

    private final RestClient restClient;
    private final String messageSendUrl;

    public KakaoMessageClient(
        RestClient.Builder builder,
        @Value("${kakao.api.message-send-url}") String messageSendUrl
    ) {
        this.restClient = builder.build();
        this.messageSendUrl = messageSendUrl;
    }

    public void sendToMe(String accessToken, Order order, Product product) {
        var templateObject = buildTemplate(order, product);

        var params = new LinkedMultiValueMap<String, String>();
        params.add(PARAM_TEMPLATE_OBJECT, templateObject);

        restClient.post()
            .uri(messageSendUrl)
            .header(HttpHeaders.AUTHORIZATION, BEARER_PREFIX + accessToken)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(params)
            .retrieve()
            .toBodilessEntity();
    }

    private String buildTemplate(Order order, Product product) {
        var totalPrice = String.format(PRICE_FORMAT, product.getPrice() * order.getQuantity());
        var message = order.getMessage() != null && !order.getMessage().isBlank()
            ? MESSAGE_PREFIX + order.getMessage()
            : "";
        return MESSAGE_TEMPLATE.formatted(
            product.getName(),
            order.getOption().getName(),
            order.getQuantity(),
            totalPrice,
            message
        );
    }
}
