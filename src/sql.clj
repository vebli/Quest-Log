(ns domain.util.sql
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

(defn gen-insert-query [{:keys [table cols]}]
  (let [col-keys    (keys cols)
        col-vals (vals cols)
        col-str (str/join ", " (map name col-keys))
        ?-str  (str/join ", " (repeat (count col-keys) "?"))
        sql-str (str "INSERT INTO " (name table) " (" col-str ") VALUES (" ?-str ")")]
    (into [sql-str] col-vals)))

(defn gen-delete-query [{:keys [table cols]}]
  [(format "DELETE FROM %s WHERE id = ?" (name table)), (:id cols)])
