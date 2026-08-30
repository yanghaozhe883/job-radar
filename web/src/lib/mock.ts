// JobRadar · Mock data (v0.1 — replace via JobProvider plugin later)
export type Job = {
  id: number;
  title: string;
  company: string;
  companyLogo: string; // initial
  city: string;
  salary: string;
  tags: string[];
  score: number;
  kind: "full-time" | "intern" | "remote";
  published: string;
};

export const jobs: Job[] = [
  { id: 1, title: "iOS 开发工程师", company: "北辰科技", companyLogo: "北", city: "上海", salary: "26-45K", tags: ["Swift", "SwiftUI"], score: 60, kind: "full-time", published: "02h" },
  { id: 2, title: "产品经理", company: "北辰科技", companyLogo: "北", city: "上海", salary: "25-45K", tags: ["产品规划", "用户研究"], score: 60, kind: "full-time", published: "05h" },
  { id: 3, title: "算法工程师", company: "深云信息", companyLogo: "深", city: "深圳", salary: "35-55K", tags: ["Python", "LLM"], score: 60, kind: "full-time", published: "1d" },
  { id: 4, title: "后端开发（Kotlin）", company: "瀚海数据", companyLogo: "瀚", city: "北京", salary: "30-50K", tags: ["Kotlin", "Spring"], score: 72, kind: "full-time", published: "1d" },
  { id: 5, title: "AI 应用工程师（实习）", company: "芥子科技", companyLogo: "芥", city: "杭州", salary: "8-12K", tags: ["RAG", "Agent"], score: 88, kind: "intern", published: "3h" },
  { id: 6, title: "前端开发（Next.js）", company: "云端科技", companyLogo: "云", city: "成都", salary: "22-40K", tags: ["Next.js", "TS"], score: 66, kind: "remote", published: "2d" },
  { id: 7, title: "智能体工程师", company: "灵犀实验室", companyLogo: "灵", city: "上海", salary: "40-70K", tags: ["Multi-Agent", "Tool Calling"], score: 92, kind: "full-time", published: "6h" },
  { id: 8, title: "运维工程师", company: "中电智广", companyLogo: "中", city: "石家庄", salary: "10-16K", tags: ["Linux", "Docker"], score: 51, kind: "full-time", published: "3d" },
];

export type SavedItem = { jobId: number; stage: string; note: string };
export const pipeline: SavedItem[] = [
  { jobId: 5, stage: "已投递", note: "AI 应用实习 · 方向契合" },
  { jobId: 7, stage: "面试中", note: "一面通过 · 等二面" },
  { jobId: 4, stage: "已收藏", note: "Kotlin 后端 · 平台大" },
  { jobId: 6, stage: "已收藏", note: "远程可投" },
];

export const stages = ["已收藏", "已投递", "面试中", "Offer"];
