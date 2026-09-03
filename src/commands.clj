(ns dispatch 
  (:require [db :as db]
            [clojure.string :as str]
            [domain.habits :as habits]))

(defmulti table-spec
  (fn [{:keys [table cmd]}]
    [table cmd]))

(defmulti gen-query
  (fn [{:keys [table cmd]}]
    [table cmd]))

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

;; (defn gen-list-query [{:keys [table cols opts]}]
;;   (fn [{:keys [table]}]
;;     (let [sql-str (format "SELECT * FORM %s WHERE " table )])
;;     [sql-str, ]))

;; (def commands
;;   "spec-fn: request -> spec
;;    query-gen-fn: table -> query vector"
;;    :tasks {:add {:spec-fn gen-insert-spec
;;                  :query-gen-fn gen-insert-query}
;;            :delete {:spec-fn gen-delete-spec
;;                     :query-gen-fn gen-delete-query}}})



;; (defmethod commands
;;   )


(defn request->query [{:keys [cmd table] :as request} ]
  (let [query-fn (:query-gen-fn (get-in commands [table cmd]))]
    (query-fn request)))

