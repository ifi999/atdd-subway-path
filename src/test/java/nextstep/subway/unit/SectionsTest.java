package nextstep.subway.unit;

import nextstep.subway.line.entity.Line;
import nextstep.subway.line.entity.Section;
import nextstep.subway.station.entity.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SectionsTest {

    @Mock
    private Station 강남역;

    @Mock
    private Station 역삼역;

    @Mock
    private Station 선릉역;

    @Mock
    private Line line;

    /**
     * A-B (distance 10)
     * B-C (distance 5)
     * -> A-B-C
     */
    @DisplayName("하행역 기준 다음 역을 추가한다.")
    @Test
    void 하행역_기준_다음_역_추가() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Section newSection = new Section(line, 역삼역, 선릉역, 5);
        when(강남역.getId()).thenReturn(1L);
        when(선릉역.getId()).thenReturn(3L);

        // when
        line.getSections().addSection(newSection);

        // then
        final Section existedSection = line.getSections().getSections().get(0);
        final Section addedSection = line.getSections().getSections().get(1);
        assertThat(existedSection.getUpStation()).isEqualTo(강남역);
        assertThat(existedSection.getDownStation()).isEqualTo(역삼역);
        assertThat(addedSection.getUpStation()).isEqualTo(역삼역);
        assertThat(addedSection.getDownStation()).isEqualTo(선릉역);
    }

    /**
     * A-B (distance 10)
     * C-B (distance 5)
     * -> A-C-B
     */
    @DisplayName("하행역 기준 중간 역을 추가한다.")
    @Test
    void 하행역_기준_중간_역_추가() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Section newSection = new Section(line, 선릉역, 역삼역,  5);
        when(강남역.getId()).thenReturn(1L);

        // when
        line.getSections().addSection(newSection);

        // then
        final Section existedSection = line.getSections().getSections().get(0);
        final Section addedSection = line.getSections().getSections().get(1);
        assertThat(existedSection.getUpStation()).isEqualTo(강남역);
        assertThat(existedSection.getDownStation()).isEqualTo(선릉역);
        assertThat(addedSection.getUpStation()).isEqualTo(선릉역);
        assertThat(addedSection.getDownStation()).isEqualTo(역삼역);
    }

    /**
     * A-B (distance 10)
     * C-B (distance 15)
     * -> C-A-B
     */
    @DisplayName("하행역 기준 이전 역을 추가한다.")
    @Test
    void 하행역_기준_이전_역_추가() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Section newSection = new Section(line, 선릉역, 역삼역,  15);
        when(강남역.getId()).thenReturn(1L);

        // when
        line.getSections().addSection(newSection);

        // then
        final Section existedSection = line.getSections().getSections().get(0);
        final Section addedSection = line.getSections().getSections().get(1);
        assertThat(existedSection.getUpStation()).isEqualTo(선릉역);
        assertThat(existedSection.getDownStation()).isEqualTo(강남역);
        assertThat(addedSection.getUpStation()).isEqualTo(강남역);
        assertThat(addedSection.getDownStation()).isEqualTo(역삼역);
    }

    /**
     * A-B (distance 10)
     * C-A (distance 5)
     * -> C-A-B
     */
    @DisplayName("상행역 기준 이전 역을 추가한다.")
    @Test
    void 상행역_기준_이전_역_추가() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Section newSection = new Section(line, 선릉역, 강남역, 5);

        // when
        line.getSections().addSection(newSection);

        // then
        final Section addedSection = line.getSections().getSections().get(0);
        final Section existedSection = line.getSections().getSections().get(1);
        assertThat(addedSection.getUpStation()).isEqualTo(선릉역);
        assertThat(addedSection.getDownStation()).isEqualTo(강남역);
        assertThat(existedSection.getUpStation()).isEqualTo(강남역);
        assertThat(existedSection.getDownStation()).isEqualTo(역삼역);
    }

    /**
     * A-B (distance 10)
     * A-C (distance 5)
     * -> A-C-B
     */
    @DisplayName("상행역 기준 중간 역을 추가한다.")
    @Test
    void 상행역_기준_중간_역_추가() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Section newSection = new Section(line, 강남역, 선릉역, 5);

        // when
        line.getSections().addSection(newSection);

        // then
        final Section existedSection = line.getSections().getSections().get(0);
        final Section addedSection = line.getSections().getSections().get(1);
        assertThat(existedSection.getUpStation()).isEqualTo(강남역);
        assertThat(existedSection.getDownStation()).isEqualTo(선릉역);
        assertThat(addedSection.getUpStation()).isEqualTo(선릉역);
        assertThat(addedSection.getDownStation()).isEqualTo(역삼역);
    }

    /**
     * A-B (distance 10)
     * A-C (distance 15)
     * -> A-B-C
     */
    @DisplayName("상행역 기준 다음 역을 추가한다.")
    @Test
    void 상행역_기준_다음_역_추가() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Section newSection = new Section(line, 강남역, 선릉역, 15);

        // when
        line.getSections().addSection(newSection);

        // then
        final Section existedSection = line.getSections().getSections().get(0);
        final Section addedSection = line.getSections().getSections().get(1);
        assertThat(existedSection.getUpStation()).isEqualTo(강남역);
        assertThat(existedSection.getDownStation()).isEqualTo(역삼역);
        assertThat(addedSection.getUpStation()).isEqualTo(역삼역);
        assertThat(addedSection.getDownStation()).isEqualTo(선릉역);
    }

    @DisplayName("연관 없는 역을 추가한다면 오류가 발생한다.")
    @Test
    void 연관_없는_역_추가_불가() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Station 신림역 = new Station("신림역");
        final Station 영동포구청역 = new Station("영동포구청역");
        final Section newSection = new Section(line, 신림역, 영동포구청역, 15);

        // when

        // then
        assertThatThrownBy(() -> line.getSections().addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Section addition failed. The new section cannot be added at any existing station. Given newSection: ");
    }

    @DisplayName("상행선에서 하행선을 추가할 때 기존과 동일한 구간 길이면 오류가 발생한다.")
    @Test
    void 상행선에서_하행선_추가_시_동일한_구간이면_실패() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        final Section newSection = new Section(line, 강남역, 선릉역, 10);

        // when

        // then
        assertThatThrownBy(() -> line.getSections().addSection(newSection))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid new section. Section's distance and new section's distance is same.");
    }

    @DisplayName("구간 검증 시 마지막 구간인지 확인할 수 있다.")
    @Test
    void 구간_검증_시_마지막_구간인지_확인_가능() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        when(역삼역.getId()).thenReturn(2L);

        // when
        final boolean isLastStationTrue = line.getSections().isLastStation(2L);
        final boolean isLastStationFalse = line.getSections().isLastStation(3L);

        // then
        assertThat(isLastStationTrue).isTrue();
        assertThat(isLastStationFalse).isFalse();
    }

    @DisplayName("구간 검증 시 이미 등록된 구간인지 확인할 수 있다.")
    @Test
    void 구간_검증_시_이미_등록된_구간인지_확인_가능() {
        // given
        line = new Line("2호선", "bg-red-600", 강남역, 역삼역, 10);
        when(강남역.getId()).thenReturn(1L);
        when(역삼역.getId()).thenReturn(2L);

        // when
        final boolean isSectionRegisteredTrue = line.getSections().isSectionRegistered(1L, 2L);
        final boolean isSectionRegisteredFalse = line.getSections().isSectionRegistered(1L, 3L);

        // then
        assertThat(isSectionRegisteredTrue).isTrue();
        assertThat(isSectionRegisteredFalse).isFalse();
    }

}
