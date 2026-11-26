package tungnn.tutor.java.tool.browsersetup.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import tungnn.tutor.java.tool.browsersetup.core.WindowBounds;

class WindowLayoutUtilsTest {

    private static final WindowBounds SCREEN = new WindowBounds(0, 25, 1440, 875);

    @Test
    void singleDriverTakesTheWholeScreen() {
        assertThat(WindowLayoutUtils.grid(SCREEN, 1, 0)).isEqualTo(new WindowBounds(0, 25, 1440, 875));
    }

    @Test
    void fourDriversFormTwoByTwo() {
        assertThat(WindowLayoutUtils.columns(4)).isEqualTo(2);
        assertThat(WindowLayoutUtils.rows(4)).isEqualTo(2);
        assertThat(WindowLayoutUtils.grid(SCREEN, 4, 0)).isEqualTo(new WindowBounds(0, 25, 720, 437));
        assertThat(WindowLayoutUtils.grid(SCREEN, 4, 3)).isEqualTo(new WindowBounds(720, 462, 720, 437));
    }

    @Test
    void tenDriversFormFourByThree() {
        assertThat(WindowLayoutUtils.columns(10)).isEqualTo(4);
        assertThat(WindowLayoutUtils.rows(10)).isEqualTo(3);
        assertThat(WindowLayoutUtils.grid(SCREEN, 10, 9)).isEqualTo(new WindowBounds(360, 25 + 2 * 291, 360, 291));
    }

    @Test
    void twoDriversAreSideBySide() {
        assertThat(WindowLayoutUtils.grid(SCREEN, 2, 1)).isEqualTo(new WindowBounds(720, 25, 720, 875));
    }

    @Test
    void invalidArgumentsAreRejected() {
        assertThatThrownBy(() -> WindowLayoutUtils.grid(SCREEN, 0, 0)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> WindowLayoutUtils.grid(SCREEN, 2, 2)).isInstanceOf(IllegalArgumentException.class);
    }
}
