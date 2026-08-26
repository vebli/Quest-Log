(ns db
  (:require [pod.babashka.go-sqlite3 :as sqlite]
            [clojure.string :as str]))

(def db "db.sqlite3")

(defn query [query]
  (sqlite/query db query))

(defn execute! [query]
  "Execute query that modifies the database"
  (sqlite/execute! db query))

(defn column-metadata [table]
  (query [(format "PRAGMA table_info(%s);" table)]))


(defn- build-insert-query [{:keys [table opts]}]
  (let [cols    (keys opts)
        col-str (str/join ", " (map name cols))
        ?-str  (str/join ", " (repeat (count cols) "?"))
        sql-str (str "INSERT INTO " (name table) " (" col-str ") VALUES (" ?-str ")")]
    (into [sql-str] (vals opts))))

;; (defn execute-cmd! [{:keys [cmd table opts]}]
;;   (let [param (...) ; generate (name, frequency, ...)
;;         param-vals] (...) ; generate (?, ?, ...)
;;     (case
;;         ("]add") [(str "INSERT INTO habits" params "VALUES" param-vals)])))
