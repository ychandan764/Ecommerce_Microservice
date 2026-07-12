package com.example.mapper;

import com.example.dto.request.AddressRequest;
import com.example.dto.request.UserRequest;
import com.example.dto.response.AddressResponse;
import com.example.dto.response.UserResponse;
import com.example.entity.Address;
import com.example.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
	
	public User toEntity(UserRequest request) {
		 if (request == null) {
	            return null;
	        }

	        User user = new User();

	        user.setFirstName(request.getFirstName());
	        user.setLastName(request.getLastName());
	        user.setEmail(request.getEmail());
	        user.setPhone(request.getPhone());

	        if (request.getAddress() != null) {
	            user.setAddress(toAddressEntity(request.getAddress()));
	        }

	        return user;
	    }
	
	 public UserResponse toResponse(User user) {

	        if (user == null) {
	            return null;
	        }

	        return UserResponse.builder()
	                .id(user.getId())
	                .firstName(user.getFirstName())
	                .lastName(user.getLastName())
	                .email(user.getEmail())
	                .phone(user.getPhone())
	                .role(user.getRole().name())	                
	                .address(toAddressResponse(user.getAddress()))
	                .build();
	    }
	 
	 public void update(User user, UserRequest request) {

	        if (user == null || request == null) {
	            return;
	        }

	        if (request.getFirstName() != null) {
	            user.setFirstName(request.getFirstName());
	        }

	        if (request.getLastName() != null) {
	            user.setLastName(request.getLastName());
	        }

	        if (request.getEmail() != null) {
	            user.setEmail(request.getEmail());
	        }

	        if (request.getPhone() != null) {
	            user.setPhone(request.getPhone());
	        }

	        if (request.getAddress() != null) {

	            if (user.getAddress() == null) {
	                user.setAddress(new Address());
	            }

	            Address address = user.getAddress();
	            AddressRequest addressRequest = request.getAddress();

	            if (addressRequest.getCity() != null) {
	                address.setCity(addressRequest.getCity());
	            }

	            if (addressRequest.getState() != null) {
	                address.setState(addressRequest.getState());
	            }

	            if (addressRequest.getZipcode() != null) {
	                address.setZipcode(addressRequest.getZipcode());
	            }
	        }
	    }
	 
	 private Address toAddressEntity(AddressRequest request) {

	        if (request == null) {
	            return null;
	        }

	        Address address = new Address();

	        address.setCity(request.getCity());
	        address.setState(request.getState());
	        address.setZipcode(request.getZipcode());

	        return address;
	    }

	    private AddressResponse toAddressResponse(Address address) {

	        if (address == null) {
	            return null;
	        }

	        return AddressResponse.builder()
	                .id(address.getId())
	                .city(address.getCity())
	                .state(address.getState())
	                .zipcode(address.getZipcode())
	                .build();
	    }
	

}
