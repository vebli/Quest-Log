(ns main (:import [java.util Date]))
(require '[next.jdbc :as jdbc]
         '[clojure.string :as str])

(def db {:dbtype "sqlite" :dbname "db.sqlite3"})
(def ds (jdbc/get-datasource db))
(jdbc/execute! ds [(slurp "sql/schema.sql")])

(jdbc/execute! ds ["
INSERT INTO habits (name) VALUES ('workout')
"])

(:habits/name (first (jdbc/execute! ds [" SELECT * FROM habits "])))

(comment
  (def args (str/split "quest add habit name:read" #"[\s]")))

(defn usage []
  (println "Usage: quest [command] [table] [column:value]"))

(defn valid-table? [s]
  (let [tables #{"habit" "task" "expense"}]
    (contains? tables s)))

(valid-table? "habit")

(defn parse-args [args]
  (into {}
        (comp
         (filter #(re-find #"^[a-zA-Z]+:[a-zA-Z]+$" %))
         (map #(str/split % #":"))
         (map (fn [[k v]] [(keyword k) v])))
        args))

(defn -main [args]
  (case (second args)
    ("add") (table-add)
    ("delete" "del") ()
    (usage)))

(-main args)

