package ccc.keewedomain.repository.insight;

public interface DrawerQueryRepository {
    boolean existsByUserIdAndName(Long userId, String name);
}
