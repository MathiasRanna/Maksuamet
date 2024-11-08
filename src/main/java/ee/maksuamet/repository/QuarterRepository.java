package ee.maksuamet.repository;

import ee.maksuamet.domain.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QuarterRepository extends JpaRepository<Quarter, Long> {
    Optional<Quarter> findByFileName(String fileName);

}
