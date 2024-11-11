package com.example.crud.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.mapping.Column;
import java.time.LocalDate;
import jakarta.persistence.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "partners")
public class Partner {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column("username")
    private Long username;
    
    @Column("active")
    private Boolean active = true;
    
    @Column("location")
    private String location;
    
    @Column("location_value")
    private String value;
    
    @Column("last_update")
    private LocalDate lastUpdate;
    
    @Column("modified_by")
    private String modifiedBy = "creator44";
    
    @Column("create_date")
    private LocalDate createDate;
    
    @Column("created_by")
    private String createdBy = "creator44";
}
