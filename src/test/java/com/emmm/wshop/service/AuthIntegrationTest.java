package com.emmm.wshop.service;

import com.emmm.wshop.WshopApplication;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kevinsawicki.http.HttpRequest;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static com.emmm.wshop.service.TelVerificationServiceTest.getEmptyTel;
import static com.emmm.wshop.service.TelVerificationServiceTest.getValidParameter;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WshopApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:test.yml")
public class AuthIntegrationTest {
    @Autowired
    Environment environment;

    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setUp() throws IOException {
        ClassicConfiguration conf = new ClassicConfiguration();
        conf.setDataSource(
                "jdbc:h2:mem:test",
                "test",
                "test"
        );
        Flyway flyway = new Flyway(conf);
        flyway.clean();
        flyway.migrate();
    }

    private String getUrl(String apiName) {
        return "http://localhost:" + environment.getProperty("local.server.port") + apiName;
    }

    @Test
    public void loginLogoutTest() throws JsonProcessingException {
        // 最开始默认情况下，访问 /api/status 处于未登录状态
        // 发送验证码
        // 带着验证码进行登录
        // 带着Cookie访问 /api/status 处于登录状态
        // 调用 /api/logout
        // 再次带着Cookie访问 /api/status 恢复成为未登录状态

        String statusResponse = HttpRequest.get(getUrl("/api/status"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body();
        Map<String, Object> response = objectMapper.readValue(statusResponse, Map.class);
        Assertions.assertFalse((Boolean) response.get("login"));

        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(getValidParameter()))
                .code();
        Assertions.assertEquals(OK.value(), responseCode);

        Map<String, List<String>> responseHeaders = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(getValidParameter()))
                .headers();

        List<String> setCookie = responseHeaders.get("Set-Cookie");
        Assertions.assertNotNull(setCookie);
    }

    @Test
    public void returnHttpOKWhenParameterIsCorrect() throws JsonProcessingException {
        int responseCode = HttpRequest.post(getUrl("/api/code"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .send(objectMapper.writeValueAsString(getValidParameter()))
                .code();
        Assertions.assertEquals(OK.value(), responseCode);
    }

    @Test
    public void returnHttpBadRequestWhenParameterIsNotCorrect() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(getUrl("/api/code"));
        post.setEntity(new StringEntity(objectMapper.writeValueAsString(getEmptyTel()), ContentType.APPLICATION_JSON));
        httpClient.execute(post, httpResponse -> {
            Assertions.assertEquals(BAD_REQUEST.value(), httpResponse.getStatusLine().getStatusCode());
            return null;
        });
    }
}
