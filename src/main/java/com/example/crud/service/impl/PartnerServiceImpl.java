package com.example.crud.service.impl;

import lombok.*;
import com.example.crud.service.PartnerService;
import com.example.crud.model.Partner;
import com.example.crud.repository.PartnerRepository;
import com.example.crud.dto.*;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PartnerServiceImpl implements PartnerService {
	private PartnerRepository partnerRepository;
	
	@Override
	public Mono<ResponseDTO> handleRequest(RequestDTO request) {
        switch (request.getOperation()) {
            case "create":
                return createPartner(request);
            case "view":
                return viewPartner(request);
            case "update":
                return updatePartner(request);
            case "delete":
                return deletePartner(request);
            default:
            	return Mono.just(new ResponseDTO());
        }
    }
	
	private Mono<ResponseDTO> createPartner(RequestDTO request) {
		Partner partner = new Partner();
		partner.setUsername(request.getUsername());
		partner.setLocation(request.getLocation());
		partner.setValue(request.getValue());
		LocalDate date = LocalDate.now();
		partner.setLastUpdate(date);
		partner.setCreateDate(date);
		
		return partnerRepository.save(partner)
				.map(savedPartner -> {
					ResponseDTO response = new ResponseDTO();
					response.setCode("0");
					response.setDetail("Successful");
					response.setUsername(savedPartner.getUsername());
					response.setLocation(savedPartner.getLocation());
					response.setRequestId(request.getRequestId());
					response.setValue(savedPartner.getValue());
					return response;
				})
				.onErrorResume(error -> {
					String errorMsg = "Failed to create username " + request.getUsername() + " - " + error.getMessage();
					ResponseDTO response = new ResponseDTO();
					response.setCode("1");
					response.setDetail(errorMsg);
					return Mono.just(response);
				});
	}
	
	private Mono<ResponseDTO> viewPartner(RequestDTO request) {
		return partnerRepository.findAllByUsername(request.getUsername())
				.collectList()
				.flatMap(partners -> {
					ResponseDTO response = new ResponseDTO();
			        response.setCode("0");
			        response.setDetail("Successful");
			        
			        List<LocationViewValueDTO> viewValues = partners.stream()
		                .map(partner -> {
		                	LocationViewValueDTO locationViewValue = new LocationViewValueDTO();
		                	locationViewValue.setLocation(partner.getLocation());
		                	locationViewValue.setValue(partner.getValue());
		                	return locationViewValue;
		                }).collect(Collectors.toList());

			        response.setViewValues(viewValues);
			        response.setRequestId(request.getRequestId());
			        return Mono.just(response);
				})
				.onErrorResume(error -> {
					System.out.println(error);
					String errorMsg = "Failed view request. No username " + request.getUsername() + " found";
					ResponseDTO response = new ResponseDTO();
					response.setCode("1");
					response.setDetail(errorMsg);
					return Mono.just(response);
				});
	}
	
	private Mono<ResponseDTO> updatePartner(RequestDTO request) {
	    return partnerRepository.findById(request.getId()) // Find partner by ID
	        .flatMap(savedPartner -> {
	            savedPartner.setLocation(request.getLocation());
	            savedPartner.setValue(request.getValue());
	            savedPartner.setUsername(request.getUsername());
	            LocalDate date = LocalDate.now();
	            savedPartner.setLastUpdate(date);
	            return partnerRepository.save(savedPartner); // Save the updated partner
	        })
	        .map(savedPartner -> {
	            ResponseDTO response = new ResponseDTO();
	            response.setCode("0");
	            response.setDetail("Successful");
	            response.setUsername(savedPartner.getUsername());
	            response.setLocation(savedPartner.getLocation());
	            response.setRequestId(request.getRequestId());
	            response.setValue(savedPartner.getValue());
	            // Set other fields in the response if needed
	            return response;
	        })
	        .onErrorResume(error -> {
	            String errorMsg = "Failed update request. No partner found with ID " + request.getId() + " - " + error.getMessage();
	            ResponseDTO response = new ResponseDTO();
	            response.setCode("1");
	            response.setDetail(errorMsg);
	            return Mono.just(response);
	        });
	}
	
	private Mono<ResponseDTO> deletePartner(RequestDTO request) {
	    return partnerRepository.findById(request.getId()) // Find partner by ID
	        .flatMap(foundPartner -> partnerRepository.delete(foundPartner) // Delete the found partner
	            .then(Mono.defer(() -> { // Use then to return a Mono
	                ResponseDTO response = new ResponseDTO(); 
	                response.setCode("0"); 
	                response.setDetail("Successful"); 
	                response.setUsername(request.getUsername()); // You may want to set the ID instead
	                return Mono.just(response); 
	            }))
	        )
	        .switchIfEmpty(Mono.defer(() -> { // Handle case where partner is not found
	            ResponseDTO response = new ResponseDTO();
	            response.setCode("1");
	            response.setDetail("No partner found with ID " + request.getId());
	            return Mono.just(response);
	        }))
	        .onErrorResume(error -> {
	            String errorMsg = "Failed delete request - " + error.getMessage();
	            ResponseDTO response = new ResponseDTO();
	            response.setCode("1");
	            response.setDetail(errorMsg);
	            return Mono.just(response);
	        });
	}
}
