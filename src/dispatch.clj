(ns dispatch 
  (:require [db :as db]
            [clojure.string :as str]
            [domain.habits :as habits]))

(defmulti request-schema
  (fn [{:keys [table cmd]}]
    [table cmd]))

(defmethod request-schema :default [request] nil)

(defmulti query
  (fn [{:keys [table cmd]}]
    [table cmd]))

(defmethod query :default [request] nil)





;; (defn gen-delete-spec [_]
;;   {:id {:coerce :int
;;         :require true}})

;; (defn request->query [{:keys [cmd table] :as request} ]
;;   (let [query-fn (:query-gen-fn (get-in commands [table cmd]))]
;;     (query-fn request)))

