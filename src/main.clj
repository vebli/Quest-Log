(ns main
  (:require [db :as db]
            [cli :as cli]))

;; (defn attempt [f & args]
;;   (try
;;     {:ok true
;;      :value (apply f args)}
;;     (catch Exception e
;;       {:ok false
;;        :error e})))


(defn -main [args]
  (let [{:keys [ok value error]} (cli/parse-cli-args args)]
    (if ok
      ;; (db/execute! value)
      (println value)
      (println error))))

(comment
  (-main ["add" "habit" "--name" "read"]))
