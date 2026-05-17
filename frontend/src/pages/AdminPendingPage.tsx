import { ShieldAlert } from 'lucide-react';

export function AdminPendingPage() {
  return (
    <section className="page-panel pending-panel">
      <ShieldAlert size={38} aria-hidden="true" />
      <p className="eyebrow">ADMIN</p>
      <h1>관리자 화면 준비 중</h1>
      <p>관리자 전용 페이지는 이번 배포 전 프론트 개선 범위에서 제외했습니다.</p>
    </section>
  );
}
