package org.chun.framework.demo.model.rq;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.chun.framework.demo.DemoController;
import org.chun.framework.demo.model.base.BaseModel;

@Getter
@Setter
@ToString
public class DemoControllerRequest extends BaseModel<DemoController> {
  DemoControllerRequest(){
    this.setBaseClass(DemoController.class);
  }

  String a;
  String g;
}
