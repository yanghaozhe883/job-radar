"use client";

import { useEffect, useState } from "react";
import { useParams } from "next/navigation";
import { ExplorerJob } from "@/lib/types";

type Insight = {
  responsibilities: string[];
  coreSkills: string[];
  riskPoints: string[];
  growth: string[];
  whyRecommended: string[];
  match: {
    skillMatch: number;
    experienceMatch: number;
    directionMatch: number;
    skillReason: string;
    experienceReason: string;
    directionReason: string;
    overall: number;
  };
  generatedBy: string;
  model?: string;
};

export default function JobDetailPage() {
  const { id } = useParams<{ id: string }>();
  const [job, setJob] = useState<ExplorerJob | null>(null);
  const [insight, setInsight] = useState<Insight | null>(null);
  const [state, setState] = useState<"loading" | "loaded" | "fallback" | "error">("loading");

  useEffect(() => {
    let stale = false;
    async function load() {
      setState("loading");
      // 1) jobs list -> find this job
      try {
        const jobsRes = await fetch(`/api/jobs?pageSize=50`, { cache: "no-store" });
        const jobsData = await jobsRes.json();
        const found = (jobsData.items ?? []).find((j: ExplorerJob) => String(j.id) === String(id));
        if (!stale) setJob(found ?? null);

        // 2) insight
        const res = await fetch(`/api/jobs/${id}/insight`, { cache: "no-store" });
        const body = await res.json();
        if (!stale) {
          if (body?.insight) {
            setInsight(body.insight);
            setState(body.insight.generatedBy === "fallback" ? "fallback" : "loaded");
          } else {
            setState("error");
          }
        }
      } catch {
        if (!stale) setState("error");
      }
    }
    load();
    return () => { stale = true; };
  }, [id]);

  return (
    <div className="max-w-4xl">
      <h1 className="text-2xl font-bold text-white">Job Detail</h1>

      {/* Job header */}
      {job && (
        <div className="glass p-6 mt-4">
          <div className="flex items-center gap-4">
            <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-gradient-to-br from-primary/20 to-accent/20 text-primary font-bold text-xl">
              {job.companyLogo}
            </div>
            <div>
              <div className="text-xl font-semibold text-white">{job.title}</div>
              <div className="text-sm text-text-secondary mt-1">{job.company} · {job.city} · {job.salary}</div>
            </div>
          </div>
          <div className="mt-3 flex gap-1 flex-wrap">
            {job.tags.map((t) => (
              <span key={t} className="rounded-md bg-white/5 px-2 py-0.5 text-[11px] text-text-secondary">{t}</span>
            ))}
          </div>
        </div>
      )}

      {/* Insight Card */}
      <div className="mt-6">
        <h2 className="text-lg font-semibold text-white mb-3">AI Insight</h2>

        {state === "loading" && <div className="glass p-8 text-text-secondary text-center">正在分析这个岗位…</div>}

        {state === "error" && (
          <div className="glass p-8 text-center">
            <div className="text-text-secondary">AI 洞察暂不可用</div>
            <div className="text-xs text-text-tertiary mt-2">职位详情仍可正常查看</div>
          </div>
        )}

        {state === "fallback" && (
          <div className="glass p-8 text-center">
            <div className="text-text-secondary">AI 洞察暂不可用（模型未返回可解析结果）</div>
            <div className="text-xs text-text-tertiary mt-2">职位详情仍可正常查看</div>
          </div>
        )}

        {state === "loaded" && insight && (
          <div className="space-y-4">
            {/* Match */}
            <div className="glass p-6">
              <div className="flex items-center justify-between mb-5">
                <div className="font-semibold text-white">匹配度</div>
                <div className="text-3xl font-extrabold text-primary">{insight.match.overall}</div>
              </div>
              <div className="space-y-4">
                <MatchBar label="技能匹配" value={insight.match.skillMatch} color="text-primary" reason={insight.match.skillReason} />
                <MatchBar label="经验匹配" value={insight.match.experienceMatch} color="text-accent" reason={insight.match.experienceReason} />
                <MatchBar label="方向匹配" value={insight.match.directionMatch} color="text-[#22d07e]" reason={insight.match.directionReason} />
              </div>
              {insight.model && <div className="mt-4 text-[10px] text-text-tertiary">模型：{insight.model}</div>}
            </div>

            {/* 6 fields */}
            <InsightSection title="岗位职责" items={insight.responsibilities} icon="◎" />
            <InsightSection title="核心技能" items={insight.coreSkills} icon="✦" />
            <InsightSection title="风险点" items={insight.riskPoints} icon="!" />
            <InsightSection title="成长空间" items={insight.growth} icon="↑" />
            <InsightSection title="为什么推荐" items={insight.whyRecommended} icon="▶" />
          </div>
        )}
      </div>
    </div>
  );
}

function MatchBar({ label, value, color, reason }: { label: string; value: number; color: string; reason: string }) {
  return (
    <div>
      <div className="flex justify-between text-sm mb-1">
        <span className="text-text-secondary">{label}</span>
        <span className={`font-semibold ${color}`}>{value}</span>
      </div>
      <div className="h-2 rounded-full bg-white/5 overflow-hidden">
        <div className={`h-full rounded-full ${color}`} style={{ width: `${value}%` }} />
      </div>
      {reason && <div className="text-xs text-text-tertiary mt-1">{reason}</div>}
    </div>
  );
}

function InsightSection({ title, items, icon }: { title: string; items: string[]; icon: string }) {
  if (!items.length) return null;
  return (
    <div className="glass p-6">
      <div className="font-semibold text-white mb-2"><span className="text-primary mr-2">{icon}</span>{title}</div>
      <ul className="space-y-1.5">
        {items.map((it, i) => (
          <li key={i} className="text-sm text-text-secondary list-disc ml-4">{it}</li>
        ))}
      </ul>
    </div>
  );
}
