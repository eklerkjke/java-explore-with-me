package ru.practicum.client;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import ru.practicum.dto.HitDto;

@Component
@Slf4j
public class StatClient {
    private final RestTemplate restTemplate;

    @Value("${stat-server.url}")
    private String serverUrl;

    @Autowired
    public StatClient() {
        this.restTemplate = new RestTemplate();
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
        restTemplate.setRequestFactory(requestFactory);
    }

    public ResponseEntity<Object> hit(@Valid HitDto hitDto) {
        log.info("Сохранение статистики для {}", hitDto);

        ResponseEntity<Object> response;
        try {
            response = restTemplate.postForEntity(serverUrl + "/hit", hitDto, Object.class);
            log.info("Успешное сохранение статистики");
        } catch (HttpStatusCodeException exception) {
            log.error("Ошибка выполнения запроса post сервером статистики для запроса {} : {}, трассировка:", hitDto, exception.getMessage(), exception);

            return ResponseEntity.status(exception.getStatusCode()).body(exception.getResponseBodyAsByteArray());
        }

        ResponseEntity.BodyBuilder responseBuilder = ResponseEntity.status(response.getStatusCode());

        if (response.hasBody()) {
            return responseBuilder.body(response.getBody());
        }

        return responseBuilder.build();
    }
}
