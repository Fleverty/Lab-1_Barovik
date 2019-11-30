package com.grom.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    private String course;
    private String groupNumber;
    private String groupLead;
    private String year;
    private List<Man> mans;
}
