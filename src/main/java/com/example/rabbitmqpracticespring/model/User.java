package com.example.rabbitmqpracticespring.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class User {
    private String Name;
    private Integer age;
}
