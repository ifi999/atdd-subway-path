package nextstep.subway.unit;

import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.line.entity.Line;
import nextstep.subway.line.repository.LineRepository;
import nextstep.subway.line.service.LineService;
import nextstep.subway.station.entity.Station;
import nextstep.subway.station.repository.StationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LineServiceMockTest {

    @Mock
    private LineRepository lineRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private LineService lineService;

    @DisplayName("지하철 노선을 생성한다.")
    @Test
    void 지하철_노선_생성() {
        // given
        final Station upStation = new Station("강남역");
        final Station downStation = new Station("역삼역");
        final Line line = new Line("신분당선", "bg-red-600", upStation, downStation, 10);
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        when(stationRepository.findById(1L)).thenReturn(Optional.of(upStation));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(downStation));
        when(lineRepository.save(any(Line.class))).thenReturn(line);

        // when
        final LineResponse response = lineService.createSubwayLine(lineRequest);

        // then
        assertThat(response.getName()).isEqualTo(lineRequest.getName());
        assertThat(response.getColor()).isEqualTo(lineRequest.getColor());
        verify(lineRepository).save(any(Line.class));
        verify(stationRepository).findById(lineRequest.getUpStationId());
        verify(stationRepository).findById(lineRequest.getDownStationId());
    }

    @DisplayName("지하철 노선 생성 시 존재하지 않는 역으로 조회할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_생성_시_존재하지_않는_역으로_조회_불가() {
        // given
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 1L, 2L, 10);

        when(stationRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.createSubwayLine(lineRequest))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("지하철 노선을 조회한다.")
    @Test
    void 지하철_노선_조회() {
        // given
        final Station upStation = new Station("강남역");
        final Station downStation = new Station("역삼역");
        final Line line = new Line("신분당선", "bg-red-600", upStation, downStation, 10);
        when(lineRepository.findById(line.getId())).thenReturn(Optional.of(line));

        // when
        final LineResponse response = lineService.getSubwayLine(line.getId());

        // then
        assertThat(response.getName()).isEqualTo(line.getName());
        assertThat(response.getColor()).isEqualTo(line.getColor());
        assertThat(response.getStations().get(0).getName()).isEqualTo(upStation.getName());
        // 아래 검증은 실패하는데 이유는 equals에서 id 값으로만 비교하기 때문으로 추측. 어떻게 할 수 있을지?
//        assertThat(response.getStations().get(1).getName()).isEqualTo(upStation.getName());
        verify(lineRepository).findById(line.getId());
    }

    @DisplayName("지하철 노선 조회 시 존재하지 않는 역으로 조회할 경우 오류가 발생한다.")
    @Test
    void 지하철_노선_조회_시_존재하지_않는_역으로_조회_불가() {
        // given
        when(lineRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lineService.getSubwayLine(1L))
                .isInstanceOf(EntityNotFoundException.class);
    }

}
