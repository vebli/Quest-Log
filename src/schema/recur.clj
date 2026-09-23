(ns schema.recur
  (:require [schema.util :as util]
            [malli.core :as m]
            [tick.core :as t]))


(defn periodic-schema [[start end]]
  [:and
   [:map
    [:start util/local-date]
    [:end util/local-date]
    [:offsets [:vector {:min 1} (util/enum-range start end)]]
    [:gap [:int {:min 1}]]]
   [:fn {:error/message "end must be after start"}
    (fn [{:keys [start end]}] (.isAfter end start))]])


(defmulti recurrence-schema* identity)

(def recurrence-schema (memoize recurrence-schema*))

(defmethod recurrence-schema* :weekly [_]
  (periodic-schema [1 8]))

(defmethod recurrence-schema* :monthly [_]
  [:multi
   {:dispatch #(.lengthOfMonth (t/date (:start %)))}
   [31 (periodic-schema [1 32])]
   [30 (periodic-schema [1 31])]
   [29 (periodic-schema [1 30])]
   [28 (periodic-schema [1 29])]])

(defmethod recurrence-schema* :yearly [_]
  [:multi
   {:dispatch #(.lengthOfYear (t/date (:start %)))}
   [366 (periodic-schema [1 367])]
   [365 (periodic-schema [1 366])]])
