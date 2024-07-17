package com.secure.createLinkedin.helper;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonInclude;


import lombok.Data;

@Data
@Component
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseStructure<T> {
	String message;
	String status;
	int code;
	T data;


}

//TEXT AND MEDIA UPLOAD TO PAGE/ORGANIZATION
//@PostMapping("/postToPage")
//public ResponseEntity<ResponseStructure<String>> createPost(
//        @RequestParam(value = "file", required = false) MultipartFile file,
//        @RequestParam("caption") String caption) {
//
//    ResponseStructure<String> response;
//    if (file != null && !file.isEmpty()) {
//        response = linkedInService.uploadImageToLinkedInPage(file, caption);
//    } else {
//        response = linkedInService.createPostPage(caption);
//    }
//
//    return new ResponseEntity<>(response, HttpStatus.valueOf(response.getCode()));
//}