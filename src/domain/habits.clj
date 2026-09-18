(ns domain.habits
  (:require [sql :as sql]
            [schema :as schema]
            [dispatch :as dis]
            [malli.core :as m]))

;; (defmethod table-spec [:habits :add]
;;   [request]
;;   ())

(defmethod dis/request-schema [:habits :add]
  [request]
  (schema/insertion (schema/col-base :habits)))

;; (defmethod dis/query [:habits :add]
;;   [request]
;;   (sql/gen-insert-query request))

;; (defmethod dis/request-schema [:habits :delete])
;; (defmethod dis/query [:habits :delete])

;; (defmethod table-spec [:habits :list])
;; (defmethod gen-query [:habits :list])

;; (defn gen-list-query [{:keys [opts]}]
;;   ["SELECT "])


;; (defn gen-list-query [{:keys [table cols opts]}]
;;   (fn [{:keys [table]}]
;;     (let [sql-str (format "SELECT * FORM %s WHERE " table )])
;;     [sql-str, ]))

;; (defn gen-list-query [{:keys [cols opts]}]
;;   ())


;; (defn gen-add-query [{:keys [cols opts]}]
;;   )
