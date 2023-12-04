package school.sptech.lcsports.dto;

import lombok.Getter;
import lombok.Setter;
import school.sptech.lcsports.security.Token;

@Getter
@Setter
public class DtoLogin {

    private Integer id;
    private String email;
    private Token token;

    public DtoLogin(Integer id, String email, Token token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }


}
