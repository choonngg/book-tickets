import { ArrowLeft, CheckCircle2 } from 'lucide-react';
import { useState } from 'react';

import { SeatMap } from '../components/SeatMap';
import { SeatSectionPicker } from '../components/SeatSectionPicker';
import type {
  ConcertResponse,
  SeatSectionSeatResponse,
  SeatSectionSummaryResponse,
} from '../types';

interface PurchasePageProps {
  concert: ConcertResponse;
  isBusy: boolean;
  seats: SeatSectionSeatResponse[];
  seatSections: SeatSectionSummaryResponse[];
  selectedSeat: SeatSectionSeatResponse | null;
  selectedSeatId: number | null;
  selectedSection: string | null;
  totalAvailableSeats: number;
  onBack: () => void;
  onPurchase: () => Promise<boolean>;
  onSelectSeat: (seatId: number) => void;
  onSelectSection: (section: string) => void;
}

export function PurchasePage({
  concert,
  isBusy,
  seats,
  seatSections,
  selectedSeat,
  selectedSeatId,
  selectedSection,
  totalAvailableSeats,
  onBack,
  onPurchase,
  onSelectSeat,
  onSelectSection,
}: PurchasePageProps) {
  const [isSeatModalOpen, setIsSeatModalOpen] = useState(false);

  async function handlePurchaseClick() {
    const completed = await onPurchase();

    if (completed) {
      window.alert('구매가 완료되었습니다.');
      setIsSeatModalOpen(false);
    }
  }

  return (
    <>
      <section className="page-hero purchase-hero">
        <div>
          <p className="eyebrow">PURCHASE</p>
          <h1>공연 구매 페이지</h1>
          <p>{concert.title}의 구역을 선택하고 좌석을 예매하세요.</p>
        </div>
        <button className="secondary-button" type="button" onClick={onBack}>
          <ArrowLeft size={16} aria-hidden="true" />
          팬 메인으로
        </button>
      </section>

      <section className="purchase-layout">
        <section className="page-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">CONCERT</p>
              <h2>{concert.title}</h2>
            </div>
            <span>{concert.status}</span>
          </div>
          <div className="selected-concert">
            <strong>{concert.venue}</strong>
            <span>{formatDate(concert.concertDate)}</span>
          </div>
          <div className="purchase-summary">
            <div>
              <span>예매 오픈</span>
              <strong>{formatDate(concert.ticketOpenDate)}</strong>
            </div>
            <div>
              <span>예매 마감</span>
              <strong>{formatDate(concert.ticketCloseDate)}</strong>
            </div>
          </div>
          <button className="wide-button" type="button" onClick={() => setIsSeatModalOpen(true)}>
            구역 및 좌석 선택
          </button>
        </section>

        <aside className="page-panel purchase-preview">
          <div className="section-heading">
            <div>
              <p className="eyebrow">SELECTED</p>
              <h2>선택 정보</h2>
            </div>
          </div>
          <div className="purchase-summary">
            <div>
              <span>선택 좌석</span>
              <strong>
                {selectedSeat && selectedSection ? `${selectedSection}구역 ${selectedSeat.row}행 ${selectedSeat.col}열` : '미선택'}
              </strong>
            </div>
            <div>
              <span>가격</span>
              <strong>{selectedSeat ? `${selectedSeat.price.toLocaleString()}원` : '-'}</strong>
            </div>
          </div>
          <p className="empty-copy">좌석을 더 넓게 보려면 좌석 선택 모달을 여세요.</p>
        </aside>
      </section>

      {isSeatModalOpen && (
        <div className="modal-backdrop seat-modal-backdrop">
          <section aria-modal="true" className="seat-selection-modal" role="dialog">
            <div className="section-heading">
              <div>
                <p className="eyebrow">SEAT MAP</p>
                <h2>구역 및 좌석 선택</h2>
              </div>
              <span>{totalAvailableSeats}석</span>
            </div>
            <div className="seat-modal-layout">
              <aside className="seat-modal-sidebar">
                <strong>{concert.title}</strong>
                <span>{concert.venue}</span>
                <SeatSectionPicker
                  sections={seatSections}
                  selectedSection={selectedSection}
                  onSelectSection={onSelectSection}
                />
              </aside>
              <div className="seat-modal-map">
                <SeatMap
                  seats={seats}
                  selectedSeatId={selectedSeatId}
                  selectedSection={selectedSection}
                  onSelectSeat={onSelectSeat}
                />
              </div>
            </div>
            <div className="seat-modal-footer">
              <div className="purchase-summary">
                <div>
                  <span>선택 좌석</span>
                  <strong>
                    {selectedSeat && selectedSection
                      ? `${selectedSection}구역 ${selectedSeat.row}행 ${selectedSeat.col}열`
                      : '미선택'}
                  </strong>
                </div>
                <div>
                  <span>가격</span>
                  <strong>{selectedSeat ? `${selectedSeat.price.toLocaleString()}원` : '-'}</strong>
                </div>
              </div>
              <div className="modal-actions">
                <button className="secondary-button" type="button" onClick={() => setIsSeatModalOpen(false)}>
                  닫기
                </button>
                <button type="button" onClick={handlePurchaseClick} disabled={isBusy}>
                  <CheckCircle2 size={17} aria-hidden="true" />
                  선택 좌석 예매하기
                </button>
              </div>
            </div>
          </section>
        </div>
      )}
    </>
  );
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('ko-KR', {
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value));
}
