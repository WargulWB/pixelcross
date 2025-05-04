package com.github.wargulwb.pixelcross.model.yarn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class YarnSortiment {

    private final Optional<String> name;
    private final String producer;
    private final List<Yarn> yarns;

    private YarnSortiment(final Builder builder) {
        producer = Objects.requireNonNull(builder.bProducer, getClass().getSimpleName() + ".producer cannot be null!");
        name = Optional.ofNullable(builder.bName);
        yarns = Collections.unmodifiableList(copyList(builder.bYarns));
        yarns.forEach(y -> y.setYarnSortiment(this));
    }

    private static <T> List<T> copyList(final List<T> list) {
        final List<T> result = new ArrayList<>();
        result.addAll(list);
        return result;
    }

    public Optional<String> getName() {
        return name;
    }

    public String getProducer() {
        return producer;
    }

    public List<Yarn> getYarns() {
        return yarns;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append(getClass().getSimpleName())
                .append("[")
                .append("name=")
                .append(name)
                .append(", producer=")
                .append(producer)
                .append(", yarns=")
                .append(yarns)
                .append("]")
                .toString();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static class Builder {

        private String bName;
        private String bProducer;
        private final List<Yarn> bYarns = new ArrayList<>();

        private Builder() {
            // hide constructor
        }

        public Builder withName(final String value) {
            bName = value;
            return this;
        }

        public Builder withProducer(final String value) {
            bProducer = value;
            return this;
        }

        public Builder withYarns(final Collection<Yarn> value) {
            bYarns.clear();
            Optional.ofNullable(value).ifPresent(bYarns::addAll);
            return this;
        }

        public YarnSortiment build() {
            return new YarnSortiment(this);
        }

    }
}
