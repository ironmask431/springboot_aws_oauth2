package com.jojodu.book.springboot.config.auth;

import com.jojodu.book.springboot.config.auth.dto.OAuthAttributes;
import com.jojodu.book.springboot.config.auth.dto.SessionUser;
import com.jojodu.book.springboot.domain.user.User;
import com.jojodu.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;
import java.util.Collections;

/**
 * OAuth2 로그인 이후 가져온 사용자의 정보(email, name, picture 등)
 * 을 기반으로 가입, 정보수정, 세션저장 등의 기능 담당
 * OAuth2UserService 인터페이스를 구현
 */
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;
    private final HttpSession httpSession;

    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        //OAuth2UserService 를 통해 oAuth2User 정보를 가져옴.

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        //현재 로그인 진행중인 서비스를 구분하는 코드. (구글, 네이버, 카카오등)

        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        //OAuth2 로그인 진행 시 키가되는 필드값. PK와 같은 의미
        //구글의 경우 기본적으로 코드지원하지만, 네이버,카카오는 지원하지않음 구글의 기본코드는 "sub"
        //이후 네이버로그인과 구글로그인을 동시 지원할 때 사용됩니다.

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName,oAuth2User.getAttributes());
        //OAuthAttributes = OAuth2UserService 를 통해서 가져온 oAuth2User의 attribute를 담을 클래스입니다.
        //이후 네이버등 다른 소셜로그인도 이 클래스를 사용합니다.

        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user",new SessionUser(user));
        //SessionUser = 세션에 사용자정보 담기위한 클래스

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey()))
                ,attributes.getAttributes()
                ,attributes.getNameAttributeKey());
    }

    //구글 사용자정보가 업데이트되었을때를 대비하여 update 기능도 같이구현.
    //사용자의 이름, 프로필사진이 변경되면 User엔티티에 반영됨
    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }

}
