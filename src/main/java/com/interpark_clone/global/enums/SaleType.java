package com.interpark_clone.global.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SaleType {
    NORMAL("일반예매"),
    EXCLUSIVE("단독판매"),
    MEMBERSHIP_PRE_SALE("멤버십 선예매"),
    EARLY_BIRD("얼리버드");

    private final String description;
}
