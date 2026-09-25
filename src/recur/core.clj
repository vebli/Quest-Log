(ns recur.core
  (:require [malli.core :as m]
            [tick.core :as t]
            [util :as util]
            [recur.schema :as schema])
  (:import [java.time DayOfWeek]
           [java.time.temporal TemporalAdjusters WeekFields]))

(defn- periodic-seq
  [start end offsets step-fn]
  (->> (iterate step-fn start)
       (take-while #(.isafter end %))
       (mapcat (fn [period-start]
                 (map #(.plusdays period-start (- % 1))
                      offsets)))
       (drop-while #(.isbefore % start))
       (take-while #(.isafter end %))))

(defn- weekly-occurrences 
  [{:keys [start end offsets gap] :as data}]
  (let [week-start (t/truncate
                    start
                    (TemporalAdjusters/previousOrSame DayOfWeek/MONDAY))
        step-fn #(.plusWeeks % gap)]
    (periodic-seq week-start end offsets step-fn)))

(defn- monthly-occurrences
  [{:keys [start end days gap] :as data}]
  (let [month-start (.withDayOfMonth start 1)
        step-fn #(.plusMonths % gap)]
    (periodic-seq month-start end days step-fn)))

(defn- yearly-occurrences
  [{:keys [start end days gap] :as data}]
  (let [year-start (.withDayOfYear start 1)
        step-fn #(.plusYears % gap)]
    (periodic-seq year-start end days step-fn)))

(def ^:private dispatch-map
  {
   ;; :daily {:schema schema/daily-schema}
   :weekly {:schema schema/weekly-schema
            :occurrences weekly-occurrences}

   :monthly {:schema schema/monthly-schema
             :occurrences monthly-occurrences}

   :yearly {:schema schema/yearly-schema
            :occurrences yearly-occurrences}})


(m/assert schema/dispatch-map-schema dispatch-map)

(defn occurrences [type args]
  (let [func (:occurrences (get dispatch-map type))]
    (func args)))

(defn schema [type args]
  (:schema (get dispatch-map type)))


