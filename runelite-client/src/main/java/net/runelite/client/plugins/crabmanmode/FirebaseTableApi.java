package net.runelite.client.plugins.crabmanmode;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.lang.reflect.Type;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class FirebaseTableApi implements UnlockedItemTableApi {
    private final String partitionKey = "UnlockedItem";
    private final OkHttpClient httpClient;
    private final String url;
    private final Gson gson;

    public FirebaseTableApi(String url, Gson gson, OkHttpClient httpClient) {
        this.httpClient = httpClient;
        this.url = url;
        this.gson = gson;
    }

    @Override
    public UnlockedItemEntity getEntity(String id) throws Exception {
        String url = this.url + "/" + partitionKey + "/" + id + ".json";
        Request request = createRequestBuilder(url)
                .get()
                .build();
        String jsonResponse = sendRequest(request);

        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        Map<String, Object> response = gson.fromJson(jsonResponse, type);
        if (response == null) {
            return null;
        }

        return this.mapToEntity(response);
    }

    @Override
    public List<UnlockedItemEntity> listEntities() throws Exception {
        String url = this.url + "/" + partitionKey + ".json";
        Request request = createRequestBuilder(url)
                .get()
                .build();
        String jsonResponse = sendRequest(request);

        Type type = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
        Map<String, Map<String, Object>> response = gson.fromJson(jsonResponse, type);

        if (response == null) {
            log.info("listEntities response is null");
            return new ArrayList<>();
        }

        return response.values().stream()
                .map(this::mapToEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteEntity(String id) throws Exception {
        String url = this.url + "/" + partitionKey + "/" + id + ".json";
        Request request = createRequestBuilder(url)
                .delete()
                .build();
        sendRequest(request);
    }

    @Override
    public void insertEntity(UnlockedItemEntity entity) throws Exception {
        String url = this.url + "/" + partitionKey + "/" + entity.getItemId().toString() + ".json";
        String jsonPayload = gson.toJson(entityToMap(entity));
        RequestBody body = RequestBody.create(MediaType.parse("application/json"), jsonPayload);
        Request request = createRequestBuilder(url)
                .put(body)
                .header("Content-Type", "application/json")
                .build();
        sendRequest(request);
    }

    private String sendRequest(Request request) throws Exception {
        try (Response response = httpClient.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                throw new Exception("Request failed: " + response.body().string());
            }
        }
    }

    private Request.Builder createRequestBuilder(String url) {
        return new Request.Builder()
                .url(url)
                .header("Accept", "application/json")
                .header("User-Agent", "CrabManModePlugin");
    }

    private Map<String, Object> entityToMap(UnlockedItemEntity entity) {
        Map<String, Object> map = new HashMap<>();
        map.put("ItemId", entity.getItemId());
        map.put("ItemName", entity.getItemName());
        map.put("AcquiredBy", entity.getAcquiredBy());
        map.put("AcquiredOn", entity.getAcquiredOn().toString());
        return map;
    }

    private UnlockedItemEntity mapToEntity(Map<String, Object> map) {
        int itemId = ((Double)map.get("ItemId")).intValue();
        String itemName = (String) map.get("ItemName");
        String acquiredBy = (String) map.get("AcquiredBy");
        OffsetDateTime acquiredOn = OffsetDateTime.parse((String) map.get("AcquiredOn"));
        return new UnlockedItemEntity(itemName, itemId, acquiredBy, acquiredOn);
    }
}
