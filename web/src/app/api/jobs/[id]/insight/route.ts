import { NextResponse } from "next/server";

const API = process.env.JOBRADAR_API ?? "http://127.0.0.1:8080/api/v1";

/**
 * GET /api/jobs/[id]/insight — thin proxy to backend /jobs/{id}/insight.
 * Web never knows the AI provider; only the backend does.
 */
export async function GET(
  request: Request,
  { params }: { params: Promise<{ id: string }> },
) {
  const { id } = await params;
  const { searchParams } = new URL(request.url);
  const qs = searchParams.toString();
  const url = `${API}/jobs/${id}/insight${qs ? `?${qs}` : ""}`;

  let insight: Record<string, unknown> | null = null;
  let code = "unavailable";
  let message = "";
  try {
    const res = await fetch(url, { cache: "no-store" });
    const body = await res.json();
    code = body?.code === 0 ? "ok" : "error";
    message = body?.message ?? "";
    insight = body?.data ?? null;
  } catch (e) {
    message = e instanceof Error ? e.message : "后端不可达";
  }

  return NextResponse.json({ code, message, insight });
}
