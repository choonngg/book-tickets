import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';

import App from './App';

describe('Ticket frontend', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('shows a customer-facing ticket booking experience', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue({
        ok: true,
        status: 200,
        json: async () => [],
      }),
    );

    render(<App />);

    expect(await screen.findByRole('heading', { name: '지금 가장 빠른 예매' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '로그인' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '콘서트 새로고침' })).toBeInTheDocument();
    expect(screen.getByRole('button', { name: '내 티켓' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '공연 둘러보기' })).toBeInTheDocument();
    expect(screen.getByRole('heading', { name: '좌석 선택' })).toBeInTheDocument();
  });

  it('loads section summary before loading seats for a selected section', async () => {
    const jsonResponse = (body: unknown) => ({
      ok: true,
      status: 200,
      json: async () => body,
    });
    const fetchMock = vi.fn(async (input: RequestInfo | URL) => {
      const url = String(input);

      if (url.endsWith('/api/concerts')) {
        return jsonResponse([
          {
            concertId: 1,
            artistId: 1,
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
          availableCount: 1500,
          seats: [{ seatId: 1, row: 1, col: 1, price: 120000 }],
        });
      }

      return jsonResponse([]);
    });
    vi.stubGlobal('fetch', fetchMock);

    render(<App />);

    fireEvent.click(screen.getByRole('button', { name: '콘서트 새로고침' }));

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith('/api/concerts/1/seats/availability', expect.any(Object));
    });
    expect(screen.getByText(/14951/)).toBeInTheDocument();

    fireEvent.click(await screen.findByRole('button', { name: /A/ }));

    await waitFor(() => {
      expect(fetchMock).toHaveBeenCalledWith('/api/concerts/1/seats/availability/sections/A', expect.any(Object));
    });
    expect(await screen.findByText('1-1')).toBeInTheDocument();
  });
});
