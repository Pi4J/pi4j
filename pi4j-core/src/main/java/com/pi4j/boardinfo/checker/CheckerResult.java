package com.pi4j.boardinfo.checker;

import java.util.List;

public record CheckerResult(String title, List<Check> results) {
    record Check(String command, String result) {
    }

    public String logOutput() {
        var log = new StringBuilder();
        log.append("Result from: ").append(title);
        results.forEach(r -> {
            if (!r.command.isEmpty()) {
                log.append("\n\t")
                    .append(r.command);
            }
            if (!r.result.isEmpty()) {
                log.append("\n\t\t")
                    .append(r.result)
                    .append("\n");
            }
        });
        return log.toString();
    }
}
