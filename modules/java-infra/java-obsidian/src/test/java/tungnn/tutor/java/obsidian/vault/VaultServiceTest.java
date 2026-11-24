package tungnn.tutor.java.obsidian.vault;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class VaultServiceTest {

    @Test
    void parsesCountsFromObjectOrArray() {
        assertThat(VaultService.parseCounts("{\"#java\":3}")).containsEntry("#java", 3);
        assertThat(VaultService.parseCounts("[{\"tag\":\"#a\",\"count\":2}]")).containsEntry("#a", 2);
        assertThat(VaultService.parseCounts("[{\"tag\":\"#b\",\"count\":\"1\"}]"))
                .containsEntry("#b", 1);
    }
}
