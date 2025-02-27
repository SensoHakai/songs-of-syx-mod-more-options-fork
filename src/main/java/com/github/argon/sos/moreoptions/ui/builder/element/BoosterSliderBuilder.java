package com.github.argon.sos.moreoptions.ui.builder.element;

import com.github.argon.sos.moreoptions.Dictionary;
import com.github.argon.sos.moreoptions.game.ui.Slider;
import com.github.argon.sos.moreoptions.ui.builder.BuildResult;
import com.github.argon.sos.moreoptions.ui.builder.Translatable;
import com.github.argon.sos.moreoptions.ui.builder.UiBuilder;
import init.sprite.SPRITES;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import snake2d.util.gui.GuiSection;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class BoosterSliderBuilder implements UiBuilder<List<GuiSection>, Slider> {
    private final Definition definition;

    public BuildResult<List<GuiSection>, Slider> build() {

        if (definition.getLabelWidth() > 0) {
            definition.getLabelDefinition().setMaxWidth(definition.getLabelWidth());
        }

        GuiSection label = LabelBuilder.builder()
            .translate(definition.getLabelDefinition())
            .build().getResult();
        label.pad(10, 5);

        Slider slider = SliderBuilder.builder()
            .definition(definition.getSliderDefinition())
            .build().getResult();
        slider.pad(10, 5);

        List<GuiSection> row = Stream.of(
            label,
            slider
        ).collect(Collectors.toList());

        return BuildResult.<List<GuiSection>, Slider>builder()
            .result(row)
            .interactable(slider)
            .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        @lombok.Setter
        @Accessors(fluent = true)
        private Definition definition;

        public Builder translate(Definition definition) {
            Dictionary dictionary = Dictionary.getInstance();
            dictionary.translate(definition.getLabelDefinition());

            return definition(definition);
        }

        public BuildResult<List<GuiSection>, Slider> build() {
            assert definition != null : "definition must not be null";

            return new BoosterSliderBuilder(definition).build();
        }
    }

    @Data
    @lombok.Builder
    public static class Definition implements Translatable {

        private LabelBuilder.Definition labelDefinition;

        private SliderBuilder.Definition sliderDefinition;


        @lombok.Builder.Default
        private int labelWidth = 0;

        @Override
        public String getKey() {
            return labelDefinition.getKey();
        }

        @Override
        public boolean isTranslate() {
            return labelDefinition.isTranslate();
        }

        @Override
        public void setTitle(String title) {
            labelDefinition.setTitle(title);
        }

        @Override
        public void setDescription(String description) {
            labelDefinition.setDescription(description);
        }
    }
}
