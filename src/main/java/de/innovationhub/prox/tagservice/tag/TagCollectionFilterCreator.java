package de.innovationhub.prox.tagservice.tag;


import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TagCollectionFilterCreator implements Filter {

  private final TagCollectionRepository tagCollectionRepository;

  public TagCollectionFilterCreator(TagCollectionRepository tagCollectionRepository) {
    this.tagCollectionRepository = tagCollectionRepository;
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
        tagCollectionRepository.save(new TagCollection(tagCollectionId));
      }
    }

    chain.doFilter(request, response);
  }
}
