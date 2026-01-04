package com.github.wargulwb.pixelcross;

import java.util.Objects;

import com.github.wargulwb.pixelcross.cli.Arguments;
import com.github.wargulwb.pixelcross.config.PixelCrossConfig;
import com.github.wargulwb.pixelcross.model.ImageModelLoader;
import com.github.wargulwb.pixelcross.utils.BooleanConverter;
import com.github.wargulwb.pixelcross.utils.ColorConverter;
import com.github.wargulwb.pixelcross.utils.ColorUtils;
import com.github.wargulwb.pixelcross.utils.JDOMUtils;
import com.google.inject.AbstractModule;

public class DependenciesModule extends AbstractModule {

    private final Arguments arguments;

    public DependenciesModule(final Arguments arguments) {
        this.arguments = Objects.requireNonNull(arguments, getClass().getSimpleName() + ".arguments cannot be null!");
    }

    @Override
    protected void configure() {
        bind(Arguments.class).toInstance(arguments);
        bind(BooleanConverter.class).asEagerSingleton();
        bind(ColorUtils.class).asEagerSingleton();
        bind(ColorConverter.class).asEagerSingleton();
        bind(JDOMUtils.class).asEagerSingleton();
        bind(PixelCrossConfig.class).asEagerSingleton();
        bind(ImageModelLoader.class).asEagerSingleton();
        bind(PatternPainter.class).asEagerSingleton();
        bind(CrossStitchPainter.class).asEagerSingleton();
        bind(YarnLegendPainter.class).asEagerSingleton();
        bind(CombinedPainter.class).asEagerSingleton();
    }

}
