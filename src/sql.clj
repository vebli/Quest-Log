(ns sql
  (:require [clojure.string :as str]
            [honey.sql :as sql]
            [honey.sql.helpers :as sqlh])
  (:import [java.time LocalDateTime]
           [java.time.format DateTimeFormatter]))

(defn insertion-query [{:keys [table cols]}]
  (-> table
      (sqlh/insert-into)
      (sqlh/values [cols])
      (sql/format)
      ))

(defn deletion-query [{:keys [table cols]}]
  (let [{:keys [id]} cols]
    (-> table
        (sqlh/delete-from)
        (sqlh/where [:= :id id])
        (sql/format))))

