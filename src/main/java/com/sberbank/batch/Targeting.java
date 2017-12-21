package com.sberbank.batch;

import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Data
@ToString
public class Targeting {
    String client;
    String combined_template_id;
    String tip_id;
    Date start_datetime;
    Date expiration_datetime;
    Double weight;
    List<Param> params;
}