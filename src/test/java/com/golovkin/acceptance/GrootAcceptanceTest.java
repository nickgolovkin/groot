package com.golovkin.acceptance;

import com.golovkin.acceptance.utils.app.log.GrootLogEntry;
import com.golovkin.acceptance.utils.common.log.LogLevel;
import org.junit.jupiter.api.Test;

import static com.golovkin.common.ColorUtils.error;

public class GrootAcceptanceTest extends AbstractAcceptanceTest {
    @Test
    public void cannot_process_command() {
        gitStub().create();

        groot().withProjectEntry("omniutils", "omniutils_dir", "omniutils_url")
                .create();

        groot().run("some unknown command");

        check().assertOutputEqual(
                error("Не удалось обработать команду [some unknown command]")
        );

        check().assertGitRequestsEqual();

        check().assertLogsEqual(
                new GrootLogEntry(LogLevel.ERROR, "Не удалось обработать команду [some unknown command]")
        );
    }
}
