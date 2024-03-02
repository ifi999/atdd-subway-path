package nextstep.subway.acceptance;

import io.restassured.path.json.JsonPath;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.SectionRequest;
import nextstep.subway.station.dto.StationRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("지하철 구간 관련 기능")
public class SectionAcceptanceTest extends BaseTest{

    private static final String LINE_API_PATH = "/lines";
    private static final String SECTION_API_PATH = "/lines/{id}/sections";
    private static final String STATION_API_PATH = "/stations";

    private Long 강남역_ID;
    private Long 역삼역_ID;
    private Long 지하철역_ID;
    private Long 신분당선_ID;

    @Override
    @BeforeEach
    void setUp() {
        super.setUp();

        final StationRequest 강남역_생성_요청 = new StationRequest("강남역");
        final ExtractableResponse<Response> 강남역_생성_응답 = callPostApi(강남역_생성_요청, STATION_API_PATH);
        강남역_ID = getIdFromApiResponse(강남역_생성_응답);

        final StationRequest 역삼역_생성_요청 = new StationRequest("역삼역");
        final ExtractableResponse<Response> 역삼역_생성_응답 = callPostApi(역삼역_생성_요청, STATION_API_PATH);
        역삼역_ID = getIdFromApiResponse(역삼역_생성_응답);

        final StationRequest 지하역_생성_요청 = new StationRequest("지하철역");
        final ExtractableResponse<Response> 지하철역_생성_응답 = callPostApi(지하역_생성_요청, STATION_API_PATH);
        지하철역_ID = getIdFromApiResponse(지하철역_생성_응답);
    }

    /**
     * Given 1개의 지하철 노선을 등록하고 (A-B)
     * When 지하철 노선에 구간을 등록하면 (B-C)
     * Then 지하철 노선 조회 시 새로운 구간의 하행역을 찾을 수 있다 (A-B-C)
     */
    @DisplayName("지하철 구간을 가장 뒤에 추가한다.")
    @Test
    void 지하철_구간_등록() {
        // given
        this.신분당선_생성();

        // when
        final JsonPath createSectionResponse = this.신분당선_구간_연장_역삼역_지하철역();

        // then
        final String lineName = createSectionResponse.get("name");
        assertThat(lineName).isEqualTo("신분당선");

        final String newDownStationName = createSectionResponse.get("stations[2].name");
        assertThat(newDownStationName).isEqualTo("지하철역");

        final List<String> names = createSectionResponse.get("stations.name");
        assertThat(names).containsExactly("강남역", "역삼역", "지하철역");
    }

    @DisplayName("지하철 구간 생성 시 이미 등록된 구간이라면 오류가 발생한다.")
    @Test
    void 지하철_구간_생성_시_이미_등록된_구간이라면_등록_불가() {
        // given
        this.신분당선_생성();

        // when
        final SectionRequest sameRequest = new SectionRequest(강남역_ID, 역삼역_ID, 5);

        // then
        callPostApiWithServerError(sameRequest, SECTION_API_PATH, 신분당선_ID);
    }

//    @DisplayName("지하철 구간 생성 시 이미 등록되어있는 역이 새로운 구간의 하행역이 된다면 오류가 발생한다.")
//    @Test
//    void 지하철_구간_생성_시_이미_등록되어있는_역이라면_새로운_구간의_하행역으로_등록_불가() {
//        // given
//        this.신분당선_생성();
//
//        // when
//        final SectionRequest sameRequest = new SectionRequest(지하철역_ID, 강남역_ID, 5);
//
//        // then
//        callPostApiWithServerError(sameRequest, SECTION_API_PATH, 신분당선_ID);
//    }

    /**
     * Given 지하철 구간을 생성하고
     * When 생성한 지하철 구간을 삭제하면
     * Then 해당 지하철 구간 정보는 삭제된다
     */
    @DisplayName("지하철 구간을 삭제한다.")
    @Test
    void 지하철_구간_삭제() {
        // given
        this.신분당선_생성();
        this.신분당선_구간_연장_역삼역_지하철역();

        // when
        callDeleteApi("stationId", 지하철역_ID, SECTION_API_PATH, 신분당선_ID);

        // then
        final ExtractableResponse<Response> 신분당선_조회_요청 = callGetApi(LINE_API_PATH + "/{id}", 신분당선_ID);
        final JsonPath getLineResponse = 신분당선_조회_요청.jsonPath();

        final List<String> names = getLineResponse.get("stations.name");
        assertThat(names).containsOnly("강남역", "역삼역");
    }

    @DisplayName("지하철 구간 삭제 시 마지막 구간이 아니라면 오류가 발생한다.")
    @Test
    void 지하철_구간_삭제_시_마지막_구간이_아니라면_삭제_불가() {
        // given
        this.신분당선_생성();
        this.신분당선_구간_연장_역삼역_지하철역();

        // then
        callDeleteApiWithServerError("stationId", 역삼역_ID, SECTION_API_PATH, 신분당선_ID);
    }

    @DisplayName("지하철 구간 삭제 시 구간이 하나 밖에 없다면 오류가 발생한다.")
    @Test
    void 지하철_구간_삭제_시_구간이_하나라면_삭제_불가() {
        // given
        this.신분당선_생성();

        // then
        callDeleteApiWithServerError("stationId", 역삼역_ID, SECTION_API_PATH, 신분당선_ID);
    }

