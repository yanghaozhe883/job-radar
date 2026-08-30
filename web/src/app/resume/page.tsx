export default function ResumePage() {
  const skills = [
    { label: "AI Application", detail: "多模型路由 · RAG 完整链路 · Tool Calling 状态机" },
    { label: "Agent Engineering", detail: "任务编排 · 角色化执行 · 人工审批 · 知识库" },
    { label: "Full Stack", detail: "Python · Go · TS · Kotlin · Next.js · Android Compose" },
    { label: "Infra", detail: "Linux · Docker 沙箱 · Caddy/Nginx · CI/CD" },
  ];
  const projects = [
    { title: "王庭 Aether Court", opt: "多 Agent 协作与调度平台", flag: "旗舰" },
    { title: "求职雷达 JobRadar", opt: "端手一体 AI 求职产品" },
    { title: "芥子 Singularity", opt: "流式 AI 对话 + RAG" },
  ];

  return (
    <div className="max-w-5xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Resume Workspace</h1>
        <p className="text-text-secondary mt-1">你的简历，也是产品的一部分。</p>
      </div>

      {/* Header */}
      <div className="glass p-6">
        <div className="flex items-center gap-4">
          <div className="flex h-14 w-14 items-center justify-center rounded-2xl bg-gradient-to-br from-primary to-primary-dark text-[#00201b] font-extrabold text-xl">
            杨
          </div>
          <div>
            <div className="flex items-center gap-2">
              <span className="text-xl font-bold text-white">杨浩哲</span>
              <span className="text-xs text-primary">Agent · AI Application · Full Stack</span>
            </div>
            <div className="text-xs text-text-secondary mt-1">
              137-3003-9933 · 3529682358@163.com · 河北保定 · 2027 年毕业
            </div>
          </div>
        </div>
      </div>

      {/* Skills */}
      <div className="mt-6">
        <h2 className="text-lg font-semibold text-white mb-3">核心技能</h2>
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {skills.map((s) => (
            <div key={s.label} className="glass p-5">
              <div className="text-primary font-semibold text-sm">{s.label}</div>
              <div className="mt-1 text-xs text-text-secondary">{s.detail}</div>
            </div>
          ))}
        </div>
      </div>

      {/* Projects */}
      <div className="mt-6">
        <h2 className="text-lg font-semibold text-white mb-3">项目经历</h2>
        <div className="flex flex-col gap-3">
          {projects.map((p) => (
            <div key={p.title} className="glass p-5 flex items-center gap-3">
              {p.flag && (
                <span className="rounded-md bg-gradient-to-r from-accent to-primary px-2 py-0.5 text-[11px] font-semibold text-[#00201b]">
                  {p.flag}
                </span>
              )}
              <span className="font-semibold text-white text-sm">{p.title}</span>
              <span className="text-xs text-text-secondary">{p.opt}</span>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
