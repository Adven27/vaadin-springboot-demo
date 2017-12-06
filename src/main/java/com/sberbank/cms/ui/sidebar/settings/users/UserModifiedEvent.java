package com.sberbank.cms.ui.sidebar.settings.users;

import com.sberbank.cms.backend.UserInfo;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class UserModifiedEvent implements Serializable {
    private final UserInfo user;
}