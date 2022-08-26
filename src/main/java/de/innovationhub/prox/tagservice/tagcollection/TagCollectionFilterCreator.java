package de.innovationhub.prox.tagservice.tagcollection;


import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.rest.core.event.AfterCreateEvent;
import org.springframework.stereotype.Component;

@Component
public class TagCollectionFilterCreator implements Filter {

  private final TagCollectionRepository tagCollectionRepository;
  private final ApplicationEventPublisher applicationEventPublisher;

  public TagCollectionFilterCreator(TagCollectionRepository tagCollectionRepository,
    ApplicationEventPublisher applicationEventPublisher) {
    this.tagCollectionRepository = tagCollectionRepository;
    this.applicationEventPublisher = applicationEventPublisher;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;

    // Pattern with capture group on the tag collection id in the url
    Pattern r = Pattern.compile("\\/tagCollections\\/(.*)\\/tags");
    Matcher m = r.matcher(req.getRequestURI());

    // If we don't have a tag collection with this particular id, we create one.
    if (m.find()) {
      UUID tagCollectionId = UUID.fromString(m.group(1));
      if (!tagCollectionRepository.existsById(tagCollectionId)) {
        var tagCollection = new TagCollection(tagCollectionId);
        tagCollectionRepository.save(tagCollection);

        // Spring Data REST events are published by the REST exporter, not by the repository.
        // Because we create a TagCollection using the repository, we need to publish the event
        // manually.
        // TODO: Get rid of Spring Data REST magic.
        applicationEventPublisher.publishEvent(new AfterCreateEvent(tagCollection));
      }
    }

    chain.doFilter(request, response);
  }
}
