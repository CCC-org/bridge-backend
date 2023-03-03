package ccc.keewedomain.service.insight;

import ccc.keewecore.consts.KeeweRtnConsts;
import ccc.keewecore.exception.KeeweException;
import ccc.keewedomain.dto.insight.CommentCreateDto;
import ccc.keewedomain.dto.insight.CommentDeleteDto;
import ccc.keewedomain.persistence.domain.insight.Comment;
import ccc.keewedomain.persistence.domain.insight.Insight;
import ccc.keewedomain.persistence.domain.user.User;
import ccc.keewedomain.persistence.repository.insight.CommentQueryRepository;
import ccc.keewedomain.persistence.repository.insight.CommentRepository;
import ccc.keewedomain.persistence.repository.utils.CursorPageable;
import ccc.keewedomain.service.user.UserDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentDomainService {

    private final CommentRepository commentRepository;
    private final CommentQueryRepository commentQueryRepository;

    private final InsightDomainService insightDomainService;
    private final UserDomainService userDomainService;

    public Comment create(CommentCreateDto dto) {
        Insight insight = insightDomainService.getByIdOrElseThrow(dto.getInsightId());
        User writer = userDomainService.getUserByIdOrElseThrow(dto.getWriterId());

        Optional<Comment> optParent = findByIdAndInsightId(dto.getParentId(), dto.getInsightId());
        optParent.ifPresent(this::validateHasNoParent);
        Comment parent = optParent.orElse(null);

        Comment comment = Comment.of(insight, writer, parent, dto.getContent());
        return commentRepository.save(comment);
    }

    public Long delete(CommentDeleteDto dto) {
        Comment comment = commentRepository.findById(dto.getCommentId()).orElseThrow(() -> {
            throw new KeeweException(KeeweRtnConsts.ERR442);
        });

        if (!Objects.equals(comment.getWriter().getId(), dto.getUserId()))
            throw new KeeweException(KeeweRtnConsts.ERR448);

        comment.delete();
        return comment.getId();
    }

    private Optional<Comment> findByIdAndInsightId(Long id, Long insightId) {
        return commentRepository.findByIdAndInsightId(id, insightId);
    }

    private void validateHasNoParent(Comment comment) {
        if (comment.getParent() != null) {
            throw new KeeweException(KeeweRtnConsts.ERR443);
        }
    }

    public List<Comment> getRepresentativeCommentsWithWriter(Long insightId) {
        final Long commentNumber = 3L;

        List<Comment> comments = commentQueryRepository.findByReplyNumberDescWithUser(insightId, 1L);
        if (comments.isEmpty()) {
            comments = commentQueryRepository.findByInsightIdOrderByIdDesc(insightId, CursorPageable.of(Long.MAX_VALUE, commentNumber));
        }

        return comments;
    }

    public Long getCommentNumberByInsightId(Long insightId) {
        return commentQueryRepository.countByInsightId(insightId);
    }

    public Map<Long, Long> getReplyNumbers(List<Comment> parents) {
        return commentQueryRepository.getReplyNumbers(parents);
    }

    public List<Comment> getComments(Long insightId, CursorPageable<Long> cPage) {
        return commentQueryRepository.findByInsightIdOrderByIdDesc(insightId, cPage);
    }

    public Map<Long, Comment> getFirstReplies(List<Comment> parents) {
        return commentQueryRepository.findFirstRepliesWithWriter(parents);
    }

    public List<Comment> getReplies(Long parentId, CursorPageable<Long> cPage) {
        return commentQueryRepository.findRepliesWithWriter(parentId, cPage);
    }
}
