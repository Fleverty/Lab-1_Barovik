package com.grom.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.grom.model.Group;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GroupService {
    @Autowired
    private ObjectMapper mapper;
    public Group getGroup(String param) {
        Group group = null;
        try {
            group = mapper.readValue(param, new TypeReference<Group>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return group;
    }
}
