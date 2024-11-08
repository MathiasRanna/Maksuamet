package ee.maksuamet.repository;

import ee.maksuamet.domain.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    // Method to find a company by registry code

    @Query("SELECT c FROM Company c WHERE (LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR c.registryCode LIKE CONCAT('%', :query, '%')) AND c.quarter.year = :year AND c.quarter.quarter = :quarter")
    List<Company> findByNameContainingIgnoreCaseOrRegistryCodeContainingIgnoreCase(@Param("query") String query,
                                                                                   @Param("year") String year,
                                                                                   @Param("quarter") int quarter);

    @Query("SELECT c FROM Company c WHERE c.registryCode = :registryCode AND " +
            "(c.quarter.year < :year OR (c.quarter.year = :year AND c.quarter.quarter < :quarter)) " +
            "ORDER BY c.quarter.year DESC, c.quarter.quarter DESC LIMIT 1")
    Optional<Company> findPreviousQuarterByRegistryCode(@Param("registryCode") String registryCode,
                                                        @Param("year") String year,
                                                        @Param("quarter") int quarter);

    @Query("SELECT c FROM Company c WHERE c.registryCode = :registryCode AND c.quarter.year = :year AND c.quarter.quarter = :quarter")
    Optional<Company> findByRegistryCodeByQuarter(@Param("registryCode") String registryCode, @Param("year") String year, @Param("quarter") int quarter);

}