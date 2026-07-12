package com.interpark_clone.domain.sport.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClubHomeCity {
    SEOUL("서울"),
    INCHEON("인천"),
    DAEJEON("대전"),
    DAEGU("대구"),
    BUSAN("부산"),
    GWANGJU("광주"),
    CHANGWON("창원"),
    SUWON("수원"),
    JEONJU("전주"),
    ULSAN("울산"),
    POHANG("포항"),
    GANGWON("강원"),
    JEJU("제주"),
    GIMCHEON("김천"),
    BUCHEON("부천"),
    ANYANG("안양");

    private final String description;
}
