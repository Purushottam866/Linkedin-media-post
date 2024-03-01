package com.secure.createLinkedin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/linkedin")
public class LinkedInController {

    private final LinkedInService linkedInService;

    @Autowired
    public LinkedInController(LinkedInService linkedInService) {
        this.linkedInService = linkedInService;
    }

    @PostMapping("/upload")
    public void uploadImageToLinkedIn(@RequestParam("file") MultipartFile file,
                                      @RequestParam("caption") String caption) {
        linkedInService.uploadImageToLinkedIn(file, caption);
    }
}
