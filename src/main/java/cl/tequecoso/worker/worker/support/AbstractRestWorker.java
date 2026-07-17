package cl.tequecoso.worker.worker.support;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public abstract class AbstractRestWorker {

    @Value("${servicios.endpoint-url}")
    private String endpoint;

    protected <T> T get(String path, Class<T> responseType) {
        return webClient().get()
                .uri(path)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    protected <T> List<T> getList(String path, Class<T> elementType) {
        return webClient().get()
                .uri(path)
                .retrieve()
                .bodyToFlux(elementType)
                .collectList()
                .block();
    }

    protected <T> T post(String path, Object body, Class<T> responseType) {
        return webClient().post()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    protected <T> T put(String path, Object body, Class<T> responseType) {
        return webClient().put()
                .uri(path)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    protected void delete(String path) {
        webClient().delete()
                .uri(path)
                .retrieve()
                .toBodilessEntity()
                .block();
    }

    protected String requiredText(Map<String, Object> variables, String key) {
        String value = optionalText(variables, key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(
                    "La variable '" + key + "' es obligatoria"
            );
        }
        return value;
    }

    protected String optionalText(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        if (value == null) {
            return null;
        }
        String text = value.toString().trim();
        return text.isEmpty() ? null : text;
    }

    protected Long requiredLong(Map<String, Object> variables, String key) {
        Long value = optionalLong(variables, key);
        if (value == null) {
            throw new IllegalArgumentException(
                    "La variable '" + key + "' es obligatoria"
            );
        }
        return value;
    }

    protected Long optionalLong(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.longValue();
        }
        return Long.parseLong(value.toString().trim());
    }

    protected Integer optionalInteger(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        return Integer.parseInt(value.toString().trim());
    }

    protected Boolean optionalBoolean(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof Boolean bool) {
            return bool;
        }
        return Boolean.parseBoolean(value.toString().trim());
    }

    protected BigDecimal optionalDecimal(Map<String, Object> variables, String key) {
        Object value = variables.get(key);
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal decimal) {
            return decimal;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return new BigDecimal(value.toString().trim());
    }

    protected LocalDate optionalDate(Map<String, Object> variables, String key) {
        String value = optionalText(variables, key);
        return value == null ? null : LocalDate.parse(value);
    }

    protected char optionalChar(Map<String, Object> variables, String key) {
        String value = optionalText(variables, key);
        return value == null ? '\0' : value.charAt(0);
    }

    protected <T> T readEntity(
            Map<String, Object> variables,
            String key,
            Function<Map<String, Object>, T> mapper
    ) {
        Object value = variables.get(key);
        if (value instanceof Map<?, ?> nestedMap) {
            @SuppressWarnings("unchecked")
            Map<String, Object> castMap = (Map<String, Object>) nestedMap;
            return mapper.apply(castMap);
        }
        if (value == null) {
            return mapper.apply(variables);
        }
        throw new IllegalArgumentException(
                "La variable '" + key + "' debe ser un objeto JSON"
        );
    }

    private WebClient webClient() {
        return WebClient.builder()
                .baseUrl(endpoint)
                .build();
    }
}
