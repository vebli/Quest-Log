(ns db
  (:require [pod.babashka.go-sqlite3 :as sqlite]
            [clojure.string :as str]
            [commands :as cmd]))

(def ^:private db "db.sqlite3")

(defn query [query]
    (sqlite/query db query))

(defn execute! [query]
  "Execute query that modifies the database"
    (sqlite/execute! db query))

(defn column-metadata [table]
  (query [(format "PRAGMA table_info(%s);" (name table))]))

(defn column-names [table]
  "Returns column names as keys"
  (map #(keyword (:name %)) (column-metadata table)))

