(ns domain.habits
  (:require [sql :as sql]
            [schema :as schema]
            [dispatch :as d]
            [malli.core :as m]))


(defmethod d/schema [:habits :add]
  [request]
  [:cols (schema/insertion (schema/col-base :habits))])

(defmethod d/query [:habits :add]
  [request]
  (sql/gen-insert-query request))


(defmethod d/schema [:habits :delete]
  [request]
  [:cols (schema/require-cols (schema/col-base :habits) #{:id})])

(defmethod d/query [:habits :delete]
  [request]
  (sql/gen-delete-query request))

;; (defmethod d/schema [:habits :list]
;;   [request]
;;   nil)

;; (defmethod d/query [:habits :list]
;;   [requeste]
;;   nil)



