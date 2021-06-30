/*
 * MIT License
 *
 * Copyright (c) 2020 TH Köln
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.innovationhub.prox.tagservice.tag;

import io.swagger.annotations.ApiResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.server.ExposesResourceFor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@RepositoryRestController
@ExposesResourceFor(Tag.class)
@RequestMapping("tags")
public class TagController {

  private final TagCollectionRepository tagCollectionRepository;

  @Autowired
  public TagController(TagCollectionRepository tagCollectionRepository) {
    this.tagCollectionRepository = tagCollectionRepository;
  }

  @GetMapping("/search/popularTags")
  public @ResponseBody
  ResponseEntity<List<TagCount>> popularTags(@RequestParam(required = false, defaultValue = "10", name = "limit") Integer limit) {
    var popularTags = this.tagCollectionRepository.findAllUsedTags().stream().flatMap(t -> t.stream())
        .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()))
        .entrySet()
        .stream()
        .sorted(Comparator.comparing(Entry::getValue, Comparator.reverseOrder()))
        .limit(limit)
        .map(entry -> new TagCount(entry.getKey(), entry.getValue()))
        .collect(Collectors.toList());

    return ResponseEntity.ok(popularTags);
  }
}
