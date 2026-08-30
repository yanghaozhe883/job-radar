import Link from "next/link";
import { jobs } from "@/lib/mock";

export default function Home() {
  const featured = jobs.slice(0, 3);
  return (
    <div className="max-w-5xl">
      {/* Hero */}
      <section className="py-6 md:py-12">
        <div className="inline-flex items-center gap-2 rounded-full glass px-3 py-1 text-[11px] text-primary mb-5">
          <span className="h-1.5 w-1.5 rounded-full bg-primary animate-pulse" />
          v0.1 · End-to-end Product Shell
        </div>
        <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight leading-tight">
          管理求职生命周期，
          <br />
          <span className="text-transparent bg-clip-text bg-gradient-to-r from-primary to-accent">
            而不是只找工作。
          </span>
        </h1>
        <p className="mt-5 max-w-xl text-text-secondary text-lg">
          JobRadar 收藏、投递记录、面试与 Offer，端手一体实时同步，并有 AI
          贯穿每个关键决策。现在开始，像管理产品一样管理你的职业。
        </p>
        <div className="mt-8 flex gap-3">
          <Link
            href="/explorer"
            className="rounded-xl bg-gradient-to-r from-primary to-primary-dark px-6 py-3 font-semibold text-[#00201b] hover:opacity-90 transition shadow-lg glow"
          >
            开始探索机会
          </Link>
          <Link
            href="/pipeline"
            className="rounded-xl glass px-6 py-3 font-medium text-white hover:bg-white/5 transition"
          >
            查看求职管道
          </Link>
        </div>
      </section>

      {/* Feature grid */}
      <section className="grid grid-cols-1 md:grid-cols-3 gap-4 mt-6">
        {[
          { t: "Opportunity Explorer", d: "聚合机会，智能匹配度一眼看懂。", icon: "◎" },
          { t: "Pipeline", d: "收藏 → 投递 → 面试 → Offer 全流程管理。", icon: "➤" },
          { t: "AI 贯穿", d: "JD 分析 / 匹配度 / 公司画像，AI 帮你决策。", icon: "✦" },
        ].map((f) => (
          <div key={f.t} className="glass p-6 hover:border-primary/30 transition">
            <div className="text-2xl text-primary mb-3">{f.icon}</div>
            <div className="font-semibold text-white">{f.t}</div>
            <div className="mt-1 text-sm text-text-secondary">{f.d}</div>
          </div>
        ))}
      </section>

      {/* Preview jobs */}
      <section className="mt-10">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-white">正在发生</h2>
          <Link href="/explorer" className="text-sm text-primary hover:underline">
            全部 →
          </Link>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {featured.map((job) => (
            <div key={job.id} className="glass p-5">
              <div className="flex items-start justify-between">
                <div>
                  <div className="font-semibold text-white text-sm">{job.title}</div>
                  <div className="text-xs text-text-secondary mt-1">{job.company}</div>
                </div>
                <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-gradient-to-br from-primary/20 to-accent/20 text-primary font-bold">
                  {job.companyLogo}
                </div>
              </div>
              <div className="mt-3 text-xs text-text-secondary">{job.city} · {job.salary}</div>
              <div className="mt-2 flex gap-1">
                {job.tags.slice(0, 2).map((t) => (
                  <span key={t} className="rounded-md bg-white/5 px-2 py-0.5 text-[11px] text-text-secondary">
                    {t}
                  </span>
                ))}
              </div>
            </div>
          ))}
        </div>
      </section>
    </div>
  );
}
