(ns specs.requstes
  (:require [malli.core :as m]
            [malli.transform :as mt]))

(defn- parse-cmd-string)

(def request
  [:map
   [:cmd :keyword
    {:decode
     {:string #(first %)}}]
   [:table :keyword
    {:decode
     {:string #(second %)}}]
   [:cols]
   [:opts]
   ])

(some->)

(comment
  (def args ["add" "habits" "--name" "read"])
  ())
