;; https://github.com/babashka/cli#spec
(ns cli
  (:require [babashka.cli :as cli]
            [db :as db]
            [commands :as cmd]))

(defn alias->table [alias]
  (let [m {:habit :habits
           :expense :expenses
           :task :tasks}]
    (m alias alias)))



(defn parse-args [[cmd table & rest]]
        ())

(defn parse-args [[command table & args]]
  (let [command (keyword command)
        table (alias->table (keyword table))
        table-exists (contains? cmd/commands table)
        command-spec (get (get cmd/commands table) command)
        command-exists-for-table (contains? (get cmd/commands table) command)]
    (cond
      (not table-exists)
      {:ok false
       :error (format "Unknown table or alias \"%s\"" table)}

      (not command-exists-for-table)
      {:ok false
       :error (format "Table %s does not support command \"%s\"" table command)}

      :else
      (let [column-names (db/column-names table)
            spec ((:spec-fn command-spec) table)
            parsed-args (:opts (cli/parse-args args {:spec spec}))
            columns (select-keys parsed-args column-names)
            opts (apply dissoc parsed-args column-names)]
        {:ok true
         :value {:cmd command
                 :table table
                 :cols columns
                 :opts opts}}))))

(defmulti f (fn [x] x))
(defmethod f :default [x] nil)
(f 1)
(f 1)
(defmethod f [(= true (int? x))]
  [x] (inc x))

(comment
  (def columns (select-keys parsed-args column-names))
  (db/column-metadata "habits")
  (select-keys (:opts (cli/parse-args ["--name" "read"] {:spec {:name {:coerce :string :require true}}})) (db/column-names "habits"))
  (cli/parse-args ["add" "habit" "--name" "--name=mo:tu:we" "more" "--r"]))

