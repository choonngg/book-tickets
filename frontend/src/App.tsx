import {
  CalendarDays,
  CalendarPlus,
  CheckCircle2,
  LogIn,
  LogOut,
  MapPin,
  Music2,
  ReceiptText,
  RefreshCw,
  Search,
  Ticket,
  UserPlus,
} from 'lucide-react';
import { FormEvent, useEffect, useMemo, useState } from 'react';

import { api } from './api';
import type {
  ConcertCreateRequest,
  ConcertResponse,
  LoginResponse,
  SeatSectionSeatResponse,
  SeatSectionSummaryResponse,
  SignupRequest,
  TicketPurchaseResponse,
  UserResponse,
  UserRole,
} from './types';

type View = 'home' | 'tickets' | 'manage';

const initialAuthForm: SignupRequest = {
  email: 'fan@example.com',
  password: 'password1234',
  name: '팬 사용자',
  role: 'FAN',
};

const initialConcertForm: ConcertCreateRequest = {
  title: '샘플 콘서트',
  venue: 'KSPO DOME',
  concertDate: '2026-06-01T19:00',
  ticketOpenDate: '2026-05-20T12:00',
  ticketCloseDate: '2026-05-31T23:59',
  price: 120000,
};

function App() {
  const [view, setView] = useState<View>('home');
  const [authForm, setAuthForm] = useState(initialAuthForm);
  const [concertForm, setConcertForm] = useState(initialConcertForm);
  const [session, setSession] = useState<LoginResponse | null>(() => readSession());
  const [me, setMe] = useState<UserResponse | null>(null);
  const [concerts, setConcerts] = useState<ConcertResponse[]>([]);
  const [selectedConcertId, setSelectedConcertId] = useState<number | null>(null);
  const [seatSections, setSeatSections] = useState<SeatSectionSummaryResponse[]>([]);
  const [selectedSection, setSelectedSection] = useState<string | null>(null);
  const [totalAvailableSeats, setTotalAvailableSeats] = useState(0);
  const [seats, setSeats] = useState<SeatSectionSeatResponse[]>([]);
  const [selectedSeatId, setSelectedSeatId] = useState<number | null>(null);
  const [tickets, setTickets] = useState<TicketPurchaseResponse[]>([]);
  const [message, setMessage] = useState('원하는 공연을 선택하고 좌석을 예매해보세요.');
  const [isBusy, setIsBusy] = useState(false);

  const accessToken = session?.accessToken ?? '';
  const refreshToken = session?.refreshToken ?? '';
  const selectedConcert = useMemo(
    () => concerts.find((concert) => concert.concertId === selectedConcertId) ?? null,
    [concerts, selectedConcertId],
  );
  const selectedSeat = useMemo(
    () => seats.find((seat) => seat.seatId === selectedSeatId) ?? null,
    [seats, selectedSeatId],
  );

  useEffect(() => {
    if (!session) {
      localStorage.removeItem('ticket-session');
      setMe(null);
      return;
    }

    localStorage.setItem('ticket-session', JSON.stringify(session));
  }, [session]);

  async function run<T>(successMessage: string, action: () => Promise<T>) {
    setIsBusy(true);

    try {
      const result = await action();
      setMessage(successMessage);
      return result;
    } catch (error) {
      setMessage(error instanceof Error ? error.message : String(error));
      return null;
    } finally {
      setIsBusy(false);
    }
  }

  async function loadConcerts(showMessage = true) {
    const result = await run(
      showMessage ? '콘서트 목록을 새로 불러왔습니다.' : '콘서트 목록을 불러왔습니다.',
      api.findConcerts,
    );

    if (result) {
      setConcerts(result);
      if (!selectedConcertId && result[0]) {
        void selectConcert(result[0]);
      }
    }
  }

  async function selectConcert(concert: ConcertResponse) {
    setSelectedConcertId(concert.concertId);
    setSelectedSeatId(null);
    setSelectedSection(null);
    setSeats([]);
    setSeatSections([]);
    setTotalAvailableSeats(0);

    const result = await run(`${concert.title}의 구역별 예매 가능 좌석을 불러왔습니다.`, () =>
      api.findSeatAvailabilitySummary(concert.concertId),
    );

    if (result) {
      setSeatSections(result.sections);
      setTotalAvailableSeats(result.totalAvailable);
    }
  }

  async function selectSection(section: string) {
    if (!selectedConcert) {
      return;
    }

    setSelectedSection(section);
    setSelectedSeatId(null);
    setSeats([]);

    const result = await run(`${section}구역 좌석을 불러왔습니다.`, () =>
      api.findSeatSectionAvailability(selectedConcert.concertId, section),
    );

    if (result) {
      setSelectedSection(result.section);
      setSeats(result.seats);
    }
  }

  async function handleSignup(event: FormEvent) {
    event.preventDefault();
    await run('회원가입이 완료되었습니다. 이제 로그인할 수 있습니다.', () => api.signup(authForm));
  }

  async function handleLogin(event: FormEvent) {
    event.preventDefault();

    const loginResult = await run('로그인되었습니다.', () =>
      api.login({ email: authForm.email, password: authForm.password }),
    );

    if (loginResult) {
      setSession(loginResult);
      const profile = await run('내 정보를 불러왔습니다.', () => api.me(loginResult.accessToken));
      setMe(profile);
    }
  }

  async function handleLogout() {
    if (refreshToken) {
      await run('로그아웃되었습니다.', () => api.logout(refreshToken));
    }

    setSession(null);
    setTickets([]);
    setMessage('로그아웃되었습니다.');
  }

  async function handlePurchase() {
    if (!accessToken) {
      setMessage('예매하려면 먼저 로그인하세요.');
      return;
    }

    if (!selectedSeat) {
      setMessage('예매할 좌석을 먼저 선택하세요.');
      return;
    }

    const result = await run('티켓 예매가 완료되었습니다.', () =>
      api.purchaseTicket(accessToken, selectedSeat.seatId, createIdempotencyKey()),
    );

    if (result) {
      setTickets((current) => [result, ...current]);
      setSelectedSeatId(null);
      if (selectedConcert) {
        void selectConcert(selectedConcert);
      }
    }
  }

  async function handleFindMyTickets() {
    if (!accessToken) {
      setMessage('내 티켓을 보려면 먼저 로그인하세요.');
      return;
    }

    const result = await run('내 티켓을 불러왔습니다.', () => api.findMyTickets(accessToken));

    if (result) {
      setTickets(result);
      setView('tickets');
    }
  }

  async function handleCreateConcert(event: FormEvent) {
    event.preventDefault();

    if (!accessToken) {
      setMessage('공연을 등록하려면 먼저 로그인하세요.');
      return;
    }

    const result = await run('공연이 등록되었습니다.', () =>
      api.createConcert(accessToken, {
        ...concertForm,
        price: Number(concertForm.price),
      }),
    );

    if (result) {
      setConcerts((current) => [result, ...current.filter((concert) => concert.concertId !== result.concertId)]);
      await selectConcert(result);
      setView('home');
    }
  }

  return (
    <main className="app-shell">
      <header className="site-header">
        <button className="brand" type="button" onClick={() => setView('home')}>
          <Ticket size={22} aria-hidden="true" />
          Book Tickets
        </button>
        <nav className="main-nav" aria-label="주요 메뉴">
          <button className={view === 'home' ? 'active' : ''} type="button" onClick={() => setView('home')}>
            공연
          </button>
          <button className={view === 'tickets' ? 'active' : ''} type="button" onClick={handleFindMyTickets}>
            내 티켓
          </button>
          <button className={view === 'manage' ? 'active' : ''} type="button" onClick={() => setView('manage')}>
            공연 등록
          </button>
        </nav>
        <div className="header-actions">
          <span className={accessToken ? 'login-state active' : 'login-state'}>{me ? me.name : '게스트'}</span>
          {accessToken ? (
            <button className="text-button" type="button" onClick={handleLogout}>
              <LogOut size={16} aria-hidden="true" />
              로그아웃
            </button>
          ) : (
            <a className="text-button" href="#login">
              <LogIn size={16} aria-hidden="true" />
              로그인
            </a>
          )}
        </div>
      </header>

      {view === 'home' && (
        <>
          <section className="hero-section">
            <div className="hero-copy">
              <p className="eyebrow">LIVE TICKET BOOKING</p>
              <h1>지금 가장 빠른 예매</h1>
              <p>공연을 고르고, 좌석을 선택하고, 티켓 구매까지 한 번에 진행하세요.</p>
              <div className="hero-actions">
                <button type="button" onClick={() => loadConcerts(true)} disabled={isBusy}>
                  <RefreshCw size={16} aria-hidden="true" />
                  콘서트 새로고침
                </button>
                <button className="secondary-button" type="button" onClick={handleFindMyTickets} disabled={isBusy}>
                  <ReceiptText size={16} aria-hidden="true" />
                  내 티켓 보기
                </button>
              </div>
            </div>
            <div className="hero-panel">
              <span>선택한 공연</span>
              <strong>{selectedConcert?.title ?? '공연을 선택하세요'}</strong>
              <p>{selectedConcert ? `${selectedConcert.venue} / ${formatDate(selectedConcert.concertDate)}` : message}</p>
            </div>
          </section>

          <section className="booking-layout">
            <section className="concert-section">
              <div className="section-heading">
                <div>
                  <p className="eyebrow">CONCERTS</p>
                  <h2>공연 둘러보기</h2>
                </div>
                <span>{concerts.length}개 공연</span>
              </div>
              <div className="concert-grid">
                {concerts.length === 0 ? (
                  <div className="empty-state">
                    <Music2 size={32} aria-hidden="true" />
                    <strong>아직 조회된 공연이 없습니다.</strong>
                    <p>백엔드 서버를 실행한 뒤 콘서트 새로고침을 눌러보세요.</p>
                  </div>
                ) : (
                  concerts.map((concert) => (
                    <article
                      className={concert.concertId === selectedConcertId ? 'concert-card selected' : 'concert-card'}
                      key={concert.concertId}
                    >
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
                        <button type="button" onClick={() => selectConcert(concert)} disabled={isBusy}>
                          상세 보기
                        </button>
                      </div>
                    </article>
                  ))
                )}
              </div>
            </section>

            <aside className="booking-panel">
              <div className="section-heading compact">
                <div>
                  <p className="eyebrow">SEATS</p>
                  <h2>좌석 선택</h2>
                </div>
                <span>{totalAvailableSeats}석</span>
              </div>
              {selectedConcert ? (
                <>
                  <div className="selected-concert">
                    <strong>{selectedConcert.title}</strong>
                    <span>{selectedConcert.venue}</span>
                  </div>
                  <div className="section-picker" aria-label="좌석 구역">
                    {seatSections.length === 0 ? (
                      <p className="empty-copy">표시할 예매 가능 구역이 없습니다.</p>
                    ) : (
                      seatSections.map((section) => (
                        <button
                          className={section.section === selectedSection ? 'section-button selected' : 'section-button'}
                          key={section.section}
                          type="button"
                          onClick={() => selectSection(section.section)}
                          disabled={isBusy}
                        >
                          {section.section}
                          <small>{section.availableCount}석</small>
                        </button>
                      ))
                    )}
                  </div>
                  <div className="seat-map" aria-label="예매 가능 좌석">
                    {!selectedSection ? (
                      <p className="empty-copy">좌석을 보려면 구역을 선택하세요.</p>
                    ) : seats.length === 0 ? (
                      <p className="empty-copy">표시할 예매 가능 좌석이 없습니다.</p>
                    ) : (
                      seats.map((seat) => (
                        <button
                          className={seat.seatId === selectedSeatId ? 'seat-button selected' : 'seat-button'}
                          key={seat.seatId}
                          type="button"
                          onClick={() => setSelectedSeatId(seat.seatId)}
                        >
                          {selectedSection}
                          <small>
                            {seat.row}-{seat.col}
                          </small>
                        </button>
                      ))
                    )}
                  </div>
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
                  <button className="wide-button" type="button" onClick={handlePurchase} disabled={isBusy}>
                    <CheckCircle2 size={17} aria-hidden="true" />
                    선택 좌석 예매하기
                  </button>
                </>
              ) : (
                <div className="empty-state small">
                  <Search size={26} aria-hidden="true" />
                  <strong>공연을 먼저 선택하세요.</strong>
                  <p>공연 카드를 누르면 좌석 선택이 열립니다.</p>
                </div>
              )}
            </aside>
          </section>
        </>
      )}

      {view === 'tickets' && (
        <section className="page-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">MY TICKETS</p>
              <h2>내 티켓</h2>
            </div>
            <button type="button" onClick={handleFindMyTickets} disabled={isBusy}>
              <RefreshCw size={16} aria-hidden="true" />
              다시 조회
            </button>
          </div>
          <div className="ticket-grid">
            {tickets.length === 0 ? (
              <div className="empty-state">
                <ReceiptText size={32} aria-hidden="true" />
                <strong>표시할 티켓이 없습니다.</strong>
                <p>예매를 완료하면 이곳에서 구매 내역을 확인할 수 있습니다.</p>
              </div>
            ) : (
              tickets.map((ticket) => (
                <article className="ticket-card" key={`${ticket.ticketId}-${ticket.seatId}`}>
                  <span>Ticket #{ticket.ticketId}</span>
                  <strong>{ticket.status}</strong>
                  <p>좌석 #{ticket.seatId}</p>
                  <p>결제 #{ticket.paymentId}</p>
                </article>
              ))
            )}
          </div>
        </section>
      )}

      {view === 'manage' && (
        <section className="page-panel">
          <div className="section-heading">
            <div>
              <p className="eyebrow">ARTIST / ADMIN</p>
              <h2>공연 등록</h2>
            </div>
            <span>로그인 필요</span>
          </div>
          <form className="manage-form" onSubmit={handleCreateConcert}>
            <label>
              공연명
              <input
                value={concertForm.title}
                onChange={(event) => setConcertForm({ ...concertForm, title: event.target.value })}
              />
            </label>
            <label>
              장소
              <input
                value={concertForm.venue}
                onChange={(event) => setConcertForm({ ...concertForm, venue: event.target.value })}
              />
            </label>
            <label>
              공연일
              <input
                type="datetime-local"
                value={concertForm.concertDate}
                onChange={(event) => setConcertForm({ ...concertForm, concertDate: event.target.value })}
              />
            </label>
            <label>
              예매 오픈
              <input
                type="datetime-local"
                value={concertForm.ticketOpenDate}
                onChange={(event) => setConcertForm({ ...concertForm, ticketOpenDate: event.target.value })}
              />
            </label>
            <label>
              예매 마감
              <input
                type="datetime-local"
                value={concertForm.ticketCloseDate}
                onChange={(event) => setConcertForm({ ...concertForm, ticketCloseDate: event.target.value })}
              />
            </label>
            <label>
              기본 가격
              <input
                type="number"
                min="0"
                value={concertForm.price}
                onChange={(event) => setConcertForm({ ...concertForm, price: Number(event.target.value) })}
              />
            </label>
            <button type="submit" disabled={isBusy}>
              <CalendarPlus size={16} aria-hidden="true" />
              공연 등록하기
            </button>
          </form>
        </section>
      )}

      <section className="auth-and-message">
        <form className="auth-card" id="login" onSubmit={handleLogin}>
          <div>
            <p className="eyebrow">ACCOUNT</p>
            <h2>로그인</h2>
          </div>
          <label>
            이메일
            <input
              type="email"
              value={authForm.email}
              onChange={(event) => setAuthForm({ ...authForm, email: event.target.value })}
            />
          </label>
          <label>
            비밀번호
            <input
              type="password"
              value={authForm.password}
              onChange={(event) => setAuthForm({ ...authForm, password: event.target.value })}
            />
          </label>
          <label>
            이름
            <input value={authForm.name} onChange={(event) => setAuthForm({ ...authForm, name: event.target.value })} />
          </label>
          <label>
            역할
            <select
              value={authForm.role}
              onChange={(event) => setAuthForm({ ...authForm, role: event.target.value as UserRole })}
            >
              <option value="FAN">FAN</option>
              <option value="ARTIST">ARTIST</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </label>
          <div className="auth-actions">
            <button type="submit" disabled={isBusy}>
              <LogIn size={16} aria-hidden="true" />
              로그인
            </button>
            <button className="secondary-button" type="button" onClick={handleSignup} disabled={isBusy}>
              <UserPlus size={16} aria-hidden="true" />
              회원가입
            </button>
          </div>
        </form>

        <aside className="message-card" aria-live="polite">
          <span>{isBusy ? '처리 중' : '알림'}</span>
          <p>{message}</p>
        </aside>
      </section>
    </main>
  );
}

function readSession(): LoginResponse | null {
  const raw = localStorage.getItem('ticket-session');

  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw) as LoginResponse;
  } catch {
    return null;
  }
}

function createIdempotencyKey() {
  if ('randomUUID' in crypto) {
    return crypto.randomUUID();
  }

  return `ticket-${Date.now()}`;
}

function formatDate(value: string) {
  return new Intl.DateTimeFormat('ko-KR', {
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  }).format(new Date(value));
}

export default App;
