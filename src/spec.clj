(ns spec 
  (:require [clojure.spec.alpha :as s]
            [commands :as cmd]))

(s/def ::cmd keyword?) 
(s/def ::table keyword?)
(s/def ::val (s/or number? string?))
(s/def ::cols (s/map-of keyword? ::val)) 
(s/def ::opts (s/map-of keyword? (s/nilable ::val))) 

(s/def ::request
  (s/and (s/keys :req [::cmd ::table ::cols ::opts])
         (fn [{:keys [cmd table]}]
           (let [cmd (keyword cmd)
                 table (keyword table)]
             (and (contains? cmd/commands table)
                  (contains? (get cmd/commands table) cmd))))))

