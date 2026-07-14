package com.example.controller;

import com.example.dto.request.UserRequest;
import com.example.dto.response.ErrorResponse;
import com.example.dto.response.UserResponse;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "User management APIs")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

	private final UserService userService;

	@Operation(
			summary = "Create a new user",
			description = "Create a new user by providing user details.",
			responses = {
					@ApiResponse(responseCode = "201", description = "User created successfully",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
					@ApiResponse(responseCode = "400", description = "Invalid user details supplied",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
			})
	@PostMapping
	public ResponseEntity<UserResponse> createUser(@Valid @RequestBody UserRequest request) {
	    UserResponse response = userService.createUser(request);
	    return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Operation(
			summary = "Retrieve a user by ID",
			description = "Get a user object by specifying its ID.",
			responses = {
					@ApiResponse(responseCode = "200", description = "User found",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
					@ApiResponse(responseCode = "404", description = "User not found",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
			})
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
	    UserResponse response = userService.getUserById(id);
	    return ResponseEntity.ok(response);
	}

	@Operation(
			summary = "Retrieve all users",
			description = "Get a list of all registered users.",
			responses = {
					@ApiResponse(responseCode = "200", description = "List of users retrieved successfully",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
					@ApiResponse(responseCode = "204", description = "No users found")
			})
	@GetMapping
	public ResponseEntity<List<UserResponse>> getAllUsers() {
	    List<UserResponse> users = userService.getAllUsers();

	    if (users.isEmpty()) {
	        return ResponseEntity.noContent().build();
	    }

	    return ResponseEntity.ok(users);
	}

	@Operation(
			summary = "Update an existing user",
			description = "Update user details for a given ID.",
			responses = {
					@ApiResponse(responseCode = "200", description = "User updated successfully",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
					@ApiResponse(responseCode = "400", description = "Invalid user details supplied",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
					@ApiResponse(responseCode = "404", description = "User not found",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
			})
	@PutMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(
	        @PathVariable Long id,
	        @Valid @RequestBody UserRequest request) {

	    UserResponse updatedUser = userService.updateUser(id, request);
	    return ResponseEntity.ok(updatedUser);
	}

	@Operation(
			summary = "Delete a user by ID",
			description = "Delete a user by specifying its ID.",
			responses = {
					@ApiResponse(responseCode = "204", description = "User deleted successfully"),
					@ApiResponse(responseCode = "404", description = "User not found",
							content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
			})
	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
	    userService.deleteUser(id);
	    return ResponseEntity.noContent().build();
	}

}
