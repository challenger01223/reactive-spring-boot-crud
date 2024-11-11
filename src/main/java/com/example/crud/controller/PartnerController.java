package com.example.crud.controller;

import lombok.*;

import com.example.crud.dto.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.example.crud.service.PartnerService;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/partners")
public class PartnerController {
	@Autowired
	private final PartnerService partnerService;
	
	@PostMapping
	@ResponseStatus(HttpStatus.OK)
	public Mono<ResponseDTO> handleRequest(@RequestBody RequestDTO request) {
        return partnerService.handleRequest(request);
    }
}
