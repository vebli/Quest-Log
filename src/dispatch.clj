(ns dispatch 
  (:require [db :as db]
            [schema.db :as schema]
            [clojure.string :as str]))

(defmulti schema
  (fn [{:keys [table cmd] :as request}]
    [table cmd]))

(defmethod schema :default
  [{:keys [table]}]
  (schema/col-base table))

(defmulti query
  (fn [{:keys [table cmd] :as request}]
    [table cmd]))

(defmethod query :default
  [_] nil)

(defmulti display
  (fn [{:keys [table cmd] :as request} query-result]
  [table cmd]))

(defmethod display :default
  [_ query-result]
  (println query-result))

;; (defmacro domain-implementer [table]
;;   `(defmacro ~(symbol (str/join "-" ["impl" (name table)])) [cmd methods]
;;     (map
;;         (fn [fname args impl]
;;           `(defmethod ~(symbol fname) [~table ~cmd]
;;              ~args
;;              ~impl))
;;       methods)))

