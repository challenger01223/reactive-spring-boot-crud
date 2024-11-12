package com.example.crud.repository;

import com.example.crud.model.Partner;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PartnerRepository extends ReactiveCrudRepository<Partner, Long> {
	Flux<Partner> findAllByUsername(Long username);
	Mono<Partner> findByUsername(Long username);
	Mono<Partner> findByUsernameAndLocationAndValue(Long username, String location, String value);
	Mono<Void> deleteByUsernameAndLocationAndValue(Long username, String location, String value);
	Mono<Partner> findByUsernameAndLocation (Long username, String location);
}
