package com.jojodu.book.springboot.web;

import com.jojodu.book.springboot.config.auth.LoginUser;
import com.jojodu.book.springboot.config.auth.dto.SessionUser;
import com.jojodu.book.springboot.service.posts.PostsService;
import com.jojodu.book.springboot.web.dto.PostsResponseDto;
import com.jojodu.book.springboot.web.dto.PostUpdateRequestDto;
import com.jojodu.book.springboot.web.dto.PostsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostsApiController {

    /**
     * 기존 스프링에서는 Controller 와 Service에서 @Autowired 를 통해서 bean을 주입받으나 여기선 안씀.
     * 대신 생성자를 통해 주입받는다. 생성자는
     * @RequiredArgsConstructor 롬복 어노테이션을 통해서 자동으로 생성된다.
     * (클래스에서 final 이 선언된 모든 필드를 인자값으로 하는 생성자를 생성해줌.)
     */
    private final PostsService postsService;

    //게시글 조회
    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById (@PathVariable Long id){
        return postsService.findById(id);
    }

    //게시글 저장, id return
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto, @LoginUser SessionUser user){
        return postsService.save(requestDto, user);
    }

    //게시글 수정
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto, @LoginUser SessionUser user){
        return postsService.update(id,requestDto, user);
    }

    //게시글 삭제
    @DeleteMapping("/api/v1/posts/{id}")
    public Long Delete(@PathVariable Long id, @LoginUser SessionUser user){
        return postsService.delete(id, user);
    }
}

