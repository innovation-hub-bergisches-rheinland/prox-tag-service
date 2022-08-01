package de.innovationhub.prox.tagservice.tag;


import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.RequestParam;

public interface TagRepositoryCustom {

  List<Tag> tagRecommendations(@RequestParam("tagIds") Collection<UUID> tagIds);
}
