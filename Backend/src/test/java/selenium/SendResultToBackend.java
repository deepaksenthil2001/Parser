package selenium;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

public class SendResultToBackend {

    private static final OkHttpClient client = new OkHttpClient();
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void send(String testName, String status, String message) {
        try {
            TestResult result = new TestResult(testName, status, message);

            String json = mapper.writeValueAsString(result);

            RequestBody body = RequestBody.create(
                    json,
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url("http://localhost:8080/api/test-result")   // ✅ FIXED URL
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                // response auto-closed
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
