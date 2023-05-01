package ccc.keeweapi.service.insight.command;

import ccc.keeweapi.component.ReactionAssembler;
import ccc.keeweapi.utils.BlockedResourceManager;
import ccc.keeweapi.dto.insight.request.ReactRequest;
import ccc.keeweapi.dto.insight.response.ReactResponse;
import ccc.keewedomain.dto.insight.ReactionDto;
import ccc.keewedomain.service.insight.ReactionDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InsightReactionCommandApiService {
    private final ReactionAssembler reactionAssembler;
    private final ReactionDomainService reactionDomainService;
    private final BlockedResourceManager blockedResourceManager;

    public ReactResponse react(ReactRequest request) {
        blockedResourceManager.validateAccessibleInsight(request);
        ReactionDto reactionDto = reactionDomainService.react(reactionAssembler.toReactionIncrementDto(request));
        return reactionAssembler.toReactResponse(reactionDto);
    }
}
