(ns specs.requstes
  (:require [malli.core :as m]))


;; (def request
;;   [:map
;;    [:cmd :keyword]
;;    [:table :keyword]
;;    {:fn (fn [{:keys [cmd table]}]
;;           (let [table (keyword table)
;;                 cmd (keyword cmd)]
;;             (and (contains? ... table)
;;                  (contains? ... ))))}])
