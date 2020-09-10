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

package io.archilab.prox.tagservice.tag;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Data
@Setter(AccessLevel.NONE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagName implements Comparable<TagName> {

  private static final int MAX_LENGTH = 40;

  @Column(length = MAX_LENGTH)
  private String tagName;

  public TagName(String tagName) {
    if (!TagName.isValid(tagName)) {
      throw new IllegalArgumentException(String.format(
          "Name %s exceeded maximum number of %d allowed characters", tagName, TagName.MAX_LENGTH));
    }
    this.tagName = tagName.toLowerCase();
  }

  public static boolean isValid(String tagName) {
    return tagName != null && tagName.length() <= TagName.MAX_LENGTH;
  }

  @Override
  public int compareTo(TagName o) {
    return this.getTagName().compareTo(o.getTagName());
  }

  public void setTagName(String tagName) {
    this.tagName = tagName.toLowerCase();
  }
}
