package com.pi4j.boardinfo.checker;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public record CheckerResult(String title, List<Check> results) {
    public void addResult(Check result) {
        results.add(result);
    }

    record Check(ResultStatus resultStatus, String command, String expected, String result) {
    }

    enum ResultStatus {
        PASS, FAIL, TO_EVALUATE, UNDEFINED
    }

    public String logOutput() {
        var log = new StringBuilder();
        log.append("Result").append(results.size() > 1 ? "s" : "").append(" from ").append(title).append("\n");
        results.forEach(r -> {
            if (!r.command.isEmpty()) {
                log.append("\n\t")
                    .append(r.command.trim());
            }
            log.append("\n\t\tStatus: ").append(r.resultStatus);
            log.append("\n\t\tExpected: ")
                .append("\n\t\t\t").append(r.expected.isEmpty() ? "-" : r.expected.trim());
            log.append("\n\t\tResult: ")
                .append(r.result.isEmpty() ? "\n\t\t\t-" : Arrays.stream(r.result.trim().split("\n"))
                    .map(l -> "\n\t\t\t" + l)
                    .collect(Collectors.joining()));
            log.append("\n");
        });
        return log.toString();
    }
}
