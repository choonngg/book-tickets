import { CalendarDays, MapPin, Music2 } from 'lucide-react';

import type { ConcertResponse } from '../types';

interface ConcertListProps {
  actionLabel: string;
  concerts: ConcertResponse[];
  emptyDescription: string;
  emptyTitle: string;
  selectedConcertId?: number | null;
  onSelect: (concert: ConcertResponse) => void;
}

export function ConcertList({
  actionLabel,
  concerts,
  emptyDescription,
  emptyTitle,
  selectedConcertId,
  onSelect,
}: ConcertListProps) {
  if (concerts.length === 0) {
    return (
      <div className="empty-state">
        <Music2 size={32} aria-hidden="true" />
        <strong>{emptyTitle}</strong>
        <p>{emptyDescription}</p>
      </div>
    );
  }

  return (
    <div className="concert-grid">
      {concerts.map((concert) => (
        <article className={concert.concertId === selectedConcertId ? 'concert-card selected' : 'concert-card'} key={concert.concertId}>
          <div className="poster-block">
            <Music2 size={30} aria-hidden="true" />
          </div>
          <div className="concert-info">
            <span className="status-pill">{concert.status}</span>
            <h3>{concert.title}</h3>
            <p>
              <MapPin size={14} aria-hidden="true" />
              {concert.venue}
            </p>
            <p>
              <CalendarDays size={14} aria-hidden="true" />
              {formatDate(concert.concertDate)}
            </p>
            <button type="button" onClick={() => onSelect(concert)}>
              {actionLabel}
            </button>
          </div>
        </article>
      ))}
    </div>
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
