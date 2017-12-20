package com.sberbank.batch;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.Map;

@Data
@ToString
public class Targeting {
    String сlient_id;
    String template_id;
    String model_id;
    Date start_datetime;
    Date end_datetime;
    Double weight;
    Map<String, String> params;
}