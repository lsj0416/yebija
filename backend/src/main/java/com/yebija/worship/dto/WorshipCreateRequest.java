package com.yebija.worship.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WorshipCreateRequest {

    @NotNull(message = "예배 날짜는 필수입니다.")
    private LocalDate worshipDate;

    private String title;

    private Long templateId;
}
