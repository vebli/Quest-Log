(ns db)
(require '[pod.babashka.go-sqlite3 :as sqlite])

(def db "db.sqlite3")

(defn query [query]
  (sqlite/query db query))

(defn execute! [query]
  "Execute query that modifies the database"
  (sqlite/execute! db query))

(defn column-metadata [table]
  (query [(format "PRAGMA table_info(%s);" table)]))

(defn add [args]
  (println args))

(defn delete [args])

