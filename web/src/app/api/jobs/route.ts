import { NextResponse } from "next/server";

// JobRadar backend base URL — the Web client never hardcodes data sources.
// It only knows "the backend". Config via env JOBRADAR_API (local default localhost:8080).
const API = process.env.JOBRADAR_API ?? "http://127.0.0.1:8080/api/v1";

interface JobItem {
  id: number;
  title: string;
  city: string;
  salary_min_k: number;
  salary_max_k: number;
  job_type?: string;
  experience?: string;
  education?: string;
  skills: string[];
  description?: string;
  company?: { name: string; city?: string; industry?: string; size?: string } | null;
  data_source?: string;
  match_score?: number;
}

/**
 * GET /api/jobs — thin, read-only proxy to the backend's JobProvider read API.
 * The Web app talks ONLY to /api/jobs; it never knows about Mock / CSV / plugins.
 */
export async function GET(request: Request) {
  const { searchParams } = new URL(request.url);
  const qs = searchParams.toString();
  const url = `${API}/jobs${qs ? `?${qs}` : ""}`;

  let jobs: JobItem[] = [];
  let total = 0;
  try {
    const res = await fetch(url, { cache: "no-store" });
    if (res.ok) {
      const body = await res.json();
      total = body?.data?.total ?? (body?.data?.items?.length ?? 0);
      jobs = (body?.data?.items ?? []).map((j: JobItem) => normalize(j));
    }
  } catch {
    // Backend unreachable -> return empty; the UI renders an empty/fallback state.
  }

  return NextResponse.json({ total, items: jobs });
}

/** Normalize the backend's snake_case into the Web's UI shape. */
function normalize(j: JobItem) {
  return {
    id: Number(j.id),
    title: j.title,
    company: j.company?.name ?? "未知",
    companyLogo: (j.company?.name ?? "?").slice(0, 1),
    city: j.city,
    salary: j.salary_min_k && j.salary_max_k ? `${j.salary_min_k}-${j.salary_max_k}K` : "",
    tags: j.skills ?? [],
    score: j.match_score ?? 0,
    source: j.data_source ?? "",
  };
}
