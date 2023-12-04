package school.sptech.lcsports.domain.efi;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class Assinatura {
    @Id
    @Column(name = "id_assinatura")
    private Integer idAssinatura;

    @Column(name = "nome_plano")
    private String nomePlano = "Plano Premium - LC Sports";

    @Column(name = "usuario_id")
    private Integer usuarioId;

    public Assinatura(Integer idAssinatura, Integer usuarioId) {
        this.idAssinatura = idAssinatura;
        this.usuarioId = usuarioId;
    }
}
