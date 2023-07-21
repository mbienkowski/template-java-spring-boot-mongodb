package com.mbienkowski.template.logging.masking;

import static lombok.AccessLevel.PRIVATE;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public class MaskingUtil {

    public static String maskMessageWithPattern(String message, Pattern maskPattern) {
        if (maskPattern == null) {
            return message;
        }

        StringBuilder sb = new StringBuilder(message);
        Matcher m = maskPattern.matcher(sb);

        while (m.find()) {
            IntStream.rangeClosed(1, m.groupCount()).forEach(group -> {
                String matchingGroup = m.group(group);
                if (matchingGroup != null) {
                    IntStream.range(m.start(group), m.end(group)).forEach(i -> sb.setCharAt(i, '*'));
                }
            });
        }

        return sb.toString();
    }

    public static Pattern getMaskPattern(String... jsonFields) {
        List<String> patterns = new ArrayList<>();
        for (String field : jsonFields) {
            patterns.add("\"%s\"\s*:\s*(.*?)".formatted(field));
            patterns.add("\"%s\"\s*:\s*\"(.*?)\"".formatted(field));
            patterns.add("\\\"%s\\\"\s*:\s*\\\"(.*?)\\\"".formatted(field));
        }
        return Pattern.compile(String.join("|", patterns), Pattern.MULTILINE);
    }
}
