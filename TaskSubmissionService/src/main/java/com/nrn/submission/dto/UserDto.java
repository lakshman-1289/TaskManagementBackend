package com.nrn.submission.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDto {
	private Long id;
    private String email;
    private String password;
    private String role;
    private String fullName;

}
