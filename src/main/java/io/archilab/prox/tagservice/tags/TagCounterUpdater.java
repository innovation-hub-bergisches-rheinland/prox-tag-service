package io.archilab.prox.tagservice.tags;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Transactional
@Service
public class TagCounterUpdater {

  //@Autowired
  //private ProjectRepository projectRepository;

  @Autowired
  private TagCounterRepository tagCounterRepository;


  public void updateTagCounter() { // TODO problems on parallel execution of different microservice replicas?
    Map<TagCounter, TagCounter> tagCountersCache = new HashMap<>();

    /*for (Project project : projectRepository.findAll()) {
      Map<TagCounter, TagCounter> projectTagCounters =
          getTagCounterForProjectTags(project.getTags());
      for (TagCounter tagCounter : projectTagCounters.values()) {
        TagCounter existingTagCounter = tagCountersCache.get(tagCounter);
        if (existingTagCounter == null) {
          tagCountersCache.put(tagCounter, tagCounter);
        } else {
          existingTagCounter.setCount(existingTagCounter.getCount() + 1);
        }
      }
    }*/

    saveTagCountersIntoRepository(tagCountersCache);
  }

  private Map<TagCounter, TagCounter> getTagCounterForProjectTags(List<Tag> projectTags) {
    Map<TagCounter, TagCounter> tagCounters = new HashMap<>();

    for (Tag projectTag1 : projectTags) {
      for (Tag projectTag2 : projectTags) {
        if (!projectTag1.equals(projectTag2)) {
          TagCounter tagCounter = new TagCounter(projectTag1, projectTag2, 1);
          if (tagCounters.get(tagCounter) == null) {
            tagCounters.put(tagCounter, tagCounter);
          }
        }
      }
    }

    return tagCounters;
  }

  private void saveTagCountersIntoRepository(Map<TagCounter, TagCounter> tagCountersCache) {
    for (TagCounter tagCounter : tagCounterRepository.findAll()) {
      TagCounter updatedTagCounter = tagCountersCache.get(tagCounter);
      if (updatedTagCounter != null) {
        tagCounter.setCount(updatedTagCounter.getCount());
        tagCountersCache.remove(tagCounter);
      } else {
        tagCounter.setCount(0);
      }
      tagCounterRepository.save(tagCounter);
    }

    for (TagCounter tagCounter : tagCountersCache.values()) {
      tagCounterRepository.save(tagCounter);
    }
  }
}
