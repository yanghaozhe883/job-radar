import { jobs } from "@/lib/mock";

export default function ExplorerPage() {
  return (
    <div className="max-w-5xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Opportunity Explorer</h1>
        <p className="text-text-secondary mt-1">聚合机会，智能匹配度一眼看懂。</p>
      </div>

      {/* Search / filter bar */}
      <div className="glass p-3 flex flex-wrap gap-2 items-center mb-6">
        <div className="flex-1 min-w-[200px] flex items-center gap-2 px-3 py-2 rounded-lg bg-white/5">
          <span className="text-text-tertiary">⌕</span>
          <input
            placeholder="搜索职位 / 公司 / 技能…"
            className="bg-transparent outline-none text-sm w-full text-white placeholder:text-text-tertiary"
          />
        </div>
        {["全部", "全职", "实习", "远程"].map((f, i) => (
          <span
            key={f}
            className={`px-3 py-1.5 rounded-lg text-sm ${
              i === 0 ? "bg-primary/15 text-primary" : "text-text-secondary hover:text-white"
            }`}
          >
            {f}
          </span>
        ))}
      </div>

      {/* Job list */}
      <div className="grid grid-cols-1 gap-3">
        {jobs.map((job) => (
          <div key={job.id} className="glass p-5 hover:border-primary/30 transition">
            <div className="flex items-center gap-4">
              <div className="flex h-12 w-12 items-center justify-center rounded-xl bg-gradient-to-br from-primary/20 to-accent/20 text-primary font-bold text-lg">
                {job.companyLogo}
              </div>
              <div className="flex-1 min-w-0">
                <div className="font-semibold text-white">{job.title}</div>
                <div className="text-xs text-text-secondary mt-0.5">{job.company}</div>
              </div>
              <div className="text-right">
                <div className="text-primary font-bold">{job.score}</div>
                <div className="text-[10px] text-text-tertiary">匹配度</div>
              </div>
            </div>
            <div className="mt-3 flex flex-wrap items-center gap-2 text-xs text-text-secondary">
              <span>{job.city}</span>
              <span className="text-text-tertiary">·</span>
              <span className="text-primary">{job.salary}</span>
              <span className="text-text-tertiary">·</span>
              <span>{job.published}</span>
              <div className="ml-auto flex gap-1">
                {job.tags.slice(0, 3).map((t) => (
                  <span key={t} className="rounded-md bg-white/5 px-2 py-0.5 text-[11px] text-text-secondary">
                    {t}
                  </span>
                ))}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
