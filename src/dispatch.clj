(ns dispatch 
  (:require [db :as db]
            [clojure.string :as str]))

(defmulti schema
  (fn [{:keys [table cmd] :as request}]
    [table cmd]))

(defmethod schema :default [request] nil)

(defmulti query
  (fn [{:keys [table cmd] :as request}]
    [table cmd]))

(defmethod query :default [request] nil)
