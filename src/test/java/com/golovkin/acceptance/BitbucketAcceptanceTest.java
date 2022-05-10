package com.golovkin.acceptance;

import com.golovkin.acceptance.utils.app.log.GrootLogEntry;
import com.golovkin.acceptance.utils.common.log.LogLevel;
import com.golovkin.acceptance.utils.http.Parameters;
import com.golovkin.acceptance.utils.http.log.HttpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.golovkin.common.ColorUtils.error;
import static com.golovkin.common.ColorUtils.warn;

@DisplayName("Bitbucket acceptance test")
public class BitbucketAcceptanceTest extends AbstractAcceptanceTest {
    @Test
    public void test() {
        httpStub().add("/hello", "POST", Parameters.create("key=value"), "", 200, "OK")
                .create();



        check().assertHttpRequestsEqual(
                new HttpRequest("/hello", "POST", "")
        );
    }
}
