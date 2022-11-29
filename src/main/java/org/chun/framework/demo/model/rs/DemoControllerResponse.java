package org.chun.framework.demo.model.rs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.chun.framework.annotations.ApiFor;
import org.chun.framework.demo.DemoController;
import org.chun.framework.demo.model.base.BaseModel;

@Getter
@Setter
@ToString
public class DemoControllerResponse extends BaseModel<DemoController> {

  @ApiFor(name = {
      "search"
  })
  private String name;

  @ApiFor(name = {
      "init",
      "getInfo"
  })
  private String mobile;

  @ApiFor(name = {
      "getInfo"
  })
  private String gender;
}

