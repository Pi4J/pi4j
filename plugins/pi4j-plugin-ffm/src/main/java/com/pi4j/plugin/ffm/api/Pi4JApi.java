package com.pi4j.plugin.ffm.api;

import com.pi4j.boardinfo.definition.BoardModel;
import com.pi4j.boardinfo.util.BoardInfoHelper;
import com.pi4j.exception.Pi4JException;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class Pi4JApi {

    public interface API {
        default boolean isEqual(Class<?> clazz) {
            return this.getClass().equals(clazz);
        }
    }

    public static void main(String[] args) {
        // Example usage of new API.
        // You can lazily create new specific board with well known protocols and settings
        // We definitely know, that rpi4b with default settings has one i2c, two spi and two pwm,
        // so we can hint the user all of them with specific interfaces.
        // See Model4B class for details
        var model4B = Pi4JApi.board(RaspberryPi.Model4B.class);

        // here we are using i2c1 interface
        try (var i2c1 = model4B.i2c1(0x55)) {
            var value = i2c1.readRegister(0x0A);
            System.out.println(value);
        }

        // we can safely reuse it once closed
        try (var i2c1 = model4B.i2c1(0x1C)) {
            var value = i2c1.readRegister(0x0A);
            System.out.println(value);
        }
    }

    // Temporary map, while core models in BoardHelper are not altered.
    private static final Map<BoardModel, Class<? extends API>> tmpBoardModels = new HashMap<>() {
        {
            put(BoardModel.MODEL_4_B, RaspberryPi.Model4B.class);
            put(BoardModel.COMPUTE_4, RaspberryPi.Cm4.class);
        }
    };

    private static API instance;

    public static <T extends API> T board(Class<T> type) {
        // essentially, if you are requesting the same board, we return the instance already created
        if (instance != null && instance.isEqual(type)) {
            return type.cast(instance);
        }
        var boardModel = BoardInfoHelper.current().getBoardModel();
        var model = tmpBoardModels.entrySet().stream()
            .filter(bm -> bm.getValue().equals(type))
            .findFirst()
            .orElseThrow();
        if (!model.getKey().equals(boardModel)) {
            // will be replaced by warning
            throw new Pi4JException("Board model does not match");
        }

        try {
            // lazy creation
            instance = type.getDeclaredConstructor().newInstance();
            return type.cast(instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new Pi4JException(e);
        }
    }
}