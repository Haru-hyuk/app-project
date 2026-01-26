package com.flower.util;

import java.util.List;

/**
 * 벡터 유사도 계산 유틸리티
 */
public class VectorSimilarityUtil {

    /**
     * 코사인 유사도 계산
     * @param vector1 첫 번째 벡터 (float 배열)
     * @param vector2 두 번째 벡터 (Double 리스트)
     * @return 코사인 유사도 (0.0 ~ 1.0)
     */
    public static double cosineSimilarity(float[] vector1, List<Double> vector2) {
        if (vector1.length != vector2.size()) {
            throw new IllegalArgumentException("벡터 차원이 일치하지 않습니다.");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            double v1 = vector1[i];
            double v2 = vector2.get(i);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }

    /**
     * 코사인 유사도 계산 (Double 리스트 버전)
     * @param vector1 첫 번째 벡터 (Double 리스트)
     * @param vector2 두 번째 벡터 (Double 리스트)
     * @return 코사인 유사도 (0.0 ~ 1.0)
     */
    public static double cosineSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("벡터 차원이 일치하지 않습니다.");
        }

        double dotProduct = 0.0;
        double norm1 = 0.0;
        double norm2 = 0.0;

        for (int i = 0; i < vector1.size(); i++) {
            double v1 = vector1.get(i);
            double v2 = vector2.get(i);
            dotProduct += v1 * v2;
            norm1 += v1 * v1;
            norm2 += v2 * v2;
        }

        if (norm1 == 0.0 || norm2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
    }
}
