package com.grom.controller;

import com.grom.model.Group;
import com.grom.service.GroupService;
import com.grom.service.TemplateService;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
public class MainController {
    @Autowired
    private TemplateService templateService;
    @Autowired
    private GroupService groupService;

    @PostMapping("create")
    public ResponseEntity createDocument(@RequestParam("template") MultipartFile file, @RequestParam("group") String groupInfo) throws Exception {
        Group group = groupService.getGroup(groupInfo);
        File template = templateService.convert(file);
        XWPFDocument document = templateService.getMicrosoftWord(template);
        File result = templateService.fillMicrosoftWord(group, document);
        System.out.println("555555");
        InputStreamResource resource = new InputStreamResource(new FileInputStream(result));
        System.out.println("666666");
        return ResponseEntity.ok()
                .contentLength(result.length())
                .contentType(MediaType.parseMediaType(file.getContentType()))
                .body(resource);
    }
}
