(ns commands
  (:require [db :as db]
            [clojure.string :as str]
            [domain.habits :as habits]))

(defmulti table-spec
  (fn [{:keys [table cmd]}]
    [table cmd]))

(defmethod table-spec :default [request] nil)

(defmulti gen-query
  (fn [{:keys [table cmd]}]
    [table cmd]))

(defmethod gen-query :default [request] nil)


;; (defn- metadata->spec [{:keys [name type notnull dflt_value]}]
;;   [(keyword name)
;;    {:coerce (sql-type->spec-type type)
;;     :require (and (= notnull 1) (nil? dflt_value))}])

;; (defn gen-insert-spec [table]
;;   (->> (name table)
;;        db/column-metadata
;;        (map metadata->spec)
;;        (into {})))


;; (defn gen-delete-spec [_]
;;   {:id {:coerce :int
;;         :require true}})

;; (defn request->query [{:keys [cmd table] :as request} ]
;;   (let [query-fn (:query-gen-fn (get-in commands [table cmd]))]
;;     (query-fn request)))

