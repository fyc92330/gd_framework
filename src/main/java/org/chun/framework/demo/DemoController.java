package org.chun.framework.demo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.chun.framework.annotations.ProcessorAPI;
import org.chun.framework.demo.model.ApiResponse;
import org.chun.framework.demo.model.rq.DemoControllerRequest;
import org.chun.framework.demo.model.rs.DemoControllerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@Slf4j
@RestController
@RequestMapping(value = "/demo")
public class DemoController {

//  @ProcessorAPI(value = "/init", method = GET)

  public Object init() {
    log.info("init");
    return new ApiResponse<>(null, 200, "ok");
  }

  @GetMapping("/info")
  public Object getInfo() {
    log.info("info");
    return ResponseEntity.ok();
  }


  @ProcessorAPI(value = "/search", method = POST)
//  @PostMapping("/search")
  public Object search(@RequestBody DemoControllerRequest request) throws JsonProcessingException {
    log.info("search:{}", new ObjectMapper().writeValueAsString(request));
    DemoControllerResponse response = new DemoControllerResponse();
    response.setName("GD");
    response.setMobile("0919");
    response.setGender("M");
    return response;
  }

}
