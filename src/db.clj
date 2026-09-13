(ns db
  (:require [pod.babashka.go-sqlite3 :as sqlite]
            [clojure.string :as str]))

(def ^:private db "db.sqlite3")

(defn query [query]
    (sqlite/query db query))

(defn execute! [query]
  "Execute query that modifies the database"
    (sqlite/execute! db query))

(def valid-tables
  #{:habits :expenses :tasks})

(def column-metadata
  (memoize
   (fn [table]
     (query [(format "PRAGMA table_info(%s);" (name table))]))))


(defn column-names [table]
  "Returns column names as keys"
  (into #{} (map #(keyword (:name %)) (column-metadata table))))

(defn has-column? [table col]
  (contains? (column-names table) (keyword col)))

