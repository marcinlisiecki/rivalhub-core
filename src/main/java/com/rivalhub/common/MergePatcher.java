package com.rivalhub.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MergePatcher<T> {

    private final ObjectMapper objectMapper;

    public T patch(JsonMergePatch mergePatch, T target, Class<T> targetClass)
            throws JsonPatchException, JsonProcessingException {

        JsonNode node = objectMapper.valueToTree(target);
        JsonNode patchedNode = mergePatch.apply(node);
        return objectMapper.treeToValue(patchedNode, targetClass);
    }
}