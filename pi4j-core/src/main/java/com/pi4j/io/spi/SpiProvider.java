package com.pi4j.io.spi;


import com.pi4j.provider.Provider;

/**
 * Provider that creates and manages {@link Spi} device instances from an {@link SpiConfig}.
 * Platform-specific Pi4J plugins implement this interface (typically via {@link SpiProviderBase})
 * to back SPI communication with concrete hardware or kernel drivers.
 */
public interface SpiProvider extends Provider<SpiProvider, Spi, SpiConfig> {

//    default <T extends Spi> T create(SpiConfigBuilder builder) {
//        return (T)create(builder.build());
//    }

//    default <T extends Spi> T create(Integer address) {
//        var config = Spi.newConfigBuilder()
//                .address(address)
//                .build();
//        return (T)create(config);
//    }
//
//    default <T extends Spi> T create(Integer address, String id) {
//        var config = Spi.newConfigBuilder()
//                .address(address)
//                .id(id)
//                .build();
//        return (T)create(config);
//    }
//
//    default <T extends Spi> T create(Integer address, String id, String name) {
//        var config = Spi.newConfigBuilder()
//                .address(address)
//                .id(id)
//                .name(name)
//                .build();
//        return (T)create(config);
//    }
//
//    default <T extends Spi> T create(Integer address, String id, String name, String description) {
//        var config = Spi.newConfigBuilder()
//                .address(address)
//                .id(id)
//                .name(name)
//                .description(description)
//                .build();
//        return (T)create(config);
//    }

}
