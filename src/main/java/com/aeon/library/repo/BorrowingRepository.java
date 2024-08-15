package com.aeon.library.repo;

import com.aeon.library.entity.Borrowing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, Long>, JpaSpecificationExecutor<Borrowing> {
    @Query(value = "select * from borrowing where borrower = :borrower and", nativeQuery = true)
    List<Borrowing> findOutStandingBooks(@Param("borrower") String borrower);

    @Query(value = "select * from borrowing where book_id = :id and return_date is null", nativeQuery = true)
    Optional<Borrowing> findBookInBorrowedStatus(@Param("id") Long id);

}
