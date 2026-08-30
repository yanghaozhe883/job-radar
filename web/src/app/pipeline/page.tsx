import { pipeline, jobs, stages } from "@/lib/mock";

export default function PipelinePage() {
  const jobById = Object.fromEntries(jobs.map((j) => [j.id, j]));

  return (
    <div className="max-w-6xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Pipeline</h1>
        <p className="text-text-secondary mt-1">收藏 → 投递 → 面试 → Offer，全流程管理。</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        {stages.map((stage) => {
          const items = pipeline.filter((p) => p.stage === stage);
          return (
            <div key={stage} className="glass p-4 min-h-[300px]">
              <div className="flex items-center justify-between mb-4">
                <span className="text-sm font-semibold text-white">{stage}</span>
                <span className="text-xs text-text-tertiary">{items.length}</span>
              </div>
              <div className="flex flex-col gap-3">
                {items.length === 0 && (
                  <div className="text-xs text-text-tertiary text-center py-8">暂无</div>
                )}
                {items.map((item) => {
                  const job = jobById[item.jobId];
                  if (!job) return null;
                  return (
                    <div key={item.jobId} className="rounded-xl bg-white/5 p-3 border border-white/5 hover:border-primary/30 transition">
                      <div className="flex items-center gap-2">
                        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-gradient-to-br from-primary/20 to-accent/20 text-primary text-xs font-bold">
                          {job.companyLogo}
                        </div>
                        <div className="min-w-0">
                          <div className="text-sm font-medium text-white truncate">{job.title}</div>
                          <div className="text-[10px] text-text-secondary truncate">{job.company} · {job.salary}</div>
                        </div>
                      </div>
                      <div className="mt-2 text-[11px] text-text-tertiary">{item.note}</div>
                    </div>
                  );
                })}
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
