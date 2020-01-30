package io.archilab.prox.tagservice.tag;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestParam;

public interface TagRepositoryCustom {

  public List<Tag> tagRecommendations(@RequestParam("tagIds") UUID[] tagIds);

}
