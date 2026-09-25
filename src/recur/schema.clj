(ns recur.schema
  (:require [schema.util :as util]
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

(def weekly-schema
  (periodic-schema [1 8]))

(def monthly-schema
  [:multi
   {:dispatch #(.lengthOfMonth (t/date (:start %)))}
   [31 (periodic-schema [1 32])]
   [30 (periodic-schema [1 31])]
   [29 (periodic-schema [1 30])]
   [28 (periodic-schema [1 29])]])

(def yearly-schema
  [:multi
   {:dispatch #(.lengthOfYear (t/date (:start %)))}
   [366 (periodic-schema [1 367])]
   [365 (periodic-schema [1 366])]])

(def dispatch-map-schema
  [:map-of
   :keyword
   [:map
    [:schema :any]
    [:occurrences :any]]])
