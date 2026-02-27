package com.ling.lingaicodegeneration.model.enums;

import lombok.Getter;

@Getter
public enum CodeGenTypeEnum {

    HTML("Single HTML file", "html"),
    MULTI_FILE("Multi-file", "multi_file");

    private final String text;
    private final String value;

    CodeGenTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static CodeGenTypeEnum getEnumByValue(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        for (CodeGenTypeEnum anEnum : CodeGenTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}