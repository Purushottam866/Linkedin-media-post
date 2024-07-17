  package com.secure.createLinkedin.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.secure.createLinkedin.helper.ResponseStructure;

import org.hibernate.internal.build.AllowSysOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class LinkedInService 
{

    @Value("${linkedin.access.token}")
    private String accessToken;

    @Autowired
    ResponseStructure<String> response;
    
   RestTemplate restTemplate = new RestTemplate();
   
   TaskScheduler taskScheduler;
   
   
//   public ResponseStructure<String> createPost(String message) {
//     
//       try {
//           String url = "https://api.linkedin.com/v2/ugcPosts";
//           String requestBody = "{\"author\":\"urn:li:person:aolpVZyS0w\",\"lifecycleState\":\"PUBLISHED\",\"specificContent\":{\"com.linkedin.ugc.ShareContent\":{\"shareCommentary\":{\"text\":\"" + message + "\"},\"shareMediaCategory\":\"NONE\"}},\"visibility\":{\"com.linkedin.ugc.MemberNetworkVisibility\":\"PUBLIC\"}}";
//           
//           HttpHeaders headers = new HttpHeaders();
//           headers.set("Authorization", "Bearer " + accessToken);
//           headers.set("Content-Type", "application/json");
//           HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//
//           ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//           if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
//               response.setStatus("Success");
//               response.setMessage("Post created successfully");
//               response.setCode(HttpStatus.CREATED.value());
//           } else {
//               response.setStatus("Failure");
//               response.setMessage("Failed to create post");
//               response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//           }
//       } catch (Exception e) {
//           response.setStatus("Failure");
//           response.setMessage("Internal Server Error");
//           response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
//       }
//       return response;
//   }
   
		// TEXT POSTING TO LINKEDIN PROFILE        urn:li:person:cHTCMRpubB       urn:li:organization:103081264
   public ResponseStructure<String> createPostProfile(String caption) {
	    ResponseStructure<String> response = new ResponseStructure<>();
	    try {
	        System.out.println("Caption: " + caption);
	        String url = "https://api.linkedin.com/v2/ugcPosts";
	        String requestBody = "{\"author\":\"urn:li:person:aolpVZyS0w\",\"lifecycleState\":\"PUBLISHED\",\"specificContent\":{\"com.linkedin.ugc.ShareContent\":{\"shareCommentary\":{\"text\":\"" + caption + "\"},\"shareMediaCategory\":\"NONE\"}},\"visibility\":{\"com.linkedin.ugc.MemberNetworkVisibility\":\"PUBLIC\"}}";

	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + accessToken);
	        headers.set("Content-Type", "application/json");
	        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

	        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
	            response.setStatus("Success");
	            response.setMessage("Post created successfully");
	            response.setCode(HttpStatus.CREATED.value());
	            response.setData(responseEntity.getBody());
	            System.out.println("Response Body: " + responseEntity.getBody());
	        } else {
	            response.setStatus("Failure");
	            response.setMessage("Failed to create post");
	            response.setCode(responseEntity.getStatusCode().value());
	            response.setData(responseEntity.getBody());
	            System.out.println("Error Response: " + responseEntity.getBody());
	        }
	    } catch (HttpClientErrorException e) {
	        response.setStatus("Failure");
	        response.setMessage("HTTP Client Error: " + e.getStatusCode());
	        response.setCode(e.getStatusCode().value());
	        System.out.println("HttpClientErrorException: " + e.getMessage());
	        e.printStackTrace();
	    } catch (HttpServerErrorException e) {
	        response.setStatus("Failure");
	        response.setMessage("HTTP Server Error: " + e.getStatusCode());
	        response.setCode(e.getStatusCode().value());
	        System.out.println("HttpServerErrorException: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	        response.setStatus("Failure");
	        response.setMessage("Internal Server Error: " + e.getMessage());
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        System.out.println("Exception: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return response;
	}
	
	
	//TEXT POST TO LINKEDIN COMPANY / PAGE
	
//	public ResponseEntity<String> createPostPage(String caption) {
//		 String url = "https://api.linkedin.com/v2/ugcPosts";
//		
//		 HttpHeaders headers = new HttpHeaders();
//		 headers.set("Authorization", "Bearer " + accessToken);
//		 headers.set("Content-Type", "application/json"); 
//		
//		 String requestBody = "{\r\n   \"author\": \"urn:li:organization:103081264\",  \n   \"lifecycleState\": \"PUBLISHED\",\r\n   \"specificContent\": {\r\n      \"com.linkedin.ugc.ShareContent\": {\r\n         \"shareCommentary\": {\r\n            \"text\": \"" + caption + "\"\r\n         },\r\n         \"shareMediaCategory\": \"NONE\"\r\n      }\r\n   },\r\n   \"visibility\": {\r\n      \"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\"\r\n   }\r\n}";
//		
//		 HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
//		
//		 return restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
//	}
	
	
	//TEXT POST TO LINKEDIN COMPANY / PAGE
	public ResponseStructure<String> createPostPage(String caption) {
	    ResponseStructure<String> response = new ResponseStructure<>();
	    try {
	        System.out.println("Caption: " + caption);
	        String url = "https://api.linkedin.com/v2/ugcPosts";
	        String requestBody = "{\"author\":\"urn:li:organization:103081264\",\"lifecycleState\":\"PUBLISHED\",\"specificContent\":{\"com.linkedin.ugc.ShareContent\":{\"shareCommentary\":{\"text\":\"" + caption + "\"},\"shareMediaCategory\":\"NONE\"}},\"visibility\":{\"com.linkedin.ugc.MemberNetworkVisibility\":\"PUBLIC\"}}";

	        HttpHeaders headers = new HttpHeaders();
	        headers.set("Authorization", "Bearer " + accessToken);
	        headers.set("Content-Type", "application/json");
	        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);

	        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
	        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
	            response.setStatus("Success");
	            response.setMessage("Post created successfully");
	            response.setCode(HttpStatus.CREATED.value());
	            response.setData(responseEntity.getBody());
	            System.out.println("Response Body: " + responseEntity.getBody());
	        } else {
	            response.setStatus("Failure");
	            response.setMessage("Failed to create post");
	            response.setCode(responseEntity.getStatusCode().value());
	            response.setData(responseEntity.getBody());
	            System.out.println("Error Response: " + responseEntity.getBody());
	        }
	    } catch (HttpClientErrorException e) {
	        response.setStatus("Failure");
	        response.setMessage("HTTP Client Error: " + e.getStatusCode());
	        response.setCode(e.getStatusCode().value());
	        System.out.println("HttpClientErrorException: " + e.getMessage());
	        e.printStackTrace();
	    } catch (HttpServerErrorException e) {
	        response.setStatus("Failure");
	        response.setMessage("HTTP Server Error: " + e.getStatusCode());
	        response.setCode(e.getStatusCode().value());
	        System.out.println("HttpServerErrorException: " + e.getMessage());
	        e.printStackTrace();
	    } catch (Exception e) {
	        response.setStatus("Failure");
	        response.setMessage("Internal Server Error: " + e.getMessage());
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        System.out.println("Exception: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return response;
	}


   @Autowired
   public LinkedInService(TaskScheduler taskScheduler) {
       this.taskScheduler = taskScheduler;
   }
   
   
 //SCHEDULING FOR CAPTION
   
   @SuppressWarnings("deprecation")
   public ResponseStructure<String> schedulePost(String message, LocalDateTime scheduleDateTime) {
       try {
           System.out.println("Post scheduled at Date Time: " + scheduleDateTime + " and message is: " + message);

           // Calculate delay until scheduled time
           Duration delay = Duration.between(LocalDateTime.now(), scheduleDateTime);
           Instant scheduledTime = Instant.now().plusMillis(delay.toMillis());

           // Schedule the task
           taskScheduler.schedule(() -> {
               // Execute the task to create the post
               createPostProfile(message);
               // System.out.println("Post created: " + message);
           }, Date.from(scheduledTime));

           // Return success response
           ResponseStructure<String> response = new ResponseStructure<>();
           response.setStatus("Success");
           response.setMessage("Post scheduled successfully");
           response.setCode(HttpStatus.OK.value());
           return response;
       } catch (Exception e) {
           // Handle exceptions
           System.err.println("Error scheduling post: " + e.getMessage());
           ResponseStructure<String> response = new ResponseStructure<>();
           response.setStatus("Failure");
           response.setMessage("Failed to schedule post");
           response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
           return response;
       }
   }

   
   // SHARE IMAGE/VIDEO AND TEXT TO LINKEDIN PROFILE
   public ResponseStructure<String> uploadImageToLinkedIn(MultipartFile file, String caption) {
	   
	    try {
	    	
	    	System.out.println("controller is here 1 " + caption + " " + file);
	    	
	        String recipeType = determineRecipeType(file);
	        String mediaType = determineMediaType(file);
	        JsonNode uploadResponse = registerUpload(recipeType);
	        String uploadUrl = uploadResponse.get("value").get("uploadMechanism").get("com.linkedin.digitalmedia.uploading.MediaUploadHttpRequest").get("uploadUrl").asText();
	        String mediaAsset = uploadResponse.get("value").get("asset").asText();
	        uploadImage(uploadUrl, file);
	        ResponseStructure<String> postResponse = createLinkedInPost(mediaAsset, caption, mediaType);

	        // Set the response based on the postResponse
	        handlePostResponse(response, postResponse);

	    } catch (HttpClientErrorException.TooManyRequests e) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Too Many Requests - " + e.getMessage());
	        response.setCode(HttpStatus.TOO_MANY_REQUESTS.value());
	        response.setData(null);
	    } catch (HttpClientErrorException e) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
	        response.setCode(e.getStatusCode().value());
	        response.setData(null);
	    } catch (IOException e) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to upload media to LinkedIn: " + e.getMessage());
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	        response.setData(null);
	    }
	    return response;
	}

	private void handlePostResponse(ResponseStructure<String> response, ResponseStructure<String> postResponse) {
	    if (postResponse.getCode() == 201) {
	        response.setStatus(postResponse.getStatus());
	        response.setMessage(postResponse.getMessage());
	        response.setCode(postResponse.getCode());
	        response.setData(postResponse.getData());
	    } else if (postResponse.getCode() == 400) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Caption is invalid");
	        response.setCode(400);
	        response.setData(null);
	    } else if (postResponse.getCode() == 401) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Unauthorized access");
	        response.setCode(401);
	        response.setData(null);
	    } else if (postResponse.getCode() == 422) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Media asset error");
	        response.setCode(422);
	        response.setData(null);
	    } else if (postResponse.getCode() == 429) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Too Many Requests");
	        response.setCode(429);
	        response.setData(null);
	    } else if (postResponse.getCode() == 500) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Internal server error");
	        response.setCode(500);
	        response.setData(null);
	    } else if (postResponse.getCode() == 503) {
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Network issues");
	        response.setCode(503);
	        response.setData(null);
	    } else {
	        // Handle other failure scenarios
	        response.setStatus("Failure");
	        response.setMessage("Failed to create LinkedIn post: Unexpected error occurred");
	        response.setCode(postResponse.getCode());
	        response.setData(null);
	    }
	}

   private String determineRecipeType(MultipartFile file) {
       String contentType = file.getContentType();
       return contentType != null && contentType.startsWith("image") ? "urn:li:digitalmediaRecipe:feedshare-image" : "urn:li:digitalmediaRecipe:feedshare-video";
   }

   private String determineMediaType(MultipartFile file) {
       return file.getContentType() != null && file.getContentType().startsWith("image") ? "image" : "video";
   }

   private JsonNode registerUpload(String recipeType) throws IOException {
	   
	   System.out.println("controller is here 2 " + recipeType);
	   
       HttpHeaders headers = new HttpHeaders();
       headers.setContentType(MediaType.APPLICATION_JSON);
       headers.set("Authorization", "Bearer " + accessToken);								// urn:li:person:cHTCMRpubB

       String requestBody = "{\"registerUploadRequest\": {\"recipes\": [\"" + recipeType + "\"],\"owner\": \"urn:li:person:aolpVZyS0w\",\"serviceRelationships\": [{\"relationshipType\": \"OWNER\",\"identifier\": \"urn:li:userGeneratedContent\"}]}}";

       HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

       ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
               "https://api.linkedin.com/v2/assets?action=registerUpload",
               HttpMethod.POST,
               requestEntity,
               JsonNode.class
       );

       if (responseEntity.getStatusCode() == HttpStatus.OK) {
           return responseEntity.getBody();
       } else {
           throw new RuntimeException("Failed to register upload: " + responseEntity.getStatusCode());
       }
   }

   private ResponseStructure<String> uploadImage(String uploadUrl, MultipartFile file) {
	  
	    try {
	    	
	    	System.out.println("controller is here 3 " + uploadUrl + " " + file);
	    	
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
	        headers.set("Authorization", "Bearer " + accessToken);

	        byte[] fileContent;
	        try {
	            fileContent = file.getBytes();
	        } catch (IOException e) {
	            response.setStatus("Failure");
	            response.setMessage("Failed to read image file");
	            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	            return response;
	        }

	        HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileContent, headers);

	        ResponseEntity<String> responseEntity = restTemplate.exchange(
	                uploadUrl,
	                HttpMethod.POST,
	                requestEntity,
	                String.class
	        );

	        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
	            response.setStatus("Success");
	            response.setMessage("Media uploaded successfully");
	            response.setCode(HttpStatus.CREATED.value());
	        } else {
	            response.setStatus("Failure");
	            response.setMessage("Failed to upload media: " + responseEntity.getStatusCode());
	            response.setCode(responseEntity.getStatusCode().value());
	        }
	    } catch (Exception e) {
	        response.setStatus("Failure");
	        response.setMessage("Internal Server Error");
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}

	private ResponseStructure<String> createLinkedInPost(String mediaAsset, String caption, String mediaType) {
	   
	    try {                                          //urn:li:person:cHTCMRpubB
	    	
	    	System.out.println("controller is here 4 " + caption + " " + mediaAsset);
	    	
	        HttpHeaders headers = new HttpHeaders();
	        headers.setContentType(MediaType.APPLICATION_JSON);
	        headers.set("Authorization", "Bearer " + accessToken);

	        String shareMediaCategory = mediaType.equals("image") ? "IMAGE" : "VIDEO";

	        String requestBody = "{\n" +
	                "    \"author\": \"urn:li:person:aolpVZyS0w\",\n" +
	                "    \"lifecycleState\": \"PUBLISHED\",\n" +
	                "    \"specificContent\": {\n" +
	                "        \"com.linkedin.ugc.ShareContent\": {\n" +
	                "            \"shareCommentary\": {\n" +
	                "                \"text\": \"" + caption + "\"\n" +
	                "            },\n" +
	                "            \"shareMediaCategory\": \"" + shareMediaCategory + "\",\n" +
	                "            \"media\": [\n" +
	                "                {\n" +
	                "                    \"status\": \"READY\",\n" +
	                "                    \"description\": {\n" +
	                "                        \"text\": \"Center stage!\"\n" +
	                "                    },\n" +
	                "                    \"media\": \"" + mediaAsset + "\",\n" +
	                "                    \"title\": {\n" +
	                "                        \"text\": \"LinkedIn Talent Connect 2021\"\n" +
	                "                    }\n" +
	                "                }\n" +
	                "            ]\n" +
	                "        }\n" +
	                "    },\n" +
	                "    \"visibility\": {\n" +
	                "        \"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\"\n" +
	                "    }\n" +
	                "}";

	        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	        ResponseEntity<String> responseEntity = restTemplate.exchange(
	                "https://api.linkedin.com/v2/ugcPosts",
	                HttpMethod.POST,
	                requestEntity,
	                String.class
	        );

	        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
	        	System.out.println("Image with caption created successfully !!");
	            response.setStatus("Success");
	            response.setMessage("LinkedIn post created successfully");
	            response.setCode(HttpStatus.CREATED.value());
	            response.setData(responseEntity.getBody());
	        } else {
	            response.setStatus("Failure");
	            response.setMessage("Failed to create LinkedIn post: " + responseEntity.getStatusCode());
	            response.setCode(responseEntity.getStatusCode().value());
	        }
	    } catch (Exception e) {
	        response.setStatus("Failure");
	        response.setMessage("Internal Server Error");
	        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
	    }
	    return response;
	}
    
	
	 // SHARE IMAGE/VIDEO AND TEXT TO LINKEDIN PAGE/ORGANIZATION
	   public ResponseStructure<String> uploadImageToLinkedInPage(MultipartFile file, String caption) {
		   
		    try {
		    	
		    	System.out.println("controller is here 1 " + caption + " " + file);
		    	
		        String recipeType = determineRecipeTypePage(file);
		        String mediaType = determineMediaTypePage(file);
		        JsonNode uploadResponse = registerUploadPage(recipeType);
		        String uploadUrl = uploadResponse.get("value").get("uploadMechanism").get("com.linkedin.digitalmedia.uploading.MediaUploadHttpRequest").get("uploadUrl").asText();
		        System.out.println("uploadUrl = "+ uploadUrl );
		        String mediaAsset = uploadResponse.get("value").get("asset").asText();
		        uploadImagePage(uploadUrl, file);
		        ResponseStructure<String> postResponse = createLinkedInPostPage(mediaAsset, caption, mediaType);
		        System.out.println(postResponse.getData());
		        response.setStatus("success");
		        response.setMessage("Media uploaded successfully");
		        response.setCode(HttpStatus.CREATED.value());
		        response.setData(postResponse.getData());
		    } catch (IOException e) {
		        response.setStatus("Failure");
		        response.setMessage("Failed to upload media to LinkedIn: " + e.getMessage());
		        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		    }
		    return response; 
		}

	   private String determineRecipeTypePage(MultipartFile file) {
	       String contentType = file.getContentType();
	       return contentType != null && contentType.startsWith("image") ? "urn:li:digitalmediaRecipe:feedshare-image" : "urn:li:digitalmediaRecipe:feedshare-video";
	   }

	   private String determineMediaTypePage(MultipartFile file) {
	       return file.getContentType() != null && file.getContentType().startsWith("image") ? "image" : "video";
	   }

	   private JsonNode registerUploadPage(String recipeType) throws IOException {
		   
		   System.out.println("controller is here 2 " + recipeType);
		   
	       HttpHeaders headers = new HttpHeaders();
	       headers.setContentType(MediaType.APPLICATION_JSON);
	       headers.set("Authorization", "Bearer " + accessToken);								// urn:li:person:cHTCMRpubB

	       String requestBody = "{\"registerUploadRequest\": {\"recipes\": [\"" + recipeType + "\"],\"owner\": \"urn:li:organization:103081264\",\"serviceRelationships\": [{\"relationshipType\": \"OWNER\",\"identifier\": \"urn:li:userGeneratedContent\"}]}}";

	       HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

	       ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(
	               "https://api.linkedin.com/v2/assets?action=registerUpload",
	               HttpMethod.POST,
	               requestEntity,
	               JsonNode.class
	       );

	       if (responseEntity.getStatusCode() == HttpStatus.OK) {
	           return responseEntity.getBody();
	       } else {
	           throw new RuntimeException("Failed to register upload: " + responseEntity.getStatusCode());
	       }
	   }

	   private ResponseStructure<String> uploadImagePage(String uploadUrl, MultipartFile file) {
		  
		    try {
		    	
		    	System.out.println("controller is here 3 " + uploadUrl + " " + file);
		    	
		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
		        headers.set("Authorization", "Bearer " + accessToken);

		        byte[] fileContent;
		        try {
		            fileContent = file.getBytes();
		        } catch (IOException e) {
		            response.setStatus("Failure");
		            response.setMessage("Failed to read image file");
		            response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		            return response;
		        }

		        HttpEntity<byte[]> requestEntity = new HttpEntity<>(fileContent, headers);

		        ResponseEntity<String> responseEntity = restTemplate.exchange(
		                uploadUrl,
		                HttpMethod.POST,
		                requestEntity,
		                String.class
		        );

		        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
		            response.setStatus("Success");
		            response.setMessage("Media uploaded successfully");
		            response.setCode(HttpStatus.CREATED.value());
		        } else {
		            response.setStatus("Failure");
		            response.setMessage("Failed to upload media: " + responseEntity.getStatusCode());
		            response.setCode(responseEntity.getStatusCode().value());
		        }
		    } catch (Exception e) {
		        response.setStatus("Failure");
		        response.setMessage("Internal Server Error");
		        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		    }
		    return response;
		}

		private ResponseStructure<String> createLinkedInPostPage(String mediaAsset, String caption, String mediaType) {
		   
		    try {                                          //urn:li:person:cHTCMRpubB
		    	
		    	System.out.println("controller is here 4 " + caption + " " + mediaAsset);
		    	
		        HttpHeaders headers = new HttpHeaders();
		        headers.setContentType(MediaType.APPLICATION_JSON);
		        headers.set("Authorization", "Bearer " + accessToken);

		        String shareMediaCategory = mediaType.equals("image") ? "IMAGE" : "VIDEO";

		        String requestBody = "{\n" +
		                "    \"author\": \"urn:li:organization:103081264\",\n" +
		                "    \"lifecycleState\": \"PUBLISHED\",\n" +
		                "    \"specificContent\": {\n" +
		                "        \"com.linkedin.ugc.ShareContent\": {\n" +
		                "            \"shareCommentary\": {\n" +
		                "                \"text\": \"" + caption + "\"\n" +
		                "            },\n" +
		                "            \"shareMediaCategory\": \"" + shareMediaCategory + "\",\n" +
		                "            \"media\": [\n" +
		                "                {\n" +
		                "                    \"status\": \"READY\",\n" +
		                "                    \"description\": {\n" +
		                "                        \"text\": \"Center stage!\"\n" +
		                "                    },\n" +
		                "                    \"media\": \"" + mediaAsset + "\",\n" +
		                "                    \"title\": {\n" +
		                "                        \"text\": \"LinkedIn Talent Connect 2021\"\n" +
		                "                    }\n" +
		                "                }\n" +
		                "            ]\n" +
		                "        }\n" +
		                "    },\n" +
		                "    \"visibility\": {\n" +
		                "        \"com.linkedin.ugc.MemberNetworkVisibility\": \"PUBLIC\"\n" +
		                "    }\n" +
		                "}";

		        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);

		        ResponseEntity<String> responseEntity = restTemplate.exchange(
		                "https://api.linkedin.com/v2/ugcPosts",
		                HttpMethod.POST,
		                requestEntity,
		                String.class
		        );

		        if (responseEntity.getStatusCode() == HttpStatus.CREATED) {
		        	System.out.println("Image with caption created successfully !!");
		            response.setStatus("Success");
		            response.setMessage("LinkedIn post created successfully");
		            response.setCode(HttpStatus.CREATED.value());
		            response.setData(responseEntity.getBody());
		        } else {
		            response.setStatus("Failure");
		            response.setMessage("Failed to create LinkedIn post: " + responseEntity.getStatusCode());
		            response.setCode(responseEntity.getStatusCode().value());
		        }
		    } catch (Exception e) {
		        response.setStatus("Failure");
		        response.setMessage("Internal Server Error");
		        response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
		    }
		    return response;
		}
	
}
	

