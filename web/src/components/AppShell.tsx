"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const NAV = [
  { href: "/", label: "Home", en: "产品首页" },
  { href: "/explorer", label: "Opportunity Explorer", en: "机会" },
  { href: "/pipeline", label: "Pipeline", en: "求职管道" },
  { href: "/resume", label: "Resume Workspace", en: "简历" },
  { href: "/settings", label: "Settings", en: "设置" },
];

const PRODUCT_NAV = [
  { href: "/explorer", label: "机会" },
  { href: "/pipeline", label: "管道" },
  { href: "/resume", label: "简历" },
  { href: "/settings", label: "设置" },
];

export function AppShell({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  return (
    <div className="flex min-h-screen">
      {/* Sidebar */}
      <aside className="hidden md:flex w-64 flex-col gap-2 border-r border-white/5 bg-[#0b101c]/60 p-6 sticky top-0 h-screen">
        <div className="mb-8 flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-primary to-primary-dark text-[#00201b] font-black text-lg shadow-lg glow">
            R
          </div>
          <div>
            <div className="text-sm font-bold text-white">JobRadar</div>
            <div className="text-[11px] text-text-secondary">求职雷达</div>
          </div>
        </div>

        <div className="mb-2 text-[10px] uppercase tracking-wider text-text-tertiary px-2">
          Product
        </div>
        {NAV.map((item) => {
          const active = pathname === item.href;
          return (
            <Link
              key={item.href}
              href={item.href}
              className={`rounded-xl px-3 py-2.5 text-sm transition ${
                active
                  ? "bg-primary/10 text-primary"
                  : "text-text-secondary hover:text-white hover:bg-white/5"
              }`}
            >
              <div className="font-medium">{item.label}</div>
              <div className="text-[10px] text-text-tertiary">{item.en}</div>
            </Link>
          );
        })}

        <div className="mt-auto rounded-xl glass p-3">
          <div className="text-[11px] text-text-secondary">
            <span className="text-primary font-semibold">v0.1</span> · Mock 数据源
          </div>
          <div className="mt-1 text-[10px] text-text-tertiary">
            端手一体 · AI 求职产品
          </div>
        </div>
      </aside>

      {/* Main */}
      <main className="flex-1 min-w-0">
        {/* Mobile top nav */}
        <div className="md:hidden flex items-center gap-1 border-b border-white/5 px-3 py-2 bg-[#0b101c]/60 sticky top-0 z-10">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-primary to-primary-dark text-[#00201b] font-black text-sm mr-2">
            R
          </div>
          {PRODUCT_NAV.map((item) => {
            const active = pathname === item.href;
            return (
              <Link
                key={item.href}
                href={item.href}
                className={`px-3 py-1.5 text-sm rounded-lg ${
                  active ? "bg-primary/10 text-primary" : "text-text-secondary"
                }`}
              >
                {item.label}
              </Link>
            );
          })}
        </div>

        <div className="px-5 md:px-10 py-8">{children}</div>
      </main>
    </div>
  );
}
