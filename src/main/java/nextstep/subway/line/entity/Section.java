package nextstep.subway.line.entity;

import nextstep.subway.station.entity.Station;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Section {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "line_id")
    private Line line;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "up_station_id")
    private Station upStation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "down_station_id")
    private Station downStation;

    @Column(nullable = false)
    private int distance;

    protected Section() {
    }

    public Section(final Line line, final Station upStation, final Station downStation, final int distance) {
        this.line = line;
        this.upStation = upStation;
        this.downStation = downStation;
        this.distance = distance;
    }

    public boolean isEqualsDistance(final Section newSection) {
        return this.getDistance() == newSection.getDistance();
    }

    public boolean isNewSectionLongerThanExisting(final Section newSection) {
        return this.getDistance() < newSection.getDistance();
    }

    public boolean canAddSectionBeforeExistingDownStation(final Section newSection) {
        return !this.getUpStation().equals(newSection.getUpStation()) &&
                this.getDownStation().equals(newSection.getDownStation());
    }

    public boolean canAddSectionAfterExistingDownStation(final Section newSection) {
        return !this.getUpStation().equals(newSection.getDownStation()) &&
                this.getDownStation().equals(newSection.getUpStation());
    }

    public boolean canAddSectionAfterExistingUpStation(final Section newSection) {
        return this.getUpStation().equals(newSection.getUpStation()) &&
                !this.getDownStation().equals(newSection.getDownStation());
    }

    public boolean canAddSectionBeforeExistingUpStation(final Section newSection) {
        return this.getUpStation().equals(newSection.getDownStation()) &&
                !this.getDownStation().equals(newSection.getUpStation());
    }

    public Long getId() {
        return id;
    }

    public Line getLine() {
        return line;
    }

    public Station getUpStation() {
        return upStation;
    }

    public Station getDownStation() {
        return downStation;
    }

    public int getDistance() {
        return distance;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Section section = (Section) o;

        return Objects.equals(id, section.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Section{" +
                "id=" + id +
                ", line=" + line +
                ", upStation=" + upStation +
                ", downStation=" + downStation +
                ", distance=" + distance +
                '}';
    }
}
