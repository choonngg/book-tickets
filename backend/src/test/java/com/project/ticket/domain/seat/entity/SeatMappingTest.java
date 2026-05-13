package com.project.ticket.domain.seat.entity;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class SeatMappingTest {

    @Test
    void usesMysqlSafeSeatLocationColumnNames() throws NoSuchFieldException {
        Table table = Seat.class.getAnnotation(Table.class);
        Column rowColumn = Seat.class.getDeclaredField("row").getAnnotation(Column.class);
        Column colColumn = Seat.class.getDeclaredField("col").getAnnotation(Column.class);

        assertThat(rowColumn.name()).isEqualTo("seat_row");
        assertThat(colColumn.name()).isEqualTo("seat_col");
        assertThat(Arrays.stream(table.uniqueConstraints())
                .flatMap(uniqueConstraint -> Arrays.stream(uniqueConstraint.columnNames())))
                .contains("seat_row", "seat_col")
                .doesNotContain("row_number", "col_number");
    }
}
