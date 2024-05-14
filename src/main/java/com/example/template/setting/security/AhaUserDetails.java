package com.example.template.setting.security;

import com.example.template.setting.common.base.EntityAbstractBase;
import com.example.template.setting.common.user.EntityUser;
import com.example.template.setting.common.user.EntityRole;
import com.example.template.util.UtilJson;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AhaUserDetails extends EntityAbstractBase implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    @Setter
    List<EntityRole> roles;
    private Long status;

    public static AhaUserDetails getAnonymousUser() {
        return builder().id(0L).username("anonymous").build();
    }

    public static AhaUserDetails of(List<EntityUser> entityUser) {
        if (entityUser == null || entityUser.isEmpty() == true) {
            return null;
        }
        List<EntityRole> roles = new ArrayList<>();
        for (int ii = 0; ii < entityUser.size(); ++ii) {
            if (entityUser.get(ii).getIdRole() != null && entityUser.get(ii).getNameRole() != null) {
                EntityRole role = EntityRole
                        .builder()
                        .id(entityUser.get(ii).getIdRole())
                        .name(entityUser.get(ii).getNameRole())
                        .build();
                roles.add(role);
            }
        }

        return builder()
                .id(entityUser.get(0).getId())
                .username(entityUser.get(0).getUsername())
                .password(entityUser.get(0).getPassword())
                .email(entityUser.get(0).getEmail())
                .phone(entityUser.get(0).getPhone())
                .roles(roles)
                .status(entityUser.get(0).getStatus())
                .createdBy(entityUser.get(0).getCreatedBy())
                .createdAt(entityUser.get(0).getCreatedAt())
                .updatedBy(entityUser.get(0).getUpdatedBy())
                .updatedAt(entityUser.get(0).getUpdatedAt())
                .isDeleted(entityUser.get(0).isDeleted())
                .deletedBy(entityUser.get(0).getDeletedBy())
                .deletedAt(entityUser.get(0).getDeletedAt())
                .build();
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (EntityRole role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Override
    public boolean isEnabled() {
        if (isDeleted() == false) {
            return true;
        }
        else if (isDeleted() == true){
            return false;
        }
        else {
            return false;
        }
    }

    @Override
    public String toString() {
        return UtilJson.convertObjectToJsonString(this);
    }
}
