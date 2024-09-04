package com.tencent.wxcloudrun.controller;

import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * counter控制器
 */
@RestController

public class JavaGameController {
  private final RestTemplate restTemplate = new RestTemplate();

  final CounterService counterService;
  final Logger logger;

  public JavaGameController(@Autowired CounterService counterService) {
    this.counterService = counterService;
    this.logger = LoggerFactory.getLogger(JavaGameController.class);
  }




  /**
   * 发送请求
   *
   * @param body   微信推送过来的消息体
   * @param openId wx open id
   * @return API response
   */
  @PostMapping(value = "/javagame/test")
  @ResponseBody
  public ResponseEntity<String> test(
          @RequestHeader(name = "x-wx-source") String source,
          @RequestHeader(name = "x-wx-openid") String openId,
          @RequestBody Map<String, Object> body) {
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.setContentType(MediaType.TEXT_PLAIN);

    Map<String, Object> reqBody = new HashMap<>();
    reqBody.put("touser", openId);
    reqBody.put("msgtype", "text");
    reqBody.put("text", new HashMap<String, String>() {{
      put("content", String.format("云托管接收消息推送成功，内容如下：\n %s", body));
    }});
    try {
      String wxApiURL = "http://api.weixin.qq.com/cgi-bin/message/custom/send";
      Map result = restTemplate.postForObject(wxApiURL, reqBody, Map.class);
      logger.info("发送回复结果: {} ", result);
      return ResponseEntity.ok().headers(responseHeaders).body("success");
    } catch (HttpClientErrorException e) {
      logger.error("sendMsg errorMsg={}, openId={}, cause by client error", e.getMessage(), openId);
      return ResponseEntity.ok().headers(responseHeaders).body("error");
    } catch (HttpServerErrorException e) {
      logger.error("sendMsg errorMsg={}, openId={}, cause by server error", e.getMessage(), openId);
      return ResponseEntity.ok().headers(responseHeaders).body("error");
    }
  }




  /**
   * 更新计数，自增或者清零
   * @param request {@link CounterRequest}
   * @return API response json
   */
  @PostMapping(value = "/api/count")
  ApiResponse create(@RequestBody CounterRequest request) {
    logger.info("/api/count post request, action: {}", request.getAction());

    Optional<Counter> curCounter = counterService.getCounter(1);
    if (request.getAction().equals("inc")) {
      Integer count = 1;
      if (curCounter.isPresent()) {
        count += curCounter.get().getCount();
      }
      Counter counter = new Counter();
      counter.setId(1);
      counter.setCount(count);
      counterService.upsertCount(counter);
      return ApiResponse.ok(count);
    } else if (request.getAction().equals("clear")) {
      if (!curCounter.isPresent()) {
        return ApiResponse.ok(0);
      }
      counterService.clearCount(1);
      return ApiResponse.ok(0);
    } else {
      return ApiResponse.error("参数action错误");
    }
  }
  
}