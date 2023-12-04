package school.sptech.lcsports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.lcsports.domain.CurtidaPostagem;

import java.util.Optional;

@Repository
public interface CurtidaRepository extends JpaRepository<CurtidaPostagem, Integer> {
    Optional<CurtidaPostagem> findByUsuarioIdUsuarioAndPostagemIdPostagem(int idUsuario, int idPostagem);
}
