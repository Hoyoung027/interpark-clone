package com.interpark_clone.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {
    CARD("카드"),
    BANK_TRANSFER("계좌이체"),
    VIRTUAL_ACCOUNT("가상계좌"),
    KAKAO_PAY("카카오페이"),
    NAVER_PAY("네이버페이"),
    TOSS_PAY("토스페이");

    private final String description;
}
