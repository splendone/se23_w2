package com.tencent.wxcloudrun.controller;

import com.google.gson.Gson;
import com.tencent.wxcloudrun.config.ApiResponse;
import com.tencent.wxcloudrun.dto.CounterRequest;
import com.tencent.wxcloudrun.model.Counter;
import com.tencent.wxcloudrun.service.CounterService;
import com.tencent.wxcloudrun.service.JavaGameService;
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

  final JavaGameService javaGameService;
  final Logger logger;

  public JavaGameController(@Autowired JavaGameService javaGameService) {
    this.javaGameService = javaGameService;
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

    logger.info(String.format("云托管接收消息，内容如下： %s", body));
    Map<String, Object> resBody = new HashMap<>();
    resBody.put("ToUserName", body.get("FromUserName"));
    resBody.put("FromUserName", body.get("ToUserName"));
    resBody.put("CreateTime", System.currentTimeMillis());
    resBody.put("MsgType", "text");
    resBody.put("Content", javaGameService.genContent(body));
    //将resBody转成json字符串
    String res = new Gson().toJson(resBody);
    logger.info(String.format("云托管回复消息，内容如下： %s", res));
    return ResponseEntity.ok().headers(responseHeaders).body(res);

  }



  
}