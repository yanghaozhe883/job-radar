import Foundation

// Pure domain model. Mirrors Android's `domain/model/Job.kt`.
struct Job: Identifiable, Hashable {
    let id: Int64
    let title: String
    let city: String
    let salaryMinK: Int
    let salaryMaxK: Int
    let jobType: JobType
    let experience: ExperienceLevel
    let education: EducationLevel
    let skills: [String]
    let description: String?
    let company: Company?
    let dataSource: String?
    let publishedAt: Int64?

    var salaryLabel: String { "\(salaryMinK)-\(salaryMaxK)K" }
}

enum JobType: String, CaseIterable {
    case fullTime = "FULL_TIME"
    case partTime = "PART_TIME"
    case internship = "INTERNSHIP"
    case remote = "REMOTE"
    case contract = "CONTRACT"

    var label: String {
        switch self {
        case .fullTime: return "全职"
        case .partTime: return "兼职"
        case .internship: return "实习"
        case .remote: return "远程"
        case .contract: return "外包"
        }
    }
}

enum ExperienceLevel: String, CaseIterable {
    case fresh = "FRESH", junior = "JUNIOR", mid = "MID", senior = "SENIOR", expert = "EXPERT"

    var label: String {
        switch self {
        case .fresh: return "应届"
        case .junior: return "1-3年"
        case .mid: return "3-5年"
        case .senior: return "5-10年"
        case .expert: return "10年以上"
        }
    }
}

enum EducationLevel: String, CaseIterable {
    case highSchool = "HIGH_SCHOOL", associate = "ASSOCIATE", bachelor = "BACHELOR", master = "MASTER", phd = "PHD"

    var label: String {
        switch self {
        case .highSchool: return "高中"
        case .associate: return "大专"
        case .bachelor: return "本科"
        case .master: return "硕士"
        case .phd: return "博士"
        }
    }
}

struct Company: Hashable {
    let id: Int64
    let name: String
    let logoUrl: String?
    let industry: String?
    let size: String?
    let city: String?
    let financingStage: String?
    let description: String?
}
