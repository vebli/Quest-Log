(ns cli
  (:require [babashka.cli :as cli]))

(defn- sql-type->spec-type [type]
        (get {"INTEGER" :int "TEXT" :string} type type))

(def ^:private alias->table
  {:habit :habits
   :expense :expenses
   :task :tasks})

(defn- metadata->spec [{:keys [name type notnull dflt_value]}]
                 [(keyword name)
                  {:coerce (sql-type->spec-type type)
                   :require (and (= notnull 1) (nil? dflt_value))
                   :alias alias->table}])


(defn- gen-cli-spec [sub-command table]
  (case sub-command
    (:add) (->> table
                 db/column-metadata
                 (map metadata->spec)
                 into {})
    (:delete) nil)) ; TODO


(defn- parse-cli-args [[sub-command table & args]]
  (let [sub-command (keyword sub-command)
        spec (gen-cli-spec sub-command table)]
    (cond
      (not (contains? {:add :delete} sub-command))
      {:ok false
       :error (str "Unknown Command \"" sub-command "\"")}

      (empty? spec)
      {:ok false
       :error (str "Unknown table or alias \"" table "\"")}

      :else
      {:ok true
       :value (cli/parse-args args {:spec spec})})))

(comment
  (gen-cli-spec "habits")
  (db/column-metadata "habits")
  (def add-spec (gen-cli-spec "habits"))
  (cli/parse-args ["--name" "read"] {:spec {:name {:coerce :string :require true}}})
  (parse-cli-args ["add" "habit" "--name" "read"]))
