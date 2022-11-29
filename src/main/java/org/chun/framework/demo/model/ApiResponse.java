package org.chun.framework.demo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.chun.framework.demo.model.base.BaseModel;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<R extends BaseModel<?>> {

  private R data;

  private Integer httpCode;

  private String errorMsg;

}
