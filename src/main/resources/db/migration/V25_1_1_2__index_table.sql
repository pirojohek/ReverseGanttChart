CREATE INDEX idx_component_start_planned
ON storage.t_project_component (c_start_date)
WHERE c_global_status = 'PLANNED'

CREATE INDEX idx_component_start_planned
ON storage.t_project_component (c_deadline)
WHERE c_global_status IN ('DELAYED', 'IT_IS_TIME', 'IN_PROGRESS',
    'REJECTED')