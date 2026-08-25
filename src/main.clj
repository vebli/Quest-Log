;; https://github.com/babashka/cli
;; https://github.com/babashka/cli#spec
(ns main)
(require '[babashka.cli :as cli]
         '[db])



(defn- sql-type->spec-type [type]
        (get {"INTEGER" :int "TEXT" :string} type type))

(def alias->table
  {:habit :habits
   :expense :expenses
   :task :tasks})

(defn- metadata->spec [{:keys [name type notnull dflt_value]}]
                 [(keyword name)
                  {:coerce (sql-type->spec-type type)
                   :require (and (= notnull 1) (nil? dflt_value))
                   :alias alias->table}])

(defn gen-spec [table]
  (let [table-metadata (db/column-metadata table)]
    (into {} (map metadata->spec table-metadata))))



(defn- parse-cli-args [[sub-command table-or-alias & args]]
  (let [add-spec (gen-spec table-or-alias)]
    (case sub-command
      "add" (cli/parse-args args {:spec add-spec}))))

(comment
  (gen-spec "habits")
  (db/column-metadata "habits")
  (def add-spec (gen-spec "habits"))
  (cli/parse-args ["--name" "read"] {:spec {:name {:coerce :string :require true}}})
  (parse-cli-args ["add" "habit" "--name" "read"]))

