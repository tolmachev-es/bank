package com.tolmachev.bank.repository;

import com.tolmachev.bank.repository.entity.LedgerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerRepository extends JpaRepository<LedgerEntity, Long> {

}
