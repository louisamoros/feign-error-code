package com.example.feignerrorcode;

import com.example.feignerrorcode.dto.UserDTO;
import com.example.feignerrorcode.feign.CustomerService;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.junit.jupiter.MockServerExtension;
import org.mockserver.matchers.Times;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import java.util.Base64;
import java.util.StringJoiner;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockServerExtension.class)
class ServiceTest {

    // system under test
    private CustomerService customerService;
    private static ClientAndServer mockServer;

    @BeforeAll
    static void setupMockServer(final ClientAndServer injectedMockServer) {
        mockServer = injectedMockServer;
    }

    @BeforeEach
    public void setup() {
        mockServer.reset();
        var url = "http://" + mockServer.remoteAddress().getHostString() + ":" + mockServer.remoteAddress().getPort();
        customerService = new CustomerService(url, "username", "password");
    }

    @Test
    public void should_get_customer() {
        // given
        final var brand = "brand";
        final var sessionToken = "jwt-session-token";
        final var basicAuth = basicAuth("username", "password");

        HttpRequest request = HttpRequest.request().withMethod("GET").withPath("/" + brand + "/customer")
                .withHeaders(
                        new Header("Authorization", basicAuth),
                        new Header("sessionToken", sessionToken)
                );

        HttpResponse response = HttpResponse.response().withStatusCode(200)
                .withBody(new StringJoiner(",", "{", "}")
                        .add("\"id\": 1234")
                        .add("\"username\": \"Sam\"")
                        .toString()
                );

        mockServer.when(request, Times.exactly(1))
                .respond(response);

        // when
        final var user = customerService.get(brand, sessionToken);

        // then
        // passing null retrieve all recorded requests
        final var httpRequests = mockServer.retrieveRecordedRequests(null);
        assertThat(httpRequests).hasSize(1);
        final var requestSent = httpRequests[0];
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(requestSent).hasFieldOrPropertyWithValue("method", "GET");
            softly.assertThat(requestSent.getHeaders().containsEntry("sessionToken", sessionToken)).isTrue();
            softly.assertThat(requestSent.getHeaders().containsEntry("Authorization", basicAuth)).isTrue();
            softly.assertThat(requestSent.getPath().getValue()).isEqualTo("/brand/customer");
            softly.assertThat(user).isEqualToComparingFieldByField(new UserDTO(1234, "Sam"));
        });
    }

    @Test
    public void should_fail_on_get_customer() {
        // given
        final var brand = "brand";
        final var basicAuth = basicAuth("username", "password");

        HttpRequest request = HttpRequest.request().withMethod("GET").withPath("/" + brand + "/customer")
                .withHeaders(
                        new Header("Authorization", basicAuth)
                        // missing sessionToken
                );

        HttpResponse response = HttpResponse.response().withStatusCode(401)
                .withBody(new StringJoiner(",", "{", "}")
                        .add("\"status\": 401")
                        .add("\"code\": \"CUST-0001\"")
                        .add("\"message\": \"Unauthorized\"")
                        .toString()
                );

        mockServer.when(request, Times.exactly(1))
                .respond(response);

        // when
        customerService.get(brand, null);

        // then
        // passing null retrieve all recorded requests
        final var httpRequests = mockServer.retrieveRecordedRequests(null);
        assertThat(httpRequests).hasSize(1);
        final var requestSent = httpRequests[0];
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(requestSent).hasFieldOrPropertyWithValue("method", "GET");
            softly.assertThat(requestSent.getHeaders().containsEntry("sessionToken")).isFalse();
            softly.assertThat(requestSent.getHeaders().containsEntry("Authorization", basicAuth)).isTrue();
            softly.assertThat(requestSent.getPath().getValue()).isEqualTo("/brand/customer");
        });
    }

    public static String basicAuth(String username, String password) {
        return "Basic " + Base64.getEncoder().encodeToString(username.concat(":").concat(password).getBytes());
    }

}
