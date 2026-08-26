;; https://github.com/babashka/cli#spec

(ns cli
  (:require [babashka.cli :as cli]
            [db :as db]))

(defn- sql-type->spec-type [type]
        (get {"INTEGER" :int "TEXT" :string} type type))

(def ^:private alias->table
  {"habit" "habits"
   "expense" "expenses"
   "task" "tasks"})


(defn- metadata->spec [{:keys [name type notnull dflt_value]}]
  [(keyword name)
   {:coerce (sql-type->spec-type type)
    :require (and (= notnull 1) (nil? dflt_value))}])


(defn- gen-cli-spec [command table]
  (case (name command)
    ("add") (->> (name table)
                 db/column-metadata
                 (map metadata->spec)
                 (into {}))
    ("delete") nil)) ; TODO

(defn- parse-cli-args [[command table & args]]
  (let [command (keyword command)
        table (keyword (alias->table table table))
        spec (gen-cli-spec command table)]
    (cond
      (not (contains? {:add :delete} command))
      {:ok false
       :error (str "Unknown Command \"" command "\"")}

      (empty? spec)
      {:ok false
       :error (str "Unknown table or alias \"" (name table) "\"")}

      :else
      {:ok true
       :value (into {:cmd command :table table} (cli/parse-args args {:spec spec}))})))

(comment
  (gen-cli-spec "add" "habits")
  (db/column-metadata "habits")
  (def add-spec (gen-cli-spec "habits"))
  (cli/parse-args ["--name" "read"] {:spec {:name {:coerce :string :require true}}})
  (parse-cli-args ["add" "habit" "--name" "read"])
  (gen-cli-spec ))
