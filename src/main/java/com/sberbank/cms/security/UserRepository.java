package com.sberbank.cms.security;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<UserInfo, Long> {

	UserInfo findByLogin(String login);

	List<UserInfo> findByLoginLikeIgnoreCaseOrNameLikeIgnoreCase(String loginLike, String nameLike);

	long countByLoginLikeIgnoreCaseOrNameLikeIgnoreCase(String loginLike, String nameLike);
}