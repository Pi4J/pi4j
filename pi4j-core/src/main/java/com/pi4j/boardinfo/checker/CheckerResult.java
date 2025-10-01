package com.pi4j.boardinfo.checker;

import java.util.List;

public record CheckerResult(String title, List<Check> results) {
    record Check(ResultStatus resultStatus, String command, String expected, String result) {
    }

    enum ResultStatus {
        PASS, FAIL, TO_EVALUATE, UNDEFINED
    }

    public String logOutput() {
        var log = new StringBuilder();
        log.append("Result from ").append(title);
        results.forEach(r -> {
            if (!r.command.isEmpty()) {
                log.append("\n\t")
                    .append(r.command.trim());
            }
            log.append("\n\t\tStatus: ").append(r.resultStatus);
            log.append("\n\t\tExpected: ")
                .append(r.expected.isEmpty() ? "-" : r.expected.trim());
            log.append("\n\t\tResult: ")
                .append(r.result.isEmpty() ? "-" : r.result.trim().replace("\n", " - "));
        });
        return log.toString();
    }
}
