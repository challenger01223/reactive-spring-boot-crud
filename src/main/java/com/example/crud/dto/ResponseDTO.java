package com.example.crud.dto;

import lombok.*;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@NoArgsConstructor
public class ResponseDTO {
	private String code;
    private String detail;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long username;
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private String requestId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private String location;
    @JsonInclude(JsonInclude.Include.NON_NULL)
	private String value;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<LocationViewValueDTO> viewValues;
}
