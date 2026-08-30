export default function SettingsPage() {
  const prefs = [
    { label: "目标城市", value: "上海", options: ["上海", "北京", "深圳", "杭州", "广州"] },
    { label: "目标岗位", value: "AI 应用开发 / Agent 工程", options: [] },
  ];

  return (
    <div className="max-w-3xl">
      <div className="mb-6">
        <h1 className="text-2xl font-bold text-white">Settings</h1>
        <p className="text-text-secondary mt-1">雷达目标配置 · 数据源 · 偏好</p>
      </div>

      {/* Prefs */}
      <div className="glass p-6 space-y-6">
        <div>
          <div className="text-sm font-semibold text-white mb-3">目标城市</div>
          <div className="flex flex-wrap gap-2">
            {["上海", "北京", "深圳", "杭州", "广州"].map((c, i) => (
              <span
                key={c}
                className={`px-4 py-1.5 rounded-lg text-sm ${
                  i === 0 ? "bg-primary/15 text-primary border border-primary/30" : "bg-white/5 text-text-secondary"
                }`}
              >
                {c}
              </span>
            ))}
          </div>
        </div>

        <div>
          <div className="text-sm font-semibold text-white mb-3">目标岗位</div>
          <div className="flex flex-wrap gap-2">
            {["AI 应用开发", "智能体工程", "Android 开发", "全栈开发"].map((r, i) => (
              <span
                key={r}
                className={`px-4 py-1.5 rounded-lg text-sm ${
                  i < 2 ? "bg-primary/15 text-primary border border-primary/30" : "bg-white/5 text-text-secondary"
                }`}
              >
                {r}
              </span>
            ))}
          </div>
        </div>

        <div>
          <div className="text-sm font-semibold text-white mb-3">数据源</div>
          <div className="rounded-lg bg-white/5 p-3 text-xs text-text-secondary">
            <span className="text-primary font-medium">JobProvider</span> · 当前 v0.1 Mock 数据源，可插拔扩展（CSV / API / Plugin）
          </div>
        </div>
      </div>

      <div className="mt-6 glass p-6 flex items-center justify-between">
        <div>
          <div className="text-sm font-semibold text-white">保存配置</div>
          <div className="text-xs text-text-secondary mt-1">端手一体实时同步</div>
        </div>
        <button className="rounded-xl bg-gradient-to-r from-primary to-primary-dark px-6 py-3 font-semibold text-[#00201b] hover:opacity-90 transition shadow-lg glow">
          保存雷达配置
        </button>
      </div>
    </div>
  );
}
