(ns domain.habits
  (:require [sql :as sql]
            [honey.sql :as hsql]
            [honey.sql.helpers :as hsqlh]
            [schema.db :as schema]
            [clojure.pprint :refer [print-table]]
            [dispatch :as d]
            [malli.core :as m]))


(defmethod d/schema [:habits :add]
  [request]
  [:map
   [:cols (schema/insertion (schema/col-base :habits))]])

(defmethod d/query [:habits :add]
  [request]
  (sql/insertion-query request))


(defmethod d/schema [:habits :delete]
  [request]
  [:map
   [:cols (schema/require-cols (schema/col-base :habits) #{:id})]])

(defmethod d/query [:habits :delete]
  [request]
  (sql/deletion-query request))

(defmethod d/schema [:habits :list]
  [request]
  nil)

(defmethod d/query [:habits :list]
  [{:keys [opts]}]
  (let [{:keys [today week month]} opts]
    (hsql/format
     (cond-> (hsqlh/from :habits)
       today (hsqlh/where)
       week (hsqlh/where)
       month (hsqlh/where)
     
       ))))

(defmethod d/display [:habits :list]
  [request query-result]
  (print-table query-result))

