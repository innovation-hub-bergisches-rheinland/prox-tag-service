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

  @Autowired private TagCollectionRepository tagCollectionRepository;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest req = (HttpServletRequest) request;

    // Create a Pattern object
    Pattern r = Pattern.compile("\\/tagCollections\\/(.*)\\/tags");

    // Now create matcher object.
    Matcher m = r.matcher(req.getRequestURI());

    if (m.find()) {
      UUID tagCollectionId = UUID.fromString(m.group(1));
      if (!tagCollectionRepository.findById(tagCollectionId).isPresent()) {
        tagCollectionRepository.save(new TagCollection(tagCollectionId));
      }
    }

    chain.doFilter(request, response);
  }
}
