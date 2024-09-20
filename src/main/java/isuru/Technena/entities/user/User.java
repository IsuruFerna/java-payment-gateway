package isuru.Technena.entities.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
@Entity // Specifies that the class is an entity and is mapped to a database table
@Getter // Generates getter methods for all fields
@Setter // Generates setter methods for all fields
@NoArgsConstructor // Generates a no-argument constructor
@AllArgsConstructor // Generates a constructor with one parameter for each field
@JsonIgnoreProperties({"password", "accountNonLocked", "credentialsNonExpired", "comments"})
public class User implements UserDetails {
    @Id // Specifies the primary key of an entity
    @GeneratedValue
    private UUID id;
    @Column(unique = true) // Specifies the column is unique
    private String email;
    @Column(unique = true)
    private String username;
    private String password;
    @Enumerated(EnumType.STRING)
    private Role role;

    //  These methods are part of the UserDetails interface in Spring Security
    //  They provide necessary information about the user's account status and the authorities
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
