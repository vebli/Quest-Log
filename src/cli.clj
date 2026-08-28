;; https://github.com/babashka/cli#spec
(ns cli
  (:require [babashka.cli :as cli]
            [db :as db]
            [commands :as cmd]))

(def ^:private alias->table
  {:habit :habits
   :expense :expenses
   :task :tasks})

(defn parse-args [[command table & args]]
  (let [command (keyword command)
        table (keyword table)
        table (alias->table table table)
        spec ((:spec-fn (cmd/commands command)) table)]
    (cond
      (not (contains? {:add :delete} command))
      {:ok false
       :error (str "Unknown Command \"" command "\"")}

      (empty? spec)
      {:ok false
       :error (str "Unknown table or alias \"" (name table) "\"")}

      :else
      (let [column-names (db/column-names table) 
            parsed-args (:opts (cli/parse-args args {:spec spec}))
            columns (select-keys parsed-args column-names)
            opts (apply dissoc parsed-args column-names)]
        {:ok true
         :value {:cmd command
                 :table table
                 :cols columns
                 :opts opts}}))))

(comment
  (def columns (select-keys parsed-args column-names))
  (db/column-metadata "habits")
  (select-keys (:opts (cli/parse-args ["--name" "read"] {:spec {:name {:coerce :string :require true}}})) (db/column-names "habits"))
  (parse-args ["add" "habit" "--name" "read" "--test"]))
