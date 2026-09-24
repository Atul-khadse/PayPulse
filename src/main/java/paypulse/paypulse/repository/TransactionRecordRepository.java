package paypulse.paypulse.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import paypulse.paypulse.model.TransationRecord;

import java.util.Optional;

@Repository
public interface TransactionRecordRepository  extends JpaRepository<TransationRecord, Long> {

    Optional<TransationRecord> findByIdempotencyKey(String idempotencyKey);

}
