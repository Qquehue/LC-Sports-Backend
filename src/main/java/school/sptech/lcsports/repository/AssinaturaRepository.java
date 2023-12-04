package school.sptech.lcsports.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import school.sptech.lcsports.domain.efi.Assinatura;
@Repository
public interface AssinaturaRepository extends JpaRepository<Assinatura, Integer> {
    @Query(value = "SELECT MAX(id) FROM Assinatura")
    Integer getUltimoId();
}
