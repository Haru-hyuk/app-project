package com.flower.search.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SemanticIntent {

    private List<String> emotion;
    private List<String> tone;
    private List<String> occasion;
    private List<String> target;
    private List<String> exclude;
}
