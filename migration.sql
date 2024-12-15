CREATE TABLE runs (
    id INTEGER PRIMARY KEY,
    start_time INTEGER DEFAULT CURRENT_TIMESTAMP,
    projects TEXT
);

CREATE TABLE test_results (
    id INTEGER PRIMARY KEY,
    run_id INTEGER,
    project TEXT,
    test_suite TEXT,
    test_id TEXT,
    result TEXT,
    test_order TEXT
);