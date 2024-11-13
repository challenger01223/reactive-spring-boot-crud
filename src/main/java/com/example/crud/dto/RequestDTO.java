package com.example.crud.dto;

import lombok.*;

@Data
@NoArgsConstructor
public class RequestDTO {
	private String operation;
	private Long username;
	private String location;
	private String value;
	private String requestId;
}
