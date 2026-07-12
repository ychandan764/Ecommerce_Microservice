package com.example.controller;

import com.example.dto.request.UserRequest;
import com.example.dto.response.UserResponse;
import com.example.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@PostMapping
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
	    UserResponse response = userService.createUser(request);
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
	    UserResponse response = userService.getUserById(id);
	    return ResponseEntity.ok(response); 
	}

	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {
	    List<UserResponse> users = userService.getAllUsers();
	    
	    if (users.isEmpty()) {
	        return ResponseEntity.noContent().build(); 
	    }
	    
	    return ResponseEntity.ok(users); 
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(
	        @PathVariable Long id,
	        @Valid @RequestBody UserRequest request) {
	    
	    UserResponse updatedUser = userService.updateUser(id, request);
	    return ResponseEntity.ok(updatedUser); 
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
	    userService.deleteUser(id);
	    return ResponseEntity.noContent().build(); 
	}

}
