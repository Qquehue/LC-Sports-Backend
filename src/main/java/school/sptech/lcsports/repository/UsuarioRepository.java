package school.sptech.lcsports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.lcsports.domain.Usuario;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    boolean existsByEmail(String email);

    List<Usuario> findUsuariosByIsPremiumTrue();

    Optional<Usuario> findByIdUsuario(int idUsuario);

    Optional<Usuario> findByEmail(String email);
}
