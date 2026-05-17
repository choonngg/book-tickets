export type UserRole = 'FAN' | 'ARTIST' | 'ADMIN';

export interface SignupRequest {
  email: string;
  password: string;
  name: string;
  role: UserRole;
}

export interface SignupResponse {
  userId: number;
  email: string;
  name: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
}

export interface UserResponse {
  userId: number;
  name: string;
  role: UserRole;
}

export interface ConcertCreateRequest {
  title: string;
  venue: string;
  concertDate: string;
  ticketOpenDate: string;
  ticketCloseDate: string;
  price: number;
}

export interface ConcertResponse {
  concertId: number;
  artistId: number;
  title: string;
  venue: string;
  concertDate: string;
  ticketOpenDate: string;
  ticketCloseDate: string;
  status: string;
}

export interface SeatResponse {
  seatId: number;
  concertId: number;
  section: string;
  row: number;
  col: number;
  price: number;
  status: string;
}

export interface SeatSectionSummaryResponse {
  section: string;
  availableCount: number;
}

export interface SeatAvailabilitySummaryResponse {
  totalAvailable: number;
  sections: SeatSectionSummaryResponse[];
}

export interface SeatSectionSeatResponse {
  seatId: number;
  row: number;
  col: number;
  price: number;
  status: string;
}

export interface SeatSectionAvailabilityResponse {
  section: string;
  availableCount: number;
  seats: SeatSectionSeatResponse[];
}

export interface TicketPurchaseResponse {
  ticketId: number;
  userId: number;
  seatId: number;
  paymentId: number;
  status: string;
}

export interface TicketViewResponse {
  ticketId: number;
  concertTitle: string;
  section: string;
  row: number;
  col: number;
  status: string;
}
