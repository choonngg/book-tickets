import { render, screen } from '@testing-library/react';
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
});
