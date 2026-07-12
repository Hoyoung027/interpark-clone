package com.interpark_clone.domain.venue.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum City {
    SEOUL("서울특별시"),
    GYEONGGI("경기도"),
    INCHEON("인천광역시"),
    GYEONGNAM("경상남도"),
    BUSAN("부산광역시"),
    GYEONGBUK("경상북도"),
    DAEGU("대구광역시"),
    JEONNAM("전라남도"),
    GWANGJU("광주광역시"),
    JEONBUK("전북특별자치도"),
    CHUNGNAM("충청남도"),
    DAEJEON("대전광역시"),
    CHUNGBUK("충청북도"),
    GANGWON("강원특별자치도"),
    JEJU("제주특별자치도"),
    ULSAN("울산광역시");

    private final String description;
}
