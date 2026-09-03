(ns domain.recur
  (:require [clojure.spec.alpha :as s]
            [clojure.string :as str]))

(defn- recur? [s]
  (and (string? s)
       (contains? #{"daily" "weekly" "monthly" "yearly"} s)))

(defn- days? [s]
  (let [days (str/split s #",")]
    (and (string? s)
         (-))))


(s/def ::recur recur?)
(s/def ::days days?)
