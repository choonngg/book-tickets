import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';

import App from './App';
import type { UserRole } from './types';

describe('Ticket frontend', () => {
  afterEach(() => {
    localStorage.clear();
    vi.unstubAllGlobals();
  });

  it('starts on the login page when there is no saved session', async () => {
    mockFetch();

    render(<App />);

    expect(await screen.findByRole('heading', { name: '로그인' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '로그인' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '회원가입' })).toBeInTheDocument();
    expect(screen.queryByLabelText('이름')).not.toBeInTheDocument();
    expect(screen.queryByLabelText('역할')).not.toBeInTheDocument();

    fireEvent.click(screen.getByRole('button', { name: '회원가입' }));

    expect(await screen.findByRole('dialog')).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '회원가입' })).toBeInTheDocument();
    expect(screen.getByLabelText('이름')).toBeInTheDocument();
    expect(screen.getByLabelText('역할')).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'FAN' })).toBeInTheDocument();
    expect(screen.getByRole('option', { name: 'ARTIST' })).toBeInTheDocument();
    expect(screen.queryByRole('option', { name: 'ADMIN' })).not.toBeInTheDocument();
  });

  it('routes FAN users to the fan home after login', async () => {
    mockLoginFlow('FAN');

    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: '로그인' }));

    expect(await screen.findByRole('heading', { name: '팬 메인' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '공연 새로고침' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '구매한 공연' })).toBeInTheDocument();
  });

  it('routes ARTIST users to the artist home after login', async () => {
    mockLoginFlow('ARTIST');

    render(<App />);
    fireEvent.change(screen.getByLabelText('이메일'), { target: { value: 'artist@example.com' } });
    fireEvent.click(screen.getByRole('button', { name: '로그인' }));

    expect(await screen.findByRole('heading', { name: '아티스트 메인' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '전체 공연' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '내 공연' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '공연 등록' })).toBeInTheDocument();
  });

  it('routes ADMIN users to the pending page after login', async () => {
    mockLoginFlow('ADMIN');

    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: '로그인' }));

    expect(await screen.findByRole('heading', { name: '관리자 화면 준비 중' })).toBeInTheDocument();
  });

  it('loads section summary before loading seats for a selected section', async () => {
    const fetchMock = mockLoginFlow('FAN');

    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: '로그인' }));
    await screen.findByRole('heading', { name: '팬 메인' });

    fireEvent.click(screen.getByRole('button', { name: '공연 새로고침' }));

    fireEvent.click(await screen.findByRole('button', { name: '구매하기' }));

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith('/api/concerts/1/seats/availability', expect.any(Object));
    });

    fireEvent.click(screen.getByRole('button', { name: '구역 및 좌석 선택' }));

    expect(screen.getByText(/14951/)).toBeInTheDocument();

    fireEvent.click(await screen.findByRole('button', { name: /A/ }));

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith('/api/concerts/1/seats/availability/sections/A', expect.any(Object));
    });
    expect(await screen.findByText('1-1')).toBeInTheDocument();
    expect(screen.getByText('1-2').closest('button')).toBeDisabled();
  });

  it('shows purchased concerts with concert and seat labels', async () => {
    mockLoginFlow('FAN');

    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: '로그인' }));
    await screen.findByRole('heading', { name: '팬 메인' });

    fireEvent.click(screen.getByRole('button', { name: '구매한 공연' }));

    expect(await screen.findByText('Spring Concert')).toBeInTheDocument();
    expect(screen.getByText('A구역 1행 1번')).toBeInTheDocument();
    expect(screen.queryByText(/좌석 #/)).not.toBeInTheDocument();
  });

  it('alerts and closes the seat modal after purchase succeeds', async () => {
    const fetchMock = mockLoginFlow('FAN');
    const alertMock = vi.fn();
    vi.stubGlobal('alert', alertMock);

    render(<App />);
    fireEvent.click(screen.getByRole('button', { name: '로그인' }));
    await screen.findByRole('heading', { name: '팬 메인' });
    fireEvent.click(screen.getByRole('button', { name: '공연 새로고침' }));
    fireEvent.click(await screen.findByRole('button', { name: '구매하기' }));
    fireEvent.click(screen.getByRole('button', { name: '구역 및 좌석 선택' }));
    fireEvent.click(await screen.findByRole('button', { name: /A/ }));
    fireEvent.click((await screen.findByText('1-1')).closest('button')!);
    fireEvent.click(screen.getByRole('button', { name: /선택 좌석 예매하기/ }));

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith('/api/tickets', expect.any(Object));
      expect(alertMock).toHaveBeenCalledWith('구매가 완료되었습니다.');
      expect(screen.queryByRole('dialog')).not.toBeInTheDocument();
    });
  });
});

function mockLoginFlow(role: UserRole) {
  const fetchMock = vi.fn(async (input: RequestInfo | URL) => {
    const url = String(input);

    if (url.endsWith('/api/auth/login')) {
      return jsonResponse({
        accessToken: 'access-token',
        refreshToken: 'refresh-token',
        tokenType: 'Bearer',
      });
    }

    if (url.endsWith('/api/users/me')) {
      return jsonResponse({
        userId: role === 'ARTIST' ? 7 : 1,
        name: role === 'ARTIST' ? '아티스트 사용자' : role === 'ADMIN' ? '관리자' : '팬 사용자',
        role,
      });
    }

    if (url.endsWith('/api/concerts')) {
      return jsonResponse([
        {
          concertId: 1,
          artistId: 7,
          title: 'Spring Concert',
          venue: 'Olympic Park',
          concertDate: '2026-06-01T19:00:00',
          ticketOpenDate: '2026-05-20T12:00:00',
          ticketCloseDate: '2026-05-31T23:59:00',
          status: 'ON_SALE',
        },
      ]);
    }

    if (url.endsWith('/api/concerts/1/seats/availability')) {
      return jsonResponse({
        totalAvailable: 14951,
        sections: [
          { section: 'A', availableCount: 1500 },
          { section: 'B', availableCount: 1498 },
        ],
      });
    }

    if (url.endsWith('/api/concerts/1/seats/availability/sections/A')) {
      return jsonResponse({
        section: 'A',
        availableCount: 1,
        seats: [
          { seatId: 1, row: 1, col: 1, price: 120000, status: 'AVAILABLE' },
          { seatId: 2, row: 1, col: 2, price: 120000, status: 'SOLD' },
        ],
      });
    }

    if (url.endsWith('/api/tickets/me')) {
      return jsonResponse([
        {
          ticketId: 10,
          concertTitle: 'Spring Concert',
          section: 'A',
          row: 1,
          col: 1,
          status: 'COMPLETED',
        },
      ]);
    }

    if (url.endsWith('/api/tickets')) {
      return jsonResponse({
        ticketId: 11,
        userId: 1,
        seatId: 1,
        paymentId: 11,
        status: 'COMPLETED',
      });
    }

    return jsonResponse([]);
  });

  vi.stubGlobal('fetch', fetchMock);
  return fetchMock;
}

function mockFetch() {
  const fetchMock = vi.fn(async () => jsonResponse([]));
  vi.stubGlobal('fetch', fetchMock);
  return fetchMock;
}

function jsonResponse(body: unknown) {
  return {
    ok: true,
    status: 200,
    json: async () => body,
  };
}
