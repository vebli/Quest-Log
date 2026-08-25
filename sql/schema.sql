PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS recurrence_rules (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    frequency TEXT NOT NULL CHECK(frequency IN ('daily', 'weekly', 'monthly', 'yearly')),
    interval_val INTEGER NOT NULL DEFAULT 1, 
    by_day TEXT,                             -- e.g., 'MO,WE,FR'
    start_date TEXT NOT NULL,                -- ISO8601 'YYYY-MM-DD'
    end_date TEXT                            -- NULL = recurring infinitely
);

-- Tasks

CREATE TABLE IF NOT EXISTS tasks (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    default_priority INTEGER NOT NULL DEFAULT 2 CHECK(default_priority IN (1, 2, 3)),
    recurrence_id INTEGER UNIQUE REFERENCES recurrence_rules(id) ON DELETE SET NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS task_instances (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    task_id INTEGER REFERENCES tasks(id) ON DELETE SET NULL,
    scheduled_date TEXT NOT NULL,      
    priority INTEGER NOT NULL,
    status TEXT NOT NULL DEFAULT 'pending' CHECK(status IN ('pending', 'completed', 'skipped')),
    completed_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_task_instances_date ON task_instances(scheduled_date);

-- Habits

CREATE TABLE IF NOT EXISTS habits (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    target_count INTEGER NOT NULL DEFAULT 1,
    unit TEXT NOT NULL DEFAULT 'times', 
    recurrence_id INTEGER UNIQUE REFERENCES recurrence_rules(id) ON DELETE SET NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS habit_instances (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    habit_id INTEGER REFERENCES habits(id) ON DELETE SET NULL,
    scheduled_date TEXT NOT NULL,      
    current_count INTEGER NOT NULL DEFAULT 0,
    target_count INTEGER NOT NULL,    
    status TEXT NOT NULL DEFAULT 'pending' CHECK(status IN ('pending', 'completed', 'skipped')),
    completed_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_habit_instances_date ON habit_instances(scheduled_date);

-- Expenses

CREATE TABLE IF NOT EXISTS expenses (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    category TEXT NOT NULL,
    default_amount_cents INTEGER NOT NULL,
    recurrence_id INTEGER UNIQUE REFERENCES recurrence_rules(id) ON DELETE SET NULL,
    created_at TEXT DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS expense_instances (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    expense_id INTEGER REFERENCES expenses(id) ON DELETE SET NULL,
    scheduled_date TEXT NOT NULL,      -- ISO8601 'YYYY-MM-DD'
    amount_cents INTEGER NOT NULL,     -- SNAPSHOT: Frozen cost for this instance
    status TEXT NOT NULL DEFAULT 'pending' CHECK(status IN ('pending', 'paid', 'skipped')),
    paid_at TEXT
);

CREATE INDEX IF NOT EXISTS idx_expense_instances_date ON expense_instances(scheduled_date);
