package ccc.keewedomain.persistence.repository.insight;

import ccc.keewedomain.persistence.domain.insight.Comment;
import ccc.keewedomain.persistence.repository.utils.CursorPageable;
import com.querydsl.core.group.GroupBy;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static ccc.keewedomain.persistence.domain.insight.QComment.comment;
import static ccc.keewedomain.persistence.domain.user.QUser.user;

@Repository
@RequiredArgsConstructor
public class CommentQueryRepository {

    private final JPAQueryFactory queryFactory;

    //최신순으로 limit 개의 댓글 조회 (답글X)
    public List<Comment> findByInsightIdOrderByIdDesc(Long insightId, CursorPageable<Long> cPage) {

        return queryFactory
                .select(comment)
                .from(comment)
                .innerJoin(comment.writer, user)
                .fetchJoin()
                .where(comment.insight.id.eq(insightId)
                        .and(comment.id.lt(cPage.getCursor()))
                        .and(comment.parent.isNull())
                        .and(comment.deleted.isFalse()))
                .limit(cPage.getLimit())
                .fetch();
    }

    //답글이 많은 순서대로 limit 개의 댓글을 작성자와 함께 조회
    public List<Comment> findByReplyNumberDescWithUser(Long insightId, Long limit) {

        return queryFactory
                .select(comment)
                .from(comment)
                .leftJoin(comment.writer, user)
                .fetchJoin()
                .where(comment.id.in(parentIdsReplyDesc(insightId)))
                .limit(limit)
                .fetch();
    }

    //답글이 많은 순서대로 limit 개의 댓글 id 조회
    private JPQLQuery<Long> parentIdsReplyDesc(Long insightId) {

        return JPAExpressions
                .select(comment.parent.id)
                .from(comment)
                .where(comment.insight.id.eq(insightId))
                .groupBy(comment.parent.id)
                .orderBy(comment.id.count().desc());
    }

    //인사이트의 총 댓글 개수 조회
    public Long countByInsightId(Long insightId) {
        return queryFactory
                .select(comment.count())
                .from(comment)
                .where(comment.insight.id.eq(insightId))
                .fetchFirst();
    }

    //최신순으로 cursor 답글 이전의 부모 댓글의 답글 n개를 작성자와 함께 조회
    public List<Comment> findByParentWithWriter(Long parentId, Long cursor, Long limit) {
        return queryFactory
                .select(comment)
                .from(comment)
                .innerJoin(comment.writer, user)
                .fetchJoin()
                .where(comment.parent.id.eq(parentId)
                        .and(comment.id.lt(cursor))
                        .and(comment.deleted.isFalse()))
                .limit(limit)
                .fetch();
    }

    //각 부모 댓글의 첫 답글들을 작성자와 함께 조회
    public Map<Long, Comment> findFirstRepliesWithWriter(List<Comment> parents) {
        return queryFactory
                .from(comment)
                .innerJoin(comment.writer, user)
                .fetchJoin()
                .where(comment.id.in(findFirstReplyIds(parents)))
                .transform(GroupBy.groupBy(comment.parent.id).as(comment));
    }

    public List<Comment> findRepliesWithWriter(Long parentId, CursorPageable<Long> cPage) {
        return queryFactory
                .select(comment)
                .from(comment)
                .innerJoin(comment.writer, user)
                .fetchJoin()
                .where(comment.parent.id.eq(parentId)
                        .and(comment.id.lt(cPage.getCursor()))
                        .and(comment.deleted.isFalse()))
                .limit(cPage.getLimit())
                .fetch();
    }

    //각 부모 댓글의 첫 답글의 id 조회
    private JPQLQuery<Long> findFirstReplyIds(List<Comment> parents) {
        return JPAExpressions
                .select(comment.id.max())
                .from(comment)
                .groupBy(comment.parent.id)
                .where(comment.parent.in(parents));
    }

    public Map<Long, Long> getReplyNumbers(List<Comment> parents) {
        return queryFactory
                .from(comment)
                .groupBy(comment.parent.id)
                .where(comment.parent.in(parents))
                .transform(GroupBy.groupBy(comment.parent.id).as(comment.count()));
    }
}
