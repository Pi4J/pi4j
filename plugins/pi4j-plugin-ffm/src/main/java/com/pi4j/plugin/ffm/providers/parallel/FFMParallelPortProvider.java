package com.pi4j.plugin.ffm.providers.parallel;

import com.pi4j.io.gpio.parallel.ParallelPort;
import com.pi4j.io.gpio.parallel.ParallelPortConfig;
import com.pi4j.io.gpio.parallel.ParallelPortProvider;
import com.pi4j.plugin.ffm.common.FFMPermissionHelper;
import com.pi4j.provider.ProviderBase;

/**
 * FFM GPIO Parallel Port Provider.
 */
public class FFMParallelPortProvider
    extends ProviderBase<ParallelPortProvider, ParallelPort, ParallelPortConfig>
    implements ParallelPortProvider {


    public FFMParallelPortProvider() {
        super("ffm-parallel-port", "FFM API Parallel Port");
        FFMPermissionHelper.checkUserPermissions(this);
    }

    @Override
    public ParallelPort create(ParallelPortConfig config) {
        var mappingLogic = new FFMParallelPort.BitmaskMappingLogic(config.offsets());
        return mappingLogic.isRequired()
            ? new FFMParallelPort.WithBitmaskMappingLogic(context, this, config, mappingLogic)
            : new FFMParallelPort(context, this, config);
    }
}
