(ns commands
  (:require [db :as db]
            [clojure.string :as str]))

(defn- sql-type->spec-type [type]
  (get {"INTEGER" :int "TEXT" :string} type type))

(defn- metadata->spec [{:keys [name type notnull dflt_value]}]
  [(keyword name)
   {:coerce (sql-type->spec-type type)
    :require (and (= notnull 1) (nil? dflt_value))}])


(defn insert-spec [table]
  (->> (name table)
       db/column-metadata
       (map metadata->spec)
       (into {})))

(defn gen-insert-query [table cols]
  (let [col-keys    (keys cols)
        col-vals (vals cols)
        col-str (str/join ", " (map name col-keys))
        ?-str  (str/join ", " (repeat (count col-keys) "?"))
        sql-str (str "INSERT INTO " (name table) " (" col-str ") VALUES (" ?-str ")")]
    (into [sql-str] col-vals)))

(declare delete-spec) ; TODO
(declare gen-delete-query) ; TODO


(def commands
  "spec-fn: function that generates spec, query-gen-fn: function that generates query vector"
  { :add {:spec-fn insert-spec
          :query-gen-fn gen-insert-query}
   :delete {:spec-fn delete-spec
            :query-gen-fn gen-delete-query}})

(defn request->query [{:keys [cmd table cols]}]
  (let [query-fn (:query-gen-fn (get commands cmd))]
    (query-fn table cols)))

