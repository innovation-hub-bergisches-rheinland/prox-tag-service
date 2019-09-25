package io.archilab.prox.tagservice.tag.recommendation;


import io.archilab.prox.tagservice.tag.Tag;
import io.archilab.prox.tagservice.tag.TagCollection;
import io.archilab.prox.tagservice.tag.TagCollectionRepository;
import lombok.NoArgsConstructor;
import lombok.var;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Transactional
@Service
@NoArgsConstructor
public class TagCounterUpdater {

  private final Logger log = LoggerFactory.getLogger(TagCounterUpdater.class);


  @Autowired
  private TagCollectionRepository tagCollectionRepository;

  @Autowired
  private TagCounterRepository tagCounterRepository;

  public TagCounterUpdater(TagCollectionRepository tagCollectionRepository,
      TagCounterRepository tagCounterRepository) {
    this.tagCollectionRepository = tagCollectionRepository;
    this.tagCounterRepository = tagCounterRepository;
  }

  public void updateTagCounter() {

    Map<TagCounter, TagCounter> cache = new HashMap<>();

    var collection = tagCollectionRepository.findAll();

    for (TagCollection col : collection) {

      List<Tag> tags = col.getTags();
      Collections.sort(tags);

      for (int i = 0; i < tags.size() - 1; i++) {
        for (int k = i + 1; k < tags.size(); k++) {
          var tag1 = tags.get(i);
          var tag2 = tags.get(k);

          var counter = new TagCounter(tag1, tag2, 1);

          if (cache.containsKey(counter)) {
            counter = cache.get(counter);
            counter.setCount(counter.getCount() + 1);
          } else {
            cache.put(counter, counter);
          }
        }
      }
    }

    this.tagCounterRepository.deleteAll();


    this.tagCounterRepository.saveAll(cache.values());

  }
}
