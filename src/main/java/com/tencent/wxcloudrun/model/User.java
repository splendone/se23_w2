package com.tencent.wxcloudrun.model;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class User implements Serializable {

  private Integer id;

  private String uid;

  private String name;

  private Integer health;

  private Integer step;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
}
