package com.example.crud.service;

import reactor.core.publisher.Mono;
import com.example.crud.dto.*;

public interface PartnerService {	
	Mono<ResponseDTO> handleRequest(RequestDTO request);
}
