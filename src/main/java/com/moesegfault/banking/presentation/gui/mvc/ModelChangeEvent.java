package com.moesegfault.banking.presentation.gui.mvc;

import java.util.Objects;
import java.util.Set;

/**
 * @brief 模型变更事件（Model Change Event），描述模型来源和受影响字段；
 *        Model change event carrying source model and affected field names.
 *
 * @param source 事件来源模型（Source model）。
 * @param changedFields 变更字段集合（Changed field names）。
 */
public record ModelChangeEvent(GuiModel source, Set<String> changedFields) {

    /**
     * @brief 构造并校验模型变更事件（Construct Validated Model Change Event）；
     *        Construct validated event and normalize changed field set.
     *
     * @param source 事件来源模型（Source model）。
     * @param changedFields 变更字段集合（Changed field names）。
     */
    public ModelChangeEvent {
        source = Objects.requireNonNull(source, "source must not be null");
        changedFields = Set.copyOf(Objects.requireNonNull(changedFields, "changedFields must not be null"));
    }
}
