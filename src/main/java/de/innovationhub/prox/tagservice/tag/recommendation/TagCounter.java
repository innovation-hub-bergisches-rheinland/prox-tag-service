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

package de.innovationhub.prox.tagservice.tag.recommendation;

import de.innovationhub.prox.tagservice.core.AbstractEntity;
import de.innovationhub.prox.tagservice.tag.Tag;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import lombok.*;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TagCounter extends AbstractEntity {

  @ManyToOne private Tag tag1;
  @ManyToOne private Tag tag2;

  @Setter private int count;

  public TagCounter(Tag tag1, Tag tag2, int count) {
    if (tag1.getId().toString().compareTo(tag2.getId().toString()) < 0) {
      this.tag1 = tag1;
      this.tag2 = tag2;
    } else {
      this.tag1 = tag2;
      this.tag2 = tag1;
    }

    this.count = count;
  }

  @Override
  public int hashCode() {
    return (tag1.getId().toString() + tag2.getId().toString()).hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof TagCounter)) {
      return false;
    }
    return (tag1.equals(((TagCounter) (o)).getTag1()) && tag2.equals(((TagCounter) (o)).getTag2()));
  }

  public Tag getOtherTag(Tag tag) {
    return tag.equals(tag1) ? tag2 : tag1;
  }
}
