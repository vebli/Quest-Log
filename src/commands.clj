(ns commands
  (:require [db :as db]
            [clojure.string :as str]
            [domain.habits :as habits]))

(defn- sql-type->spec-type [type]
  (get {"INTEGER" :int "TEXT" :string} type type))

(defn- metadata->spec [{:keys [name type notnull dflt_value]}]
  [(keyword name)
   {:coerce (sql-type->spec-type type)
    :require (and (= notnull 1) (nil? dflt_value))}])


(defn gen-insert-spec [table]
  (->> (name table)
       db/column-metadata
       (map metadata->spec)
       (into {})))


(defn gen-insert-query [{:keys [table cols]}]
  (let [col-keys    (keys cols)
        col-vals (vals cols)
        col-str (str/join ", " (map name col-keys))
        ?-str  (str/join ", " (repeat (count col-keys) "?"))
        sql-str (str "INSERT INTO " (name table) " (" col-str ") VALUES (" ?-str ")")]
    (into [sql-str] col-vals)))

(defn gen-delete-spec [_]
  {:id {:coerce :int
        :require true}})

(defn gen-delete-query [{:keys [table cols]}]
  [(format "DELETE FROM %s WHERE id = ?" (name table)), (:id cols)])

(def commands
  "spec-fn: request -> spec
   query-gen-fn: table -> query vector"
  {:habits {:add {:spec-fn gen-insert-spec
                  :query-gen-fn gen-insert-query}
            :delete {:spec-fn gen-delete-spec
                     :query-gen-fn gen-delete-query}
            ;; :list {:query-gen-fn habits/gen-list-query}
            }
   :tasks {:add {:spec-fn gen-insert-spec
                 :query-gen-fn gen-insert-query}
           :delete {:spec-fn gen-delete-spec
                    :query-gen-fn gen-delete-query}}})

(defn request->query [{:keys [cmd table] :as request} ]
  (let [query-fn (:query-gen-fn (get-in commands [table cmd]))]
    (query-fn request)))

