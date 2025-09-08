package com.pi4j.library.gpiod.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <p>GpioLine</p>
 *
 * @author Alexander Liggesmeyer (<a href="https://alexander.liggesmeyer.net/">https://alexander.liggesmeyer.net/</a>)
 * @version $Id: $Id
 */
public class GpioLine extends CWrapper {
    private static final Logger logger = LoggerFactory.getLogger(GpioLine.class);
    private final int offset;

    GpioLine(int offset, long cPointer) {
        super(cPointer);
        this.offset = offset;
    }

    public int getOriginalOffset() {
        return this.offset;
    }

    public int getOffset() {
        return GpioD.lineGetOffset(getCPointer());
    }

    public String getName() {
        return GpioD.lineGetName(getCPointer());
    }

    public String getConsumer() {
        return GpioD.lineGetConsumer(getCPointer());
    }

    public LineDirection getDirection() {
        return GpioD.lineGetDirection(getCPointer());
    }

    public LineActiveState getActiveState() {
        return GpioD.lineGetActiveState(getCPointer());
    }

    public LineBias getBias() {
        return GpioD.lineGetBias(getCPointer());
    }

    public boolean isUsed() {
        return GpioD.lineIsUsed(getCPointer());
    }

    public boolean isOpenDrain() {
        return GpioD.lineIsOpenDrain(getCPointer());
    }

    public boolean isOpenSource() {
        return GpioD.lineIsOpenSource(getCPointer());
    }

    public void update() {
        GpioD.lineUpdate(getCPointer());
    }

    public void requestInput(String consumer) {
        GpioD.lineRequestInput(getCPointer(), consumer);
    }

    public void requestOutput(String consumer, int defaultVal) {
        GpioD.lineRequestOutput(getCPointer(), consumer, defaultVal);
    }

    public void requestRisingEdgeEvents(String consumer) {
        GpioD.lineRequestRisingEdgeEvents(getCPointer(), consumer);
    }

    public void requestFallingEdgeEvents(String consumer) {
        GpioD.lineRequestFallingEdgeEvents(getCPointer(), consumer);
    }

    public void requestBothEdgeEvents(String consumer) {
        GpioD.lineRequestBothEdgeEvents(getCPointer(), consumer);
    }

    public void requestInputFlags(String consumer, int flags) {
        GpioD.lineRequestInputFlags(getCPointer(), consumer, flags);
    }

    public void requestOutputFlags(String consumer, int flags, int defaultValue) {
        GpioD.lineRequestOutputFlags(getCPointer(), consumer, flags, defaultValue);
    }

    public void requestRisingEdgeEventsFlags(String consumer, int flags) {
        GpioD.lineRequestRisingEdgeEventsFlags(getCPointer(), consumer, flags);
    }

    public void requestFallingEdgeEventsFlags(String consumer, int flags) {
        GpioD.lineRequestFallingEdgeEventsFlags(getCPointer(), consumer, flags);
    }

    public void requestBothEdgeEventsFlags(String consumer, int flags) {
        GpioD.lineRequestBothEdgeEventsFlags(getCPointer(), consumer, flags);
    }

    public boolean isRequested() {
        return GpioD.lineIsRequested(getCPointer());
    }

    public boolean isFree() {
        return GpioD.lineIsFree(getCPointer());
    }

    public int getValue() {
        return GpioD.lineGetValue(getCPointer());
    }

    public void setValue(int value) {
        GpioD.lineSetValue(getCPointer(), value);
    }

    public void setConfig(LineRequest direction, int flags, int value) {
        GpioD.lineSetConfig(getCPointer(), direction, flags, value);
    }

    public void setFlags(int flags) {
        GpioD.lineSetFlags(getCPointer(), flags);
    }

    public void setDirectionInput() {
        GpioD.lineSetDirectionInput(getCPointer());
    }

    public void setDirectionOutput(int value) {
        GpioD.lineSetDirectionOutput(getCPointer(), value);
    }

    public boolean eventWait(long timeoutNs) {
        return GpioD.lineEventWait(getCPointer(), timeoutNs);
    }

    public GpioLineEvent eventRead(GpioLineEvent lineEvent) {
        GpioD.lineEventRead(getCPointer(), lineEvent.getCPointer());
        return lineEvent;
    }
}
