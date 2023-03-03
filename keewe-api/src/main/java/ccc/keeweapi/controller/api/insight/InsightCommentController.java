package ccc.keeweapi.controller.api.insight;

import static ccc.keewecore.consts.KeeweConsts.LONG_MAX_STRING;

import ccc.keeweapi.dto.ApiResponse;
import ccc.keeweapi.dto.insight.*;
import ccc.keeweapi.service.insight.command.InsightCommentCommandApiService;
import ccc.keeweapi.service.insight.query.InsightCommentQueryApiService;
import ccc.keewedomain.persistence.repository.utils.CursorPageable;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class InsightCommentController {

    private final InsightCommentCommandApiService insightCommentCommandApiService;
    private final InsightCommentQueryApiService insightCommentQueryApiService;

    @PostMapping
    public ApiResponse<CommentCreateResponse> create(@RequestBody @Valid CommentCreateRequest request) {
        return ApiResponse.ok(insightCommentCommandApiService.create(request));
    }

    @DeleteMapping
    public ApiResponse<CommentDeleteResponse> delete(@RequestBody CommentDeleteRequest request) {
        return ApiResponse.ok(insightCommentCommandApiService.delete(request));
    }

    @GetMapping("/representative/insights/{insightId}")
    public ApiResponse<RepresentativeCommentResponse> getRepresentativeComments(@PathVariable Long insightId) {
        return ApiResponse.ok(insightCommentQueryApiService.getRepresentativeComments(insightId));
    }

    @GetMapping("/insights/{insightId}")
    public ApiResponse<List<CommentResponse>> getComments(
            @PathVariable Long insightId,
            @RequestParam(required = false, defaultValue = LONG_MAX_STRING) Long cursor,
            @RequestParam Long limit) {

        return ApiResponse.ok(insightCommentQueryApiService.getCommentsWithFirstReply(insightId, CursorPageable.of(cursor, limit)));
    }

    @GetMapping("{parentId}/replies")
    public ApiResponse<List<ReplyResponse>> getReplies(
            @PathVariable Long parentId,
            @RequestParam(required = false, defaultValue = LONG_MAX_STRING) Long cursor,
            @RequestParam Long limit) {

        return ApiResponse.ok(insightCommentQueryApiService.getReplies(parentId, CursorPageable.of(cursor, limit)));
    }
}
