package com.example.demo.security;

import com.example.demo.user.domain.User;
import com.example.demo.user.domain.UserRole;
import com.example.demo.user.domain.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class CustomUserDetails implements UserDetails{
    private final Long id;
    private final String username;
    private final String password;
    private final UserRole role;
    private UserStatus status;

    public CustomUserDetails(User user){
        this.id=user.getId();
        this.username=user.getUsername();
        this.password=user.getPassword();
        this.role=user.getRole();
        this.status=user.getStatus();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        String roleName = "ROLE_" + role.name();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override public boolean isAccountNonExpired() {
        return true;
    }
    @Override public boolean isAccountNonLocked() {
        return true;
    }
    @Override public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override public boolean isEnabled() {
        return this.status != UserStatus.DISABLED;
    }
}
