(ns domain.habits
  (:require [domain.util.sql :as sql]
            [malli.core :as m]))

;; (defmethod table-spec [:habits :add]
;;   [request]
;;   ())

(defmethod gen-query [:habits :add]
  [request]
  (sql/gen-insert-query request))

(defmethod table-spec [:habits :delete])
(defmethod gen-query [:habits :delete])

(defmethod table-spec [:habits :list])
(defmethod gen-query [:habits :list])

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
