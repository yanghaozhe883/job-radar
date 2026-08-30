// JobRadar · Unified domain model (v0.2 · Connect)
// The Web client only ever sees this shape — it never knows the backend's data source.
export type ExplorerJob = {
  id: number;
  title: string;
  company: string;
  companyLogo: string;
  city: string;
  salary: string;
  tags: string[];
  score: number;
  source: string;
};

export type JobListResponse = {
  total: number;
  provider: string;
  items: ExplorerJob[];
};
