package school.sptech.lcsports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.lcsports.domain.Avaliacao;
import school.sptech.lcsports.domain.Usuario;

import java.util.List;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, Integer> {
    List<Avaliacao> findByAvaliado(Usuario usuario);

    List<Avaliacao> findByAvaliador(Usuario usuario);
}
