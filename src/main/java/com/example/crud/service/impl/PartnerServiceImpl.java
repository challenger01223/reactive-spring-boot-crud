package com.example.crud.service.impl;

import lombok.*;
import com.example.crud.service.PartnerService;
import com.example.crud.model.Partner;
import com.example.crud.repository.PartnerRepository;
import com.example.crud.dto.*;

import org.springframework.stereotype.Service;
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
		return validateRequest(request)
	            .flatMap(validationResponse -> {
	                if ("0".equals(validationResponse.getCode())) {
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
	                } else {
	                    return Mono.just(validationResponse);
	                }
	     });
    }
	
	private Mono<ResponseDTO> validateRequest(RequestDTO request) {
	    ResponseDTO response = new ResponseDTO();
	    
	    if (request.getOperation() == null) {
	        response.setCode("1");
	        response.setDetail("Missing required field: operation");
	        return Mono.just(response);
	    }
	    
	    String operation = request.getOperation();

	    switch (operation) {
	        case "create":
	        	if (request.getUsername() == null || !(request.getUsername() instanceof Long)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: username for create operation");
	                return Mono.just(response);
	            }
	            if (request.getLocation() == null || !(request.getLocation() instanceof String)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: location for create operation");
	                return Mono.just(response);
	            }
	            if (request.getValue() == null || !(request.getValue() instanceof String)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: value for create operation");
	                return Mono.just(response);
	            }
	            break;
	        case "view":
	        	if (request.getUsername() == null || !(request.getUsername() instanceof Long)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: username for view operation");
	                return Mono.just(response);
	            }
	            break;
	        case "update":
	        	if (request.getUsername() == null || !(request.getUsername() instanceof Long)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: username for update operation");
	                return Mono.just(response);
	            }
	            if (request.getLocation() == null || !(request.getLocation() instanceof String)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: location for update operation");
	                return Mono.just(response);
	            }
	            if (request.getValue() == null || !(request.getValue() instanceof String)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: value for update operation");
	                return Mono.just(response);
	            }
	            break;
	        case "delete":
	        	if (request.getUsername() == null || !(request.getUsername() instanceof Long)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: username for delete operation");
	                return Mono.just(response);
	            }
	            if (request.getLocation() == null || !(request.getLocation() instanceof String)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: location for delete operation");
	                return Mono.just(response);
	            }
	            if (request.getValue() == null || !(request.getValue() instanceof String)) {
	                response.setCode("1");
	                response.setDetail("Invalid type or missing required field: value for delete operation");
	                return Mono.just(response);
	            }
	            break;
	        default:
	            response.setCode("1");
	            response.setDetail("Invalid operation");
	            return Mono.just(response);
	    }

	    // If validation passes
	    response.setCode("0");
	    return Mono.just(response);
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
					response.setRequestId(request.getRequestId());
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
					response.setRequestId(request.getRequestId());
					return Mono.just(response);
				});
	}
	
	private Mono<ResponseDTO> updatePartner(RequestDTO request) {
		return partnerRepository.findByUsernameAndLocation(request.getUsername(), request.getLocation())
		        .flatMap(savedPartner -> {
		            savedPartner.setValue(request.getValue());
		            savedPartner.setLastUpdate(LocalDate.now());
		            return partnerRepository.save(savedPartner);
		        })
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
		        .switchIfEmpty(Mono.defer(() -> {
		            ResponseDTO response = new ResponseDTO();
		            response.setCode("1");
		            response.setDetail("No partner found with Username: " + request.getUsername() + " and Location: " + request.getLocation());
		            response.setRequestId(request.getRequestId());
		            return Mono.just(response);
		        }))
		        .onErrorResume(error -> {
		            String errorMsg = "Failed update request. Error: " + error.getMessage();
		            ResponseDTO response = new ResponseDTO();
		            response.setCode("1");
		            response.setDetail(errorMsg);
		            response.setRequestId(request.getRequestId());
		            return Mono.just(response);
		        });
	}
	
	private Mono<ResponseDTO> deletePartner(RequestDTO request) {
		return partnerRepository.deleteByUsernameOrLocationOrValue(
		        request.getUsername(), 
		        request.getLocation(), 
		        request.getValue()
		    )
		    .then(Mono.defer(() -> {
		        ResponseDTO response = new ResponseDTO(); 
		        response.setCode("0"); 
		        response.setDetail("Successful"); 
		        response.setUsername(request.getUsername());
		        response.setRequestId(request.getRequestId());
		        return Mono.just(response); 
		    }))
		    .onErrorResume(error -> {
		        String errorMsg = "Failed delete request - " + error.getMessage();
		        ResponseDTO response = new ResponseDTO();
		        response.setCode("1");
		        response.setDetail(errorMsg);
		        response.setRequestId(request.getRequestId());
		        return Mono.just(response);
		    });
	}
}
