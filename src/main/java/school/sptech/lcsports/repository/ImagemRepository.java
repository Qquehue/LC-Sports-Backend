package school.sptech.lcsports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import school.sptech.lcsports.domain.Imagem;

@Repository
public interface ImagemRepository extends JpaRepository<Imagem, Integer> {
}
