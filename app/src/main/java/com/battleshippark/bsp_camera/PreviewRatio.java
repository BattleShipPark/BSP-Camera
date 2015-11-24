package com.battleshippark.bsp_camera;

/**
 */
public enum PreviewRatio {
    RATIO_16_9("16:9", 16f / 9),
    RATIO_4_3("4:3", 4f / 3),
    RATIO_1_1("1:1", 1);

    private String text;
    private float ratio;

    PreviewRatio(String text, float ratio) {
        this.text = text;
        this.ratio = ratio;
    }

    public String getText() {
        return text;
    }

    public static PreviewRatio of(String text) {
        for (PreviewRatio ratio : values()) {
            if (ratio.text.equals(text))
                return ratio;
        }
        throw new RuntimeException();
    }

    public float getRatio() {
        return ratio;
    }
}
