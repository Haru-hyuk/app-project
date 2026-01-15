package com.flower.service;

import com.flower.search.dto.SemanticIntent;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.Set;
@Component
public class SemanticQueryBuilder {

    public String build(SemanticIntent intent) {

        Set<String> tokens = new LinkedHashSet<>();

        if (intent.getEmotion() != null) {
            intent.getEmotion().forEach(e ->
                tokens.add(e.trim().toLowerCase())
            );
        }

        if (intent.getTone() != null) {
            intent.getTone().forEach(t ->
                tokens.add(t.trim().toLowerCase())
            );
        }

        if (intent.getOccasion() != null) {
            intent.getOccasion().forEach(o ->
                tokens.add(o.trim().toLowerCase())
            );
        }

        if (intent.getTarget() != null) {
            intent.getTarget().forEach(t ->
                tokens.add(t.trim().toLowerCase())
            );
        }

        return String.join(" ", tokens);
    }
}
