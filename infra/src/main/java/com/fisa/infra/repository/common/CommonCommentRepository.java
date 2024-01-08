package com.fisa.infra.repository.common;


import com.fisa.infra.domain.entity.Account;
import com.fisa.infra.domain.entity.Comment;

/*
* 해당 인터페이스를 통해서 메서드를 명시할 때 사용합니다.
* */
public interface CommonCommentRepository {

    Comment findById(Long id);

}
