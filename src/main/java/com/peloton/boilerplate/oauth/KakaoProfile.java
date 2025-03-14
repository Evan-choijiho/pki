package com.peloton.boilerplate.oauth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class KakaoProfile {
    @JsonProperty("id")
    private long id;

    @JsonProperty("properties")
    private Properties properties;

    @JsonProperty("kakao_account")
    private KakaoAccount kakaoAccount;

    @Getter
    @ToString
    public static class Properties {
        @JsonProperty("nickname")
        private String nickname;

        @JsonProperty("profile_image")
        private String profileImage;

        @JsonProperty("thumbnail_image")
        private String thumbnailImage;
    }

    @Getter
    @ToString
    public static class KakaoAccount {
        @JsonProperty("profile_needs_agreement")
        Boolean profileNeedsAgreement;

        @JsonProperty("profile_nickname_needs_agreement")
        Boolean profileNicknameNeedsAgreement;

        @JsonProperty("profile_image_needs_agreement")
        Boolean profileImageNeedsAgreement;

        @JsonProperty("name_needs_agreement")
        Boolean nameNeedsAgreement;

        @JsonProperty("name")
        String name;

        @JsonProperty("email_needs_agreement")
        Boolean emailNeedsAgreement;

        @JsonProperty("is_email_valid")
        Boolean isEmailValid;

        @JsonProperty("is_email_verified")
        Boolean isEmailVerified;

        @JsonProperty("email")
        String email;

        @JsonProperty("age_range_needs_agreement")
        Boolean ageRangeNeedsAgreement;

        @JsonProperty("age_range")
        String ageRange;

        @JsonProperty("birthyear_needs_agreement")
        Boolean birthyearNeedsAgreement;

        @JsonProperty("birthyear")
        String birthyear;

        @JsonProperty("birthday_needs_agreement")
        Boolean birthdayNeedsAgreement;

        @JsonProperty("birthday")
        String birthday;

        @JsonProperty("birthday_type")
        String birthdayType;

        @JsonProperty("gender_needs_agreement")
        Boolean genderNeedsAgreement;

        @JsonProperty("gender")
        String gender;

        @JsonProperty("phone_number_needs_agreement")
        Boolean phoneNumberNeedsAgreement;

        @JsonProperty("phone_number")
        String phoneNumber;

        @JsonProperty("ci_needs_agreement")
        Boolean ciNeedsAgreement;

        @JsonProperty("ci")
        String ci;

        @JsonProperty("profile")
        private Profile profile;

        @Getter
        @ToString
        public static class Profile {
            @JsonProperty("nickname")
            private String nickname;

            @JsonProperty("thumbnail_image_url")
            private String thumbnailImageUrl;

            @JsonProperty("profile_image_url")
            private String profileImageUrl;

            @JsonProperty("is_default_image")
            private Boolean isDefaultImage;
        }
    }
}
