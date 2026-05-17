import type { SeatSectionSeatResponse } from '../types';

interface SeatMapProps {
  seats: SeatSectionSeatResponse[];
  selectedSeatId: number | null;
  selectedSection: string | null;
  onSelectSeat: (seatId: number) => void;
}

export function SeatMap({ seats, selectedSeatId, selectedSection, onSelectSeat }: SeatMapProps) {
  if (!selectedSection) {
    return <p className="empty-copy">좌석을 보려면 구역을 선택하세요.</p>;
  }

  if (seats.length === 0) {
    return <p className="empty-copy">표시할 예매 가능 좌석이 없습니다.</p>;
  }

  return (
    <div className="seat-map" aria-label="예매 가능 좌석">
      {seats.map((seat) => {
        const isAvailable = seat.status === 'AVAILABLE';

        return (
          <button
            className={[
              'seat-button',
              seat.seatId === selectedSeatId ? 'selected' : '',
              isAvailable ? 'available' : 'unavailable',
            ]
              .filter(Boolean)
              .join(' ')}
            disabled={!isAvailable}
            key={seat.seatId}
            type="button"
            onClick={() => onSelectSeat(seat.seatId)}
          >
            {selectedSection}
            <small>
              {seat.row}-{seat.col}
            </small>
          </button>
        );
      })}
    </div>
  );
}
