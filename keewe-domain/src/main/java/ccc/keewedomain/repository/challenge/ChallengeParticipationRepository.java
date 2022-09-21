package ccc.keewedomain.repository.challenge;

import ccc.keewedomain.domain.challenge.ChallengeParticipation;
import ccc.keewedomain.domain.challenge.enums.ChallengeParticipationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChallengeParticipationRepository extends
        JpaRepository<ChallengeParticipation, Long>,
        ChallengeParticipationQueryRepository {
    Optional<ChallengeParticipation> findByChallengerIdAndStatus(Long userId, ChallengeParticipationStatus status);
}
