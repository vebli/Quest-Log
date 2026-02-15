;; https://github.com/babashka/cli
;; https://github.com/babashka/cli#spec

(ns main)
(require '[babashka.cli :as cli]
         '[db])

(def entity->table
  {"habit" "habits"
   "expense" "expenses"
   "task" "tasks"})

(def sql-type->spec-type
  {"INTEGER" :int
   "TEXT" :string})

(defn maybe [f]
  (fn [v]
    (or (f v) v)))

(defn gen-spec [table]
  (let [columns (->> table
                     (db/column-metadata)
                     (map #(select-keys % [:name :type :notnull]))
                     (map #(update % :type (maybe sql-type->spec-type)))
                     (map #(update % :name (maybe keyword)))
                     )]
    columns))

(comment
  (def *command-line-args* ["add" "--table" "habits" "--name" "read"])
  (cli/parse-args *command-line-args* {:spec {:name :long}}))

(def dispatch-table
  [{:cmds ["add"] :fn db/add :spec }
   {:cmds ["delete"] :fn db/delete}
   {:cmds [] :fn (fn [_] (println "Usage: add habit --name <str>"))}])

(defn -main [args]
  (cli/dispatch dispatch-table args))

(def *command-line-args* ["delete" "--table" "habits" "--name" "read"])
(-main *command-line-args*)

