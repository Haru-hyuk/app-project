package com.flower.service;

import com.flower.search.dto.SemanticIntent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

    private final DeepSeekChatService deepSeekChatService;
    private final SemanticQueryBuilder semanticQueryBuilder;
    private final EmbeddingService embeddingService;

    public float[] search(String query) {

        // 1. 의미 해석
        SemanticIntent intent = deepSeekChatService.analyze(query);

        // 2. 의미 문장
        String semanticQuery = semanticQueryBuilder.build(intent);

        // 3. Ollama 임베딩
        return embeddingService.embed(semanticQuery);
    }
}
