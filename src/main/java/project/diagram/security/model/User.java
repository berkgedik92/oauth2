package project.diagram.security.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import javax.validation.constraints.NotNull;

@Getter
@Setter
public class User {

    private String name;

    @Id
    private String email;

    private String imageUrl;

    @JsonIgnore
    private String password;

    @NotNull
    private AuthProviderType provider;

    private String providerId;
}
