package de.innovationhub.prox.tagservice.tag;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Value;

@ApiModel
@Value
public class TagCount {
  @ApiModelProperty(value = "tag")
  Tag tag;

  @ApiModelProperty(value = "count")
  long count;
}
