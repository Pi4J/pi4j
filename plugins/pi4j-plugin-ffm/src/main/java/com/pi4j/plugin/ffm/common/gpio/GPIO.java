package com.pi4j.plugin.ffm.common.gpio;

import io.github.digitalsmile.annotation.NativeMemory;
import io.github.digitalsmile.annotation.NativeMemoryOptions;
import io.github.digitalsmile.annotation.structure.Enum;
import io.github.digitalsmile.annotation.structure.Enums;
import io.github.digitalsmile.annotation.structure.Struct;
import io.github.digitalsmile.annotation.structure.Structs;

@NativeMemory(headers = "/usr/src/linux-headers-${linux-headers}/include/uapi/linux/gpio.h")
@NativeMemoryOptions(processRootConstants = true)
@Structs({
    @Struct(name = "gpiochip_info", javaName = "ChipInfo"),
    @Struct(name = "gpio_v2_line_info", javaName = "LineInfo"),
    @Struct(name = "gpio_v2_line_attribute", javaName = "LineAttribute"),
    @Struct(name = "gpio_v2_line_config", javaName = "LineConfig"),
    @Struct(name = "gpio_v2_line_config_attribute", javaName = "LineConfigAttribute"),
    @Struct(name = "gpio_v2_line_event", javaName = "LineEvent"),
    @Struct(name = "gpio_v2_line_request", javaName = "LineRequest"),
    @Struct(name = "gpio_v2_line_values", javaName = "LineValues")
})
@Enums({
    @Enum(name = "gpio_v2_line_attr_id", javaName = "LineAttributeId")
})
public interface GPIO {
}
