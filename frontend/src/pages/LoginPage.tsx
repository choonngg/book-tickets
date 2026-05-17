import { LogIn, UserPlus } from 'lucide-react';
import type { FormEvent } from 'react';
import { useState } from 'react';

import type { SignupRequest } from '../types';

interface LoginPageProps {
  authForm: SignupRequest;
  isBusy: boolean;
  onChange: (form: SignupRequest) => void;
  onLogin: (event: FormEvent) => void;
  onSignup: (event: FormEvent) => void;
}

export function LoginPage({ authForm, isBusy, onChange, onLogin, onSignup }: LoginPageProps) {
  const [isSignupOpen, setIsSignupOpen] = useState(false);

  function handleSignup(event: FormEvent) {
    void onSignup(event);
    setIsSignupOpen(false);
  }

  return (
    <section className="login-page">
      <div className="login-hero">
        <p className="eyebrow">LIVE TICKET BOOKING</p>
        <h1>역할에 맞게 시작하는 티켓 예매</h1>
        <p>팬은 공연을 고르고 좌석을 구매하고, 아티스트는 공연을 등록하고 관리합니다.</p>
      </div>

      <form className="auth-card" onSubmit={onLogin}>
        <div className="section-heading form-heading">
          <div>
            <p className="eyebrow">ACCOUNT</p>
            <h2>로그인</h2>
          </div>
        </div>
        <label>
          이메일
          <input
            type="email"
            value={authForm.email}
            onChange={(event) => onChange({ ...authForm, email: event.target.value })}
          />
        </label>
        <label>
          비밀번호
          <input
            type="password"
            value={authForm.password}
            onChange={(event) => onChange({ ...authForm, password: event.target.value })}
          />
        </label>
        <div className="auth-actions">
          <button type="submit" disabled={isBusy}>
            <LogIn size={16} aria-hidden="true" />
            로그인
          </button>
          <button className="secondary-button" type="button" onClick={() => setIsSignupOpen(true)} disabled={isBusy}>
            <UserPlus size={16} aria-hidden="true" />
            회원가입
          </button>
        </div>
      </form>

      {isSignupOpen && (
        <div className="modal-backdrop">
          <form aria-modal="true" className="signup-modal" onSubmit={handleSignup} role="dialog">
            <div className="section-heading form-heading">
              <div>
                <p className="eyebrow">SIGN UP</p>
                <h2>회원가입</h2>
              </div>
            </div>
            <label>
              이메일
              <input
                type="email"
                value={authForm.email}
                onChange={(event) => onChange({ ...authForm, email: event.target.value })}
              />
            </label>
            <label>
              비밀번호
              <input
                type="password"
                value={authForm.password}
                onChange={(event) => onChange({ ...authForm, password: event.target.value })}
              />
            </label>
            <label>
              이름
              <input value={authForm.name} onChange={(event) => onChange({ ...authForm, name: event.target.value })} />
            </label>
            <label>
              역할
              <select
                value={authForm.role === 'ADMIN' ? 'FAN' : authForm.role}
                onChange={(event) => onChange({ ...authForm, role: event.target.value as SignupRequest['role'] })}
              >
                <option value="FAN">FAN</option>
                <option value="ARTIST">ARTIST</option>
              </select>
            </label>
            <div className="auth-actions">
              <button type="submit" disabled={isBusy}>
                <UserPlus size={16} aria-hidden="true" />
                가입하기
              </button>
              <button className="secondary-button" type="button" onClick={() => setIsSignupOpen(false)}>
                닫기
              </button>
            </div>
          </form>
        </div>
      )}
    </section>
  );
}
