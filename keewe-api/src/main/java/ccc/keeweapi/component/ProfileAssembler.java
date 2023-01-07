package ccc.keeweapi.component;

import ccc.keeweapi.dto.user.*;
import ccc.keeweapi.utils.SecurityUtil;
import ccc.keewedomain.dto.user.FollowCheckDto;
import ccc.keewedomain.dto.user.FollowToggleDto;
import ccc.keewedomain.dto.user.OnboardDto;
import ccc.keewedomain.dto.user.UploadProfilePhotoDto;
import ccc.keewedomain.persistence.domain.common.Interest;
import ccc.keewedomain.persistence.domain.title.Title;
import ccc.keewedomain.persistence.domain.title.TitleAchievement;
import ccc.keewedomain.persistence.domain.user.Follow;
import ccc.keewedomain.persistence.domain.user.User;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ProfileAssembler {

    public OnboardDto toOnboardDto(OnboardRequest request) {
        return OnboardDto.of(SecurityUtil.getUserId(), request.getNickname(), request.getInterests());
    }

    public OnboardResponse toOnboardResponse(User user) {
        return OnboardResponse.of(user.getId(), user.getNickname(), user.getInterests());
    }

    public FollowToggleDto toFollowToggleDto(Long targetId) {
        return FollowToggleDto.of(SecurityUtil.getUserId(), targetId);
    }

    public FollowToggleResponse toFollowToggleResponse(boolean following) {
        return FollowToggleResponse.of(following);
    }

    public FollowCheckDto toFollowCheckDto(Long targetId) {
        return FollowCheckDto.of(targetId, SecurityUtil.getUserId());
    }

    public UploadProfilePhotoDto toUploadProfilePhotoDto(MultipartFile imageFile) {
        return UploadProfilePhotoDto.of(
                SecurityUtil.getUserId(),
                imageFile
        );
    }

    //TODO 프로필 사진, 자기소개 수정
    public ProfileMyPageResponse toProfileMyPageResponse(User user, Boolean isFollowing, Long followerCount, Long followingCount, String challengeName) {
        return ProfileMyPageResponse.of(
                user.getNickname(),
                user.getProfilePhotoURL(),
                user.getRepTitleName(),
                "introduction",
                user.getInterests().stream()
                        .map(Interest::getName)
                        .collect(Collectors.toList()),
                isFollowing,
                followerCount,
                followingCount,
                challengeName
        );
    }

    public MyPageTitleResponse toMyPageTitleResponse(Long total, List<TitleAchievement> achievements) {
        List<AchievedTitleResponse> achievedTitleResponses = achievements.stream()
                .map(this::toAchievedTitleResponse)
                .collect(Collectors.toList());

        return MyPageTitleResponse.of(total, achievedTitleResponses);
    }

    public AchievedTitleResponse toAchievedTitleResponse(TitleAchievement achievement) {
        Title title = achievement.getTitle();
        return AchievedTitleResponse.of(
                title.getId(),
                title.getName(),
                title.getIntroduction(),
                achievement.getCreatedAt()
        );
    }

    public FollowUserResponse toFollowerResponse(User follower, boolean isFollow) {
        return FollowUserResponse.of(
                follower.getId(),
                follower.getNickname(),
                follower.getProfilePhotoURL(),
                follower.getRepTitleName(),
                isFollow);
    }

    public FollowUserListResponse toFollowUserListResponse(List<Follow> follows, List<User> users, Set<Long> followingIdSet) {
        List<FollowUserResponse> followUserResponse = users.stream()
                .map(user -> this.toFollowerResponse(user, followingIdSet.contains(user.getId())))
                .collect(Collectors.toList());

        LocalDateTime cursor = !follows.isEmpty() ? follows.get(follows.size() - 1).getCreatedAt() : null;
        return FollowUserListResponse.of(Optional.ofNullable(cursor), followUserResponse);
    }
}