    /**
     * Given 지하철 노선을 생성하고 (A-B)
     * When 노선에 역을 노선 가운데 추가하면 (A-C)
     * Then 지하철 노선 조회 시 가운데 추가한 역을 찾을 수 있다 (A-C-B)
     */
    @DisplayName("지하철 노선에 역을 노선 가운데 추가한다.")
    @Test
    void 지하철_노선_가운데_역_추가() {
        // given
        this.신분당선_생성();

        // when
        final SectionRequest sectionRequest = new SectionRequest(강남역_ID, 지하철역_ID, 5);
        final ExtractableResponse<Response> createSectionResponse = callPostApi(sectionRequest, SECTION_API_PATH, 신분당선_ID);
        final JsonPath jsonPath = createSectionResponse.jsonPath();


        // then
        final List<String> names = jsonPath.get("stations.name");
        assertThat(names).containsExactly("강남역", "지하철역", "역삼역");
    }

    /**
     * Given 지하철 노선을 생성하고 (A-B)
     * When 노선에 역을 노선 가장 앞에 추가하면 (C-A)
     * Then 지하철 노선 조회 시 가장 앞에 추가한 역을 찾을 수 있다 (C-A-B)
     */
    @DisplayName("지하철 노선에 역을 노선 가장 앞에 추가한다.")
    @Test
    void 지하철_노선_가장_앞에_역_추가() {
        // given
        this.신분당선_생성();

        // when
        final SectionRequest sectionRequest = new SectionRequest(지하철역_ID, 강남역_ID, 5);
        final ExtractableResponse<Response> createSectionResponse = callPostApi(sectionRequest, SECTION_API_PATH, 신분당선_ID);
        final JsonPath jsonPath = createSectionResponse.jsonPath();


        // then
        final List<String> names = jsonPath.get("stations.name");
        assertThat(names).containsExactly("지하철역", "강남역", "역삼역");
    }

    /**
     * Given 지하철 노선을 생성하고 (A-B, distance 10)
     * When 노선에 기존 상행역을 가진 역이면서 거리가 더 먼 역을 추가하면 (A-C, distance 15)
     * Then 지하철 노선 조회 시 가장 뒤에 추가한 역을 찾을 수 있다 (A-B-C)
     */
    @DisplayName("지하철 노선에 기존 상행역을 가진 역이면서 거리가 더 먼 역을 노선 가장 뒤에 추가한다.")
    @Test
    void 지하철_노선_기존_상행역_가진_역이면서_거리가_더_먼_노선_가장_뒤에_추가() {
        // given
        this.신분당선_생성();

        // when
        final SectionRequest sectionRequest = new SectionRequest(강남역_ID, 지하철역_ID, 15);
        final ExtractableResponse<Response> createSectionResponse = callPostApi(sectionRequest, SECTION_API_PATH, 신분당선_ID);
        final JsonPath jsonPath = createSectionResponse.jsonPath();


        // then
        final List<String> names = jsonPath.get("stations.name");
        assertThat(names).containsExactly("강남역", "역삼역", "지하철역");
    }

    /**
     * Given 지하철 노선을 생성하고 (A-B, distance 10)
     * When 노선에 기존 하행역을 가진 역이면서 거리가 짧은 역을 추가하면 (C-B, distance 5)
     * Then 지하철 노선 조회 시 중간에 추가한 역을 찾을 수 있다 (A-C-B)
     */
    @DisplayName("지하철 노선에 기존 상행역을 가진 역이면서 거리가 더 먼 역을 노선 가장 뒤에 추가한다.")
    @Test
    void 지하철_노선_기존_하행역_가진_역이면서_거리가_짧은_노선_중간에_추가() {
        // given
        this.신분당선_생성();

        // when
        final SectionRequest sectionRequest = new SectionRequest(지하철역_ID, 역삼역_ID, 5);
        final ExtractableResponse<Response> createSectionResponse = callPostApi(sectionRequest, SECTION_API_PATH, 신분당선_ID);
        final JsonPath jsonPath = createSectionResponse.jsonPath();


        // then
        final List<String> names = jsonPath.get("stations.name");
        assertThat(names).containsExactly("강남역", "지하철역", "역삼역");
    }

    /**
     * Given 지하철 노선을 생성하고 (A-B, distance 10)
     * When 노선에 기존 하행역을 가진 역이면서 거리가 더 긴 역을 추가하면 (C-B, distance 15)
     * Then 지하철 노선 조회 시 가장 앞에 추가한 역을 찾을 수 있다 (C-A-B)
     */
    @DisplayName("지하철 노선에 기존 상행역을 가진 역이면서 거리가 더 먼 역을 노선 가장 뒤에 추가한다.")
    @Test
    void 지하철_노선_기존_하행역_가진_역이면서_거리가_더_긴_노선_앞에_추가() {
        // given
        this.신분당선_생성();

        // when
        final SectionRequest sectionRequest = new SectionRequest(지하철역_ID, 역삼역_ID, 15);
        final ExtractableResponse<Response> createSectionResponse = callPostApi(sectionRequest, SECTION_API_PATH, 신분당선_ID);
        final JsonPath jsonPath = createSectionResponse.jsonPath();


        // then
        final List<String> names = jsonPath.get("stations.name");
        assertThat(names).containsExactly("지하철역", "강남역", "역삼역");
    }

    private JsonPath 신분당선_구간_연장_역삼역_지하철역() {
        final SectionRequest sectionRequest = new SectionRequest(역삼역_ID, 지하철역_ID, 5);
        final ExtractableResponse<Response> createSectionResponse = callPostApi(sectionRequest, SECTION_API_PATH, 신분당선_ID);
        final JsonPath jsonPath = createSectionResponse.jsonPath();

        return jsonPath;
    }

    private void 신분당선_생성() {
        final LineRequest lineRequest = new LineRequest("신분당선", "bg-red-600", 강남역_ID, 역삼역_ID, 10);
        final ExtractableResponse<Response> createLineResponse = callPostApi(lineRequest, LINE_API_PATH);
        신분당선_ID = getIdFromApiResponse(createLineResponse);
    }

}
