package com.aeon.library.repo;

import com.aeon.library.entity.Copy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CopyRepository extends JpaRepository<Copy, Long>, JpaSpecificationExecutor<Copy> {
    Optional<Copy> findByIdAndDeletedFalse(@Param("id") Long id);
}
