package com.example.demo.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Setter
@Getter
public class Output {
    public String key;
    public String value;
    public Double confidence;
}
