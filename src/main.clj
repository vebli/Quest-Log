(ns main
  (:require [db :as db]
            [decode :as decode]
            [dispatch :as dispatch]
            [malli.core :as m]))

(add-tap (bound-fn* println))

(defn -main [args]
  (let [{:keys [cmd table cols opts] :as request} (decode/parse-cli-args args)
        schema (dispatch/schema request)
        validation-result (m/validate schema request)
        query (dispatch/query request)]
    (if (true? validation-result)
      (dispatch/display request (db/execute! query))
      (m/explain validation-result))))

(db/column-metadata :habits)
(comment
  (def args ["add" "habit" "--name" "read" "--description" "read book" "--hi"])
  (def request (decode/parse-cli-args args))
  (def schema (dispatch/schema request))
  (def validation-result (m/validate schema request))
  (def query (dispatch/query request))
  (-main args)
  )



