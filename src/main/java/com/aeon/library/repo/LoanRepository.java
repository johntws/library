package com.aeon.library.repo;

import com.aeon.library.entity.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long>, JpaSpecificationExecutor<Loan> {
    @Query(value = "select * from loan where borrower = :borrower and", nativeQuery = true)
    List<Loan> findOutStandingBooks(@Param("borrower") String borrower);

    @Query(value = "select * from loan where copy_id = :id and return_date is null", nativeQuery = true)
    Optional<Loan> findLoanedCopy(@Param("id") Long id);

}
