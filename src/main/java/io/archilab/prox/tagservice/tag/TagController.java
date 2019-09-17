package io.archilab.prox.tagservice.tag;


import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.RequestMapping;


@RepositoryRestController
@ExposesResourceFor(Tag.class)
@RequestMapping("tags")
public class TagController {

}
