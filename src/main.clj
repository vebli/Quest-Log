;; https://github.com/babashka/cli
;; https://github.com/babashka/cli#spec
(ns main)
(require '[babashka.cli :as cli]
         '[db])

;
; (declare parse-args)
;
; (defn -main [args]
;   (parse-args args))

(def cli->table
  {"habit" "habits"
   "expense" "expenses"
   "task" "tasks"})


(defn- sql-type->spec-type [type]
        (get {"INTEGER" :int "TEXT" :string} type type))

(defn- gen-spec [table]
  "Reads table metadata and extracts column name, type and if it is nullable"
  (->> table
         (db/column-metadata)
         (map #(select-keys % [:name :type :notnull]))
         (map #(update % :type sql-type->spec-type))
         ))

(comment
  (def *command-line-args* ["add" "--table" "habits" "--name" "read"])
  (cli/parse-args *command-line-args* {:spec {:name :long}}))

(def dispatch-table
  [{:cmds ["add"] :fn db/add }
   {:cmds ["delete"] :fn db/delete}
   {:cmds [] :fn (fn [_] (println "Usage: add habit --name <str>"))}])

(defn -main [args]
  (cli/dispatch dispatch-table args))

(comment (def *command-line-args* ["delete" "--table" "habits" "--name" "read"])
         (cli/dispatch dispatch-table *command-line-args*)
(-main *command-line-args*)

