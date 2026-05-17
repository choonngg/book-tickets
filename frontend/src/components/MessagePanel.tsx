interface MessagePanelProps {
  isBusy: boolean;
  message: string;
}

export function MessagePanel({ isBusy, message }: MessagePanelProps) {
  return (
    <aside className="message-card" aria-live="polite">
      <span>{isBusy ? '처리 중' : '알림'}</span>
      <p>{message}</p>
    </aside>
  );
}
