(ns sql
  (:require [clojure.string :as str])
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

(defn- sql-type->spec-type [type]
  (get {"INTEGER" :int "TEXT" :string} type type))

(def sqlite-datetime-fmt
  (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss"))

(def sqlite-date-fmt
  (DateTimeFormatter/ofPattern "yyyy-MM-dd"))

(defn now [format]
  (.format (LocalDateTime/now) format))

(defn rel-now [format offset]
  (.format (.plusDays (LocalDateTime/now) offset) format))


(defn placeholder-str [count] (str "(" (str/join ", " (repeat count "?")) ")"))
(defn param-str [params] (str "(" (str/join ", " (map name params)) ")"))

(defn where-equals [cols]
  (str "WHERE "
   (str/join " AND "
    (mapv (fn [[k v]] (str/join " " [(name k) "=" v]))
          cols))))

(defn gen-insert-query [{:keys [table cols]}]
  (let [col-keys    (keys cols)
        col-vals (vals cols)
        sql-str (str/join " " ["INSERT INTO" (name table)  (param-str col-keys) "VALUES" (placeholder-str (count col-keys))])]
    (into [sql-str] col-vals)))

(defn gen-delete-query [{:keys [table cols]}]
  [(format "DELETE FROM %s WHERE id = ?" (name table)), (:id cols)])
