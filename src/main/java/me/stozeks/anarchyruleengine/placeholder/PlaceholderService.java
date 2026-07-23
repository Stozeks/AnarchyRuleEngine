package me.stozeks.anarchyruleengine.placeholder;

import me.stozeks.anarchyruleengine.model.InteractionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PlaceholderService {

    private static final Pattern PLACEHOLDER_PATTERN =
            Pattern.compile("%([^%]+)%");

    private final List<PlaceholderResolver> resolvers;

    public PlaceholderService(
            List<PlaceholderResolver> resolvers
    ) {
        Objects.requireNonNull(
                resolvers,
                "resolvers"
        );

        this.resolvers = Collections.unmodifiableList(
                new ArrayList<>(resolvers)
        );
    }

    public String replace(
            String text,
            InteractionContext context
    ) {
        Objects.requireNonNull(
                text,
                "text"
        );

        Objects.requireNonNull(
                context,
                "context"
        );

        Matcher matcher =
                PLACEHOLDER_PATTERN.matcher(text);

        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String placeholder = matcher.group(1);

            String replacement = resolvePlaceholder(
                    placeholder,
                    context
            );

            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement(replacement)
            );
        }

        matcher.appendTail(result);

        return result.toString();
    }

    private String resolvePlaceholder(
            String placeholder,
            InteractionContext context
    ) {
        for (PlaceholderResolver resolver : resolvers) {
            if (resolver.supports(placeholder)) {
                String resolvedValue =
                        resolver.resolve(
                                placeholder,
                                context
                        );

                return resolvedValue == null
                        ? ""
                        : resolvedValue;
            }
        }

        return "%" + placeholder + "%";
    }

    public List<PlaceholderResolver> getResolvers() {
        return resolvers;
    }
}