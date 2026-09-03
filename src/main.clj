(ns main
  (:require [db :as db]
            [cli :as cli]
            [commands :as cmd]))

(add-tap (bound-fn* println))

;; (defn -main [args]
;;   (let [{:keys [ok value error]} (cli/parse-args args)]
;;     (if ok
;;       (db/execute! (cmd/request->query value))
;;       (println error))))

(comment
  (def args ["add" "habit" "--name" "write" "--title"])
  (def args ["delete" "habit" "--id" "2"])
  (def query (cmd/request->query (:value (cli/parse-args args))))
  (-main args))

