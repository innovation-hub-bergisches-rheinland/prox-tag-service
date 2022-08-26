package de.innovationhub.prox.tagservice.tag.dto;

import java.util.Map;

public record ReadTagPopularityDto(Map<String, Integer> popularity) {
}
