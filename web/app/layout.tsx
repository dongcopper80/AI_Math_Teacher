import type { Metadata } from 'next';
import './globals.css';
export const metadata: Metadata = { title: 'Nobita — Học Toán thật vui', description: 'Nobita đồng hành cùng em học Toán từ tiểu học đến THPT.' };
export default function RootLayout({ children }: Readonly<{children: React.ReactNode}>) { return <html lang="vi"><body>{children}</body></html>; }