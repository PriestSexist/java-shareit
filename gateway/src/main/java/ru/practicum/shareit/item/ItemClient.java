package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";
    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postItem(int ownerId, ItemRequestDto itemRequestDto) {
        return post("", ownerId, itemRequestDto);
    }

    public ResponseEntity<Object> patchItem(int itemId, int ownerId, ItemRequestDto itemRequestDto) {
        return patch("/" + itemId, ownerId, itemRequestDto);
    }

    public ResponseEntity<Object> getItemById(int ownerId, int itemId) {
        return get("/" + itemId, ownerId);
    }

    public void deleteItemById(int itemId) {
        delete(  "/" + itemId);
    }

    public ResponseEntity<Object> getAllItems(int ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }
    public ResponseEntity<Object> getSearchedItems(String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", parameters);
    }

    public ResponseEntity<Object> postComment(int ownerId, int itemId, CommentRequestDto commentRequestDto) {
        return post("/" + itemId + "/comment" , ownerId, commentRequestDto);
    }
}
