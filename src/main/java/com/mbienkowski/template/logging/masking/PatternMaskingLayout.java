package com.mbienkowski.template.logging.masking;


import static com.mbienkowski.template.logging.masking.MaskingUtil.getMaskPattern;
import static com.mbienkowski.template.logging.masking.MaskingUtil.maskMessageWithPattern;

import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import java.util.regex.Pattern;

public class PatternMaskingLayout extends PatternLayout {

    Pattern maskPatterns;

    public void addMaskJsonFields(String jsonFields) {
        this.maskPatterns = getMaskPattern(jsonFields.split(",\\s*"));
    }

    @Override
    public String doLayout(ILoggingEvent event) {
        return maskMessageWithPattern(super.doLayout(event), maskPatterns);
    }

}
