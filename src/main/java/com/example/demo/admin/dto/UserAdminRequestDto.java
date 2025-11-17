package com.example.demo.admin.dto;

import com.example.demo.user.domain.UserRole;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserAdminRequestDto {
    private List<Long> id;
    private List<UserRole> role;
}
