package com.sberbank.cms.ui.sidebar.planning.offers;

import com.sberbank.cms.backend.Offer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@RequiredArgsConstructor
@Getter
public class OfferModifiedEvent implements Serializable {
    private final Offer offer;
}