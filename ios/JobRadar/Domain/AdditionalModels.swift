import Foundation

// Pure domain models. Mirrors Android `domain/model/UserPreference.kt` + JobFilter etc.

struct UserPreference: Hashable {
    var city: String = "上海"
    var targetRoles: [String] = ["前端工程师", "Android 工程师"]
    var skillTags: [String] = []
    var salaryMinK: Int = 20
    var salaryMaxK: Int = 45
    var preferredJobTypes: [String] = ["全职"]
    var yearsOfExperience: Int = 3
    var preferredCompanies: [String] = []

    static let `default` = UserPreference()
}

struct MatchScore: Hashable {
    let total: Int
    let salaryMatch: Int
    let skillMatch: Int
    let companyMatch: Int
    let reason: String
}

struct User: Hashable, Identifiable {
    let id: Int64
    let phone: String?
    let nickname: String?
    let avatarUrl: String?
}

// Job filter + interaction status (mirrors Android `JobFilter.kt`).
struct JobFilter: Hashable {
    var city: String? = nil
    var keyword: String? = nil
    var jobTypes: [String] = []
    var minSalaryK: Int? = nil
    var sort: Sort = .comprehensive
}

enum Sort: String, CaseIterable {
    case comprehensive = "COMPREHENSIVE"
    case latest = "LATEST"
    case salary = "SALARY"
    case match = "MATCH"

    var label: String {
        switch self {
        case .comprehensive: return "综合"
        case .latest: return "最新"
        case .salary: return "薪资"
        case .match: return "匹配"
        }
    }
}

enum JobStatus: String {
    case seen = "SEEN"
    case favorite = "FAVORITE"
    case applied = "APPLIED"
    case hidden = "HIDDEN"
}

/// Real-time push from the backend WebSocket (mirrors `JobPushEvent`).
struct JobPushSignal: Hashable {
    let jobId: Int64
    let title: String?
    let matchScore: Int?
}
